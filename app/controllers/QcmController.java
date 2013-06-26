package controllers;

import controllers.dao.*;
import models.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utils.CategoryEnum;
import utils.Utils;
import views.html.admin;
import views.html.qcm;
import xmlbeans.Questionnaire;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private static BigDecimal score = BigDecimal.ZERO;

    @Security.Authenticated(SecurityManager.class)
    public static Result index() {
        return ok(qcm.render(buildQcm(), qForm, null, null, QcmDao.listAll(), answeredQuestIds, Utils.getConnectedUser()));
    }

    public static List<Question> buildQcm() {
        if (evaluatedQcm != null){
            List<Question> l = QuestionDao.listByQcmId(evaluatedQcm.id);
            Utils.shuffleList(l);
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
        Qcm q = QuestionDao.getQuestion(uAns.questionId).qcm;
        List<Question> questions = q.questions;
        for(Long qId : answeredQuestIds){
            questions.remove(QuestionDao.getQuestion(qId));
        }
        return ok(qcm.render(questions, filledForm, null, "EVAL", null, answeredQuestIds, Utils.getConnectedUser()));
    }

    public static Result createQcm() {
        Form<Qcm> filledForm = qcmForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(admin.render(null, null, "QCM", QcmDao.listAll(), qcmForm, null, Utils.getConnectedUser()));
        }
        Qcm qcm = filledForm.get();
        QcmDao.saveOrUpdate(qcm);
        return ok(admin.render(QuestionDao.listByQcmId(qcm.id), qForm, "QST", null, null, qcm, Utils.getConnectedUser()));
    }

    public static Result majQcm(){
        Form<Qcm> filledForm = qcmForm.bindFromRequest();
        if(filledForm.hasErrors()){
            return badRequest(admin.render(null, null, "QCM", QcmDao.listAll(), filledForm, null, Utils.getConnectedUser()));
        }
        Qcm q = filledForm.get();
        QcmDao.saveOrUpdate(q);
        return ok(admin.render(QuestionDao.listByQcmId(q.id), qForm, "QST", null, filledForm, QcmDao.findById(q.id), Utils.getConnectedUser()));
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
        return ok(admin.render(QuestionDao.listByQcmId(id), qForm, "QST", null, qcmForm.fill(QcmDao.findById(id)), QcmDao.findById(id), Utils.getConnectedUser()));
    }

    public static Result updateQcm(Long id){
        Form<Qcm> filledForm = qcmForm.bindFromRequest();
        if(filledForm.hasErrors()){
            return badRequest(admin.render(QuestionDao.listByQcmId(id), qForm, "QST", null, filledForm, QcmDao.findById(id), Utils.getConnectedUser()));
        }else{
            Qcm qcm = filledForm.get();
            QcmDao.saveOrUpdate(qcm);
            return ok(admin.render(QuestionDao.listByQcmId(id), qForm, "QST", null, null, qcm, Utils.getConnectedUser()));
        }
    }

    public static Result score() {
        BigDecimal maxScore = evaluatedQcm.maxScore;
        score = BigDecimal.ZERO;
        List<Response> responses = ResponseDao.listByUser(session().get("user"));
        for (Response response : responses) {
            Choice c = response.choice;
            if (Utils.STATUS_OK.equals(c.status))
                score = score.add(BigDecimal.ONE);
        }
        BigDecimal resultScore = score.multiply(BigDecimal.valueOf(20)).divide(maxScore, 1, RoundingMode.HALF_UP);
        return ok(qcm.render(new ArrayList<Question>(), qForm, resultScore.toString(), "EVAL", QcmDao.listAll(), answeredQuestIds, Utils.getConnectedUser()));
    }

    /**
     * Action appelée lorsque l'utilisateur souhaite répondre à un QCM.
     *
     * @param id l'identifiant du QCM auquel l'utilisateur veut répondre.
     * @return appelle le rendu de la page du QCM en question.
     */
    public static Result tester(Long id) {
        initQcmForTest(id);
        return ok(qcm.render(buildQcm(), qForm, null, "EVAL", QcmDao.listAll(), answeredQuestIds, Utils.getConnectedUser()));
    }

    public static Result exportQcm(Long qcmId) throws DatatypeConfigurationException, JAXBException, IOException {
        Qcm qcm = QcmDao.findById(qcmId);
        Questionnaire xmlQuestionnaire = Utils.convertDbQuestToXmlQuest(qcm);
        JAXBContext jaxbContext = JAXBContext.newInstance(xmlQuestionnaire.getClass());
        Marshaller marsh = jaxbContext.createMarshaller();
        marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        File f = File.createTempFile("export-" + xmlQuestionnaire.getTitre().replaceAll("/", "-").replaceAll(" ", "")+"-", ".xml");
        marsh.marshal(xmlQuestionnaire, new FileOutputStream(f));
        response().setHeader("Content-Disposition", "attachment; filename=" + f.getName());
        response().setContentType("application/x-download");
        return ok(f);
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

    public static Result calculateMaxScore(Long qcmId){
        Qcm qcm = QcmDao.findById(qcmId);
        qcm.maxScore = BigDecimal.ZERO;
        List<Question> questions = qcm.questions;
        for(Question q: questions){
            List<Choice> choices = q.possibleResp;
            for(Choice choice: choices){
                if(Utils.STATUS_OK.equals(choice.status)){
                    qcm.maxScore = qcm.maxScore.add(BigDecimal.ONE);
                }
            }
        }
        QcmDao.saveOrUpdate(qcm);
        return redirect(routes.Admin.index());
    }

    public static List<Domain> listDomains(){
        return DomainDao.listAll();
    }

    private static void persistXmlQuestToDbQuest(Questionnaire questionnaire) {
        Qcm qcm = new Qcm();
        qcm.name = questionnaire.getTitre();
        qcm.category = ".NET".equals(questionnaire.getCategorie())? CategoryEnum.DOT_NET:CategoryEnum.JAVA;
        qcm.questions = Utils.convertXmlQuestionsToDbQuest(questionnaire.getQuestion(), qcm);
        QcmDao.saveOrUpdate(qcm);
    }

    /**
     * Remet le score à 0, efface les anciennes réponses et initialise la valeur de référence pour le qcm.
     *
     * @param id l'identifiant du QCM auquel l'utilisateur veut répondre.
     */
    private static void initQcmForTest(Long id) {
        score = BigDecimal.ZERO;
        answeredQuestIds.clear();
        evaluatedQcm = QcmDao.findById(id);
        User user = UserDao.findUserByMail(session().get("user"));
        ResponseDao.deleteRespForUserAndQcm(evaluatedQcm, user);
    }
}
