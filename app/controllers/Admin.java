package controllers;


import controllers.dao.QcmDao;
import controllers.dao.QuestionDao;
import models.Qcm;
import models.Question;
import play.data.Form;
import play.mvc.*;
import views.html.admin;

import java.util.ArrayList;


/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 29/05/13
 * Time: 10:18
 * To change this template use File | Settings | File Templates.
 */
public class Admin extends Controller{

    private static Form<Question> qForm = Form.form(Question.class);
    static Form<Qcm> qcmForm = Form.form(Qcm.class);

    @Security.Authenticated(SecurityManager.class)
    public static Result index(){
        return  ok(views.html.admin.render(null, null, "", QcmDao.listAll(), null, null));
    }

    public static Result createQcm(){
        return ok(admin.render(QuestionDao.listAll(), qForm, "QCM", QcmDao.listAll(), qcmForm, null));
    }

    public static Result retourQuestions(Long qcmId){
        return ok(admin.render(QuestionDao.listByQcmId(qcmId), qForm, "QST", null, null, QcmDao.findById(qcmId)));
    }
}
