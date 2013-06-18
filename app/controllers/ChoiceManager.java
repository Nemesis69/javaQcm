package controllers;

import controllers.dao.ChoiceDao;
import controllers.dao.QuestionDao;
import controllers.dao.UserDao;
import models.Choice;
import models.Question;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

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
    private static Form<Question> qForm = Form.form(Question.class);

    public static Result index() {
        return ok(views.html.possibleResponse.render(editedQuestion, rForm, ChoiceDao.listByQuestId(editedQuestion.id), editedQuestion.qcm, qForm, Utils.getConnectedUser()));
    }


    public static Result editQuestion(Long id) {
        setEditedQuestion(id);
        return ok(views.html.possibleResponse.render(editedQuestion, rForm, ChoiceDao.listByQuestId(editedQuestion.id), editedQuestion.qcm, qForm, Utils.getConnectedUser()));
    }

    public static Result addResponses(Long questionId) {
        setEditedQuestion(questionId);
        Form<Choice> filledForm = rForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(views.html.possibleResponse.render(editedQuestion, filledForm, ChoiceDao.listByQuestId(editedQuestion.id), editedQuestion.qcm, qForm, Utils.getConnectedUser()));
        } else {
            Choice r = filledForm.get();
            r.questionRef = editedQuestion;
            ChoiceDao.save(r);
            return redirect(routes.ChoiceManager.index());
        }
    }

    private static Question setEditedQuestion(Long questionId) {
        return (editedQuestion == null || editedQuestion.id != questionId) ? editedQuestion = QuestionDao.getQuestion(questionId) : editedQuestion;
    }

    public static Result deleteResponse(Long id) {
        ChoiceDao.deleteResponse(id);
        return ok(views.html.possibleResponse.render(editedQuestion, rForm, ChoiceDao.listByQuestId(editedQuestion.id), editedQuestion.qcm, qForm, Utils.getConnectedUser()));
    }

}
