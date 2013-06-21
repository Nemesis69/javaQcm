package controllers;

import controllers.dao.*;
import models.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import tyrex.util.Configuration;
import views.html.admin;
import views.html.qcm;
import xmlbeans.ObjectFactory;
import xmlbeans.Questionnaire;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 29/05/13
 * Time: 10:23
 * To change this template use File | Settings | File Templates.
 */
public class QcmController extends Controller {

    private static Form qForm = Form.form(UserAnswer.class);
    private static Form<Qcm> qcmForm = Form.form(Qcm.class);
    private static Qcm evaluatedQcm;
    private static List<Long> answeredQuestIds = new ArrayList<Long>();
    private static Long score = Long.valueOf(0);

    @Security.Authenticated(SecurityManager.class)
    public static Result index() {
        return ok(qcm.render(buildQcm(), qForm, score, null, QcmDao.listAll(), answeredQuestIds, Utils.getConnectedUser()));
    }

    public static List<Question> buildQcm() {
        if (evaluatedQcm != null){
            List<Question> l = QuestionDao.listByQcmId(evaluatedQcm.id);
            Random rand = new Random();
            rand.setSeed(System.currentTimeMillis());
            Collections.shuffle(l, rand);
            return l;
        }
        else
            return new ArrayList<Question>();
    }

