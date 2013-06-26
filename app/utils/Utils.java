package utils;

import controllers.dao.DomainDao;
import controllers.dao.UserDao;
import models.*;
import play.mvc.Controller;
import xmlbeans.ObjectFactory;
import xmlbeans.Questionnaire;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: NBE08314
 * Date: 18/06/13
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */
public class Utils extends Controller{

    public static final String STATUS_OK = "OK";

    public static final String STATUS_KO = "KO";

    public static User getConnectedUser() {
        User requestUser = UserDao.findUserByMail(Controller.request().username());
        return requestUser!=null?requestUser:UserDao.findUserByMail(session().get("user"));
    }

    public static String getMd5String(User user) throws NoSuchAlgorithmException {
        MessageDigest msgDig = MessageDigest.getInstance("MD5");
        msgDig.update(user.password.getBytes(Charset.forName("UTF8")));
        byte[] hash = msgDig.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hash.length; ++i) {
            sb.append(Integer.toHexString((hash[i] & 0xFF) | 0x100).substring(1,3));
        }
        return sb.toString();
    }

    public static Questionnaire convertDbQuestToXmlQuest(Qcm qcm) throws DatatypeConfigurationException {
        List<Question> questions = qcm.questions;
        ObjectFactory objectFactory = new ObjectFactory();
        Questionnaire xmlQuestionnaire = objectFactory.createQuestionnaire();
        xmlQuestionnaire.setTitre(qcm.name);
        xmlQuestionnaire.setCategorie(qcm.category.getCategory());
        xmlQuestionnaire.setDateCreation(DatatypeFactory.newInstance().newXMLGregorianCalendar(getGregorianCalendar()));
        xmlQuestionnaire.setVersion(Byte.valueOf("1"));
        xmlQuestionnaire.getQuestion().addAll(generateXmlQuestionListFromDb(qcm.questions));
        return xmlQuestionnaire;
    }

    public static List<Question> convertXmlQuestionsToDbQuest(List<Questionnaire.Question> questions, Qcm qcm) {
        List<Question> res = new ArrayList<Question>();
        for(Questionnaire.Question q : questions){
            Question dbQust = new Question();
            dbQust.text = q.getIntitule();
            dbQust.qcm = qcm;
            dbQust.domain = qcm.category.equals(CategoryEnum.JAVA) ? DomainDao.getByLibelle("JAVA") : DomainDao.getByLibelle("DOT_NET");
            if(dbQust.domain != null)
                dbQust.domainIdValue = dbQust.domain.id.toString();
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
            c.status = "true".equals(r.getSolution())?Utils.STATUS_OK:Utils.STATUS_KO;
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
            resp.setSolution(Utils.STATUS_OK.equals(c.status) ? "true" : "false");
            resp.setVersion(Byte.valueOf("1"));
            xmlResponses.add(resp);
        }
        return xmlResponses;
    }

    public static void shuffleList(List<Question> l) {
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        Collections.shuffle(l, rand);
    }
}
