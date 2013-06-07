package controllers;

import models.Question;
import models.Response;
import play.data.Form;
import play.mvc.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 07/06/13
 * Time: 10:36
 * To change this template use File | Settings | File Templates.
 */
public class ResponseManager extends Controller {

    private static Question editedQuestion;

    private static Form<Response> rForm = Form.form(Response.class);

    public static Result index(){
        return ok(views.html.possibleResponse.render(editedQuestion, rForm, ResponseController.listByQuestId(editedQuestion.id)));
    }

    public static Result editQuestion(Long id){
        setEditedQuestion(id);
        return ok(views.html.possibleResponse.render(editedQuestion, rForm, ResponseController.listByQuestId(editedQuestion.id)));
    }

    public static Result addResponses(Long questionId){
        setEditedQuestion(questionId);
        Form<Response> filledForm = rForm.bindFromRequest();
        if(filledForm.hasErrors()){
            return badRequest(views.html.possibleResponse.render(editedQuestion, filledForm, ResponseController.listByQuestId(editedQuestion.id)));
        }else{
            Response r = filledForm.get();
            r.questionRef = editedQuestion;
            ResponseController.save(r);
            return redirect(routes.ResponseManager.index());
        }
    }

    private static Question setEditedQuestion(Long questionId) {
        return (editedQuestion == null || editedQuestion.id != questionId) ? editedQuestion = QuestionController.getQuestion(questionId) : editedQuestion;
    }

    public static Result deleteResponse(Long id){
        ResponseController.deleteResponse(id);
        return ok(views.html.possibleResponse.render(editedQuestion, rForm, ResponseController.listByQuestId(editedQuestion.id)));
    }

}