    public static Result validateResponses() {
        Form<UserAnswer> filledForm = qForm.bindFromRequest();
        UserAnswer uAns = filledForm.get();
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] checkedVal = map.get("selectedChoices");
        String[] questionIds = map.get("questionId");
        List<String> l = Arrays.asList(checkedVal);
        for (String s : l) {
            Response response = new Response();
            response.choice = ChoiceDao.finder.byId(Long.valueOf(s));
            response.question = QuestionDao.getQuestion(Long.valueOf(questionIds[0]));
            response.user = UserDao.findUserByMail(session().get("user"));
            ResponseDao.saveResponse(response);
            if (!answeredQuestIds.contains(response.question))
                answeredQuestIds.add(response.question.id);
        }
        return ok(qcm.render(buildQcm(), filledForm, score, "EVAL", null, answeredQuestIds, Utils.getConnectedUser()));
    }

    public static Result createQcm() {
        Form<Qcm> filledForm = qcmForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(admin.render(null, null, "QCM", QcmDao.listAll(), qcmForm, null, Utils.getConnectedUser()));
        }
        Qcm qcm = filledForm.get();
        QcmDao.save(qcm);
        return ok(admin.render(QuestionDao.listByQcmId(qcm.id), qForm, "QST", null, null, qcm, Utils.getConnectedUser()));
    }

    public static Result deleteQcm(Long id) {
        QcmDao.delete(id);
        List<Qcm> qcms = QcmDao.listAll();
        if (qcms == null || qcms.size() == 0)
            return ok(admin.render(null, null, "", new ArrayList<Qcm>(), null, null, Utils.getConnectedUser()));
        else
            return ok(admin.render(null, null, "", qcms, qcmForm, null, Utils.getConnectedUser()));
    }

    public static Result editQcm(Long id) {
        return ok(admin.render(QuestionDao.listByQcmId(id), qForm, "QST", null, null, QcmDao.findById(id), Utils.getConnectedUser()));
    }

    public static Result score() {
        score = 0L;
        List<Response> responses = ResponseDao.listByUser(session().get("user"));
        for (Response response : responses) {
            Choice c = response.choice;
            if ("OK".equals(c.status))
                score += 1;
        }
        return ok(qcm.render(buildQcm(), qForm, score, "EVAL", QcmDao.listAll(), answeredQuestIds, Utils.getConnectedUser()));
    }

    /**
     * Action appelée lorsque l'utilisateur souhaite répondre à un QCM.
     *
     * @param id l'identifiant du QCM auquel l'utilisateur veut répondre.
     * @return appelle le rendu de la page du QCM en question.
     */
    public static Result tester(Long id) {
        initQcmForTest(id);
        return ok(qcm.render(buildQcm(), qForm, score, "EVAL", QcmDao.listAll(), answeredQuestIds, Utils.getConnectedUser()));
    }

    public static Result exportQcm(Long qcmId) throws DatatypeConfigurationException, JAXBException, IOException {
        Qcm qcm = QcmDao.findById(qcmId);
        Questionnaire xmlQuestionnaire = convertDbQuestToXmlQuest(qcm);
        JAXBContext jaxbContext = JAXBContext.newInstance(xmlQuestionnaire.getClass());
        Marshaller marsh = jaxbContext.createMarshaller();
        marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        File f = File.createTempFile("export-" + xmlQuestionnaire.getTitre().replaceAll("/", "-").replaceAll(" ", "")+"-", ".xml");
        marsh.marshal(xmlQuestionnaire, new FileOutputStream(f));
        response().setHeader("Content-Disposition", "attachment; filename=" + f.getName());
        response().setContentType("application/x-download");
        return ok(f);
    }

    private static Questionnaire convertDbQuestToXmlQuest(Qcm qcm) throws DatatypeConfigurationException {
        List<Question> questions = qcm.questions;
        ObjectFactory objectFactory = new ObjectFactory();
        Questionnaire xmlQuestionnaire = objectFactory.createQuestionnaire();
        xmlQuestionnaire.setTitre(qcm.name);
        xmlQuestionnaire.setCategorie(qcm.category);
        xmlQuestionnaire.setDateCreation(DatatypeFactory.newInstance().newXMLGregorianCalendar(getGregorianCalendar()));
        xmlQuestionnaire.setVersion(Byte.valueOf("1"));
        xmlQuestionnaire.getQuestion().addAll(generateXmlQuestionListFromDb(qcm.questions));
        return xmlQuestionnaire;
    }

    public static Result importQcm() {
        return ok(admin.render(null, null, "IMP", null, null, null, Utils.getConnectedUser()));
    }

    public static Result createQcmFromXml() throws JAXBException {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("xml");
        if (filePart != null) {
            String fileName = filePart.getFilename();
            String contentType = filePart.getContentType();
            File file = filePart.getFile();
            JAXBContext jaxbContext = JAXBContext.newInstance(Questionnaire.class);
            Unmarshaller unMarsh = jaxbContext.createUnmarshaller();
            Questionnaire questionnaire = (Questionnaire)unMarsh.unmarshal(file);
            persistXmlQuestToDbQuest(questionnaire);
            flash("success", "QCM bien importé");
            return redirect(routes.Admin.index());
        } else {
            flash("error", "Pas de fichier en entrée de l'import");
            return redirect(routes.Admin.index());
        }
    }

    private static void persistXmlQuestToDbQuest(Questionnaire questionnaire) {
        Qcm qcm = new Qcm();
        qcm.name = questionnaire.getTitre();
        qcm.category = questionnaire.getCategorie();
        qcm.questions = convertXmlQuestionsToDbQuest(questionnaire.getQuestion(), qcm);
        QcmDao.save(qcm);
    }

    private static List<Question> convertXmlQuestionsToDbQuest(List<Questionnaire.Question> questions, Qcm qcm) {
        List<Question> res = new ArrayList<Question>();
        for(Questionnaire.Question q : questions){
            Question dbQust = new Question();
            dbQust.text = q.getIntitule();
            dbQust.qcm = qcm;
            res.add(dbQust);
            dbQust.possibleResp = convertXmlRespToDbResp(q.getReponse(), dbQust);
        }
        return res;
    }

    private static List<Choice> convertXmlRespToDbResp(List<Questionnaire.Question.Reponse> responses, Question dbQust) {
        List<Choice> res = new ArrayList<Choice>();
        for(Questionnaire.Question.Reponse r : responses){
            Choice c = new Choice();
            c.libelle = r.getLibelle();
            c.status = "true".equals(r.getSolution())?"OK":"KO";
            res.add(c);
        }
        return res;
    }

    private static GregorianCalendar getGregorianCalendar() {
        GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTime(new Date());
        return gCal;
    }

    private static List<Questionnaire.Question> generateXmlQuestionListFromDb(List<Question> questions) throws DatatypeConfigurationException {
        List<Questionnaire.Question> xmlQuestions = new ArrayList<Questionnaire.Question>();
        for (Question q : questions) {
            Questionnaire.Question xmlQuest = new Questionnaire.Question();
            xmlQuest.setIntitule(q.text);
            xmlQuest.setPonderation(Byte.valueOf("1"));
            xmlQuest.setVersion(Byte.valueOf("1"));
            xmlQuest.getReponse().addAll(generateXmlResponseFromDb(q.possibleResp));
            xmlQuestions.add(xmlQuest);
        }
        return xmlQuestions;
    }

    private static List<Questionnaire.Question.Reponse> generateXmlResponseFromDb(List<Choice> possibleResp) throws DatatypeConfigurationException {
        List<Questionnaire.Question.Reponse> xmlResponses = new ArrayList<Questionnaire.Question.Reponse>();
        for (Choice c : possibleResp) {
            Questionnaire.Question.Reponse resp = new Questionnaire.Question.Reponse();
            resp.setDateCreation(DatatypeFactory.newInstance().newXMLGregorianCalendar(getGregorianCalendar()));
            resp.setDateModification(DatatypeFactory.newInstance().newXMLGregorianCalendar(getGregorianCalendar()));
            resp.setLibelle(c.libelle);
            resp.setSolution("OK".equals(c.status) ? "true" : "false");
            resp.setVersion(Byte.valueOf("1"));
            xmlResponses.add(resp);
        }
        return xmlResponses;
    }

    /**
     * Remet le score à 0, efface les anciennes réponses et initialise la valeur de référence pour le qcm.
     *
     * @param id l'identifiant du QCM auquel l'utilisateur veut répondre.
     */
    private static void initQcmForTest(Long id) {
        score = 0L;
        answeredQuestIds.clear();
        evaluatedQcm = QcmDao.findById(id);
        User user = UserDao.findUserByMail(session().get("user"));
        ResponseDao.deleteRespForUserAndQcm(evaluatedQcm, user);
    }
}
