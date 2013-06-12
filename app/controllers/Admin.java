package controllers;


import models.Question;
import play.data.Form;
import play.mvc.*;


/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 29/05/13
 * Time: 10:18
 * To change this template use File | Settings | File Templates.
 */
public class Admin extends Controller{

    private static Form<Question> qForm = Form.form(Question.class);

    @Security.Authenticated(SecurityManager.class)
    public static Result index(){
        return  ok(views.html.admin.render(QuestionDao.listAll(), qForm));
    }
}
