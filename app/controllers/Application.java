package controllers;

import models.Question;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    static Form<Question> qForm = Form.form(Question.class);
    public static Result index() {
        return ok(index.render("QCM d'auto-évaluation en JAVA / JEE"));
    }

    public static Result createQuestion(){
        Form<Question> filledForm = qForm.bindFromRequest();
        if(filledForm.hasErrors()){
            return badRequest(admin.render(QuestionDao.listAll(), filledForm));
        }
        QuestionDao.createQuestion(filledForm.get());
        return redirect(routes.Admin.index());
    }

    public static Result editQuestion(Long id){
        return redirect(routes.ChoiceManager.index());
    }


    public static Result deleteQuestion(Long id){
        if(null == id ){
            return badRequest(admin.render(QuestionDao.listAll(), qForm));
        } else{
            QuestionDao.delete(id);
            return redirect(routes.Admin.index());
        }
    }
}
