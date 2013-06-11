package controllers;

import models.Choice;
import models.Question;
import play.data.Form;
import play.mvc.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 07/06/13
 * Time: 10:36
 * To change this template use File | Settings | File Templates.
 */
public class ChoiceManager extends Controller {

    private static Question editedQuestion;

    private static Form<Choice> rForm = Form.form(Choice.class);

    public static Result index(){
        return ok(views.html.possibleResponse.render(editedQuestion, rForm, ChoiceController.listByQuestId(editedQuestion.id)));
    }

    public static Result editQuestion(Long id){
        setEditedQuestion(id);
        return ok(views.html.possibleResponse.render(editedQuestion, rForm, ChoiceController.listByQuestId(editedQuestion.id)));
    }

    public static Result addResponses(Long questionId){
        setEditedQuestion(questionId);
        Form<Choice> filledForm = rForm.bindFromRequest();
        if(filledForm.hasErrors()){
            return badRequest(views.html.possibleResponse.render(editedQuestion, filledForm, ChoiceController.listByQuestId(editedQuestion.id)));
        }else{
            Choice r = filledForm.get();
            r.questionRef = editedQuestion;
            ChoiceController.save(r);
            return redirect(routes.ChoiceManager.index());
        }
    }

    private static Question setEditedQuestion(Long questionId) {
        return (editedQuestion == null || editedQuestion.id != questionId) ? editedQuestion = QuestionController.getQuestion(questionId) : editedQuestion;
    }

    public static Result deleteResponse(Long id){
        ChoiceController.deleteResponse(id);
        return ok(views.html.possibleResponse.render(editedQuestion, rForm, ChoiceController.listByQuestId(editedQuestion.id)));
    }

}
