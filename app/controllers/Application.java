package controllers;

import controllers.dao.QcmDao;
import controllers.dao.QuestionDao;
import models.Qcm;
import models.Question;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin;
import views.html.index;

public class Application extends Controller {

    static Form<Question> qForm = Form.form(Question.class);
    static Form<Qcm> qcmForm = Form.form(Qcm.class);

    public static Result index() {
        return ok(index.render("QCM d'auto-évaluation en JAVA / JEE"));
    }

    public static Result createQuestion(Long qcmId) {
        Qcm qcm = QcmDao.findById(qcmId);
        Form<Question> filledForm = qForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(admin.render(QuestionDao.listByQcmId(qcmId), filledForm, "QST", null, null, qcm));
        }
        Question q = filledForm.get();
        q.qcm = qcm;
        QuestionDao.createQuestion(q);
        return ok(admin.render(QuestionDao.listByQcmId(qcmId), qForm, "QST", null, null, qcm));
    }

    public static Result editQuestion(Long id) {
        return redirect(routes.ChoiceManager.index());
    }


    public static Result deleteQuestion(Long id, Long qcmId) {
        if (null == id) {
            return badRequest(admin.render(QuestionDao.listByQcmId(qcmId), qForm, "", null, null, QcmDao.findById(qcmId)));
        } else {
            QuestionDao.delete(id);
            return ok(admin.render(QuestionDao.listByQcmId(qcmId), qForm, "QST", null, null, QcmDao.findById(qcmId)));
        }
    }
}
