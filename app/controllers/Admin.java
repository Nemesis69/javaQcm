package controllers;


import controllers.dao.QcmDao;
import controllers.dao.QuestionDao;
import models.Qcm;
import models.Question;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.Utils;
import views.html.admin;


/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 29/05/13
 * Time: 10:18
 * To change this template use File | Settings | File Templates.
 */
public class Admin extends Controller {

    static Form<Qcm> qcmForm = Form.form(Qcm.class);
    private static Form<Question> qForm = Form.form(Question.class);

    @Security.Authenticated(SecurityManager.class)
    public static Result index() {
        return ok(views.html.admin.render(null, null, "", QcmDao.listAll(), null, null, Utils.getConnectedUser()));
    }


    public static Result createQcm() {
        return ok(admin.render(QuestionDao.listAll(), qForm, "QCM", QcmDao.listAll(), qcmForm, null, Utils.getConnectedUser()));
    }

    public static Result retourQuestions(Long qcmId) {
        return ok(admin.render(QuestionDao.listByQcmId(qcmId), qForm, "QST", null, qcmForm.fill(QcmDao.findById(qcmId)), QcmDao.findById(qcmId), Utils.getConnectedUser()));
    }
}
