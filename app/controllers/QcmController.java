package controllers;

import controllers.dao.*;
import models.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin;
import views.html.qcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


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
        return ok(qcm.render(buildQcm(), qForm, score, null, QcmDao.listAll(), answeredQuestIds));
    }

    public static List<Question> buildQcm() {
        if (evaluatedQcm != null)
            return QuestionDao.listByQcmId(evaluatedQcm.id);
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
        return ok(qcm.render(buildQcm(), filledForm, score, "EVAL", null, answeredQuestIds));
    }

    public static Result createQcm() {
        Form<Qcm> filledForm = qcmForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(admin.render(null, null, "QCM", QcmDao.listAll(), qcmForm, null));
        }
        Qcm qcm = filledForm.get();
        QcmDao.save(qcm);
        return ok(admin.render(QuestionDao.listByQcmId(qcm.id), qForm, "QST", null, null, qcm));
    }

    public static Result deleteQcm(Long id) {
        QcmDao.delete(id);
        List<Qcm> qcms = QcmDao.listAll();
        if (qcms == null || qcms.size() == 0)
            return ok(admin.render(null, null, "", new ArrayList<Qcm>(), null, null));
        else
            return ok(admin.render(null, null, "", qcms, qcmForm, null));
    }

    public static Result editQcm(Long id) {
        return ok(admin.render(QuestionDao.listByQcmId(id), qForm, "QST", null, null, QcmDao.findById(id)));
    }

    public static Result score() {
        score = 0L;
        List<Response> responses = ResponseDao.listByUser(session().get("user"));
        for (Response response : responses) {
            Choice c = response.choice;
            if ("OK".equals(c.status))
                score += 1;
        }
        return ok(qcm.render(buildQcm(), qForm, score, "EVAL", QcmDao.listAll(), answeredQuestIds));
    }

    /**
     * Action appelée lorsque l'utilisateur souhaite répondre à un QCM.
     * @param id l'identifiant du QCM auquel l'utilisateur veut répondre.
     * @return appelle le rendu de la page du QCM en question.
     */
    public static Result tester(Long id) {
        initQcmForTest(id);
        return ok(qcm.render(buildQcm(), qForm, score, "EVAL", QcmDao.listAll(), answeredQuestIds));
    }

    /**
     * Remet le score à 0, efface les anciennes réponses et initialise la valeur de référence pour le qcm.
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
