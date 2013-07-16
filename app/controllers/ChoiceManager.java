package controllers;

import controllers.dao.ChoiceDao;
import controllers.dao.QcmDao;
import controllers.dao.QuestionDao;
import models.Choice;
import models.Question;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Utils;
import views.html.possibleResponse;

import java.math.BigDecimal;

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
        return ok(possibleResponse.render(editedQuestion, rForm, ChoiceDao.listByQuestId(editedQuestion.id), editedQuestion.qcm, qForm, Utils.getConnectedUser(), "", null));
    }


    public static Result editQuestion(Long id) {
        setEditedQuestion(id);
        return ok(possibleResponse.render(editedQuestion, rForm, ChoiceDao.listByQuestId(editedQuestion.id), editedQuestion.qcm, qForm.fill(editedQuestion), Utils.getConnectedUser(), "", null));
    }

    public static Result addResponses(Long questionId) {
        setEditedQuestion(questionId);
        Form<Choice> filledForm = rForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(possibleResponse.render(editedQuestion, filledForm, ChoiceDao.listByQuestId(editedQuestion.id), editedQuestion.qcm, qForm, Utils.getConnectedUser(), "", null));
        } else {
            Choice r = filledForm.get();
            r.questionRef = editedQuestion;
            if(Utils.STATUS_OK.equals(r.status)){
                editedQuestion.qcm.maxScore.add(BigDecimal.ONE);
                QcmDao.saveOrUpdate(editedQuestion.qcm);
            }
            ChoiceDao.saveOrUpdate(r);
            return redirect(routes.ChoiceManager.index());
        }
    }

    private static Question setEditedQuestion(Long questionId) {
        return (editedQuestion == null || !editedQuestion.id.equals(questionId)) ? editedQuestion = QuestionDao.getQuestion(questionId) : editedQuestion;
    }

    public static Result editResponse(Long id){
        return ok(possibleResponse.render(editedQuestion, rForm.fill(ChoiceDao.getById(id)), ChoiceDao.listByQuestId(editedQuestion.id), editedQuestion.qcm, qForm, Utils.getConnectedUser(), "EDIT", ChoiceDao.getById(id)));
    }

    public static Result updResponse(){
        Form<Choice> filledForm = rForm.bindFromRequest();
        if(filledForm.hasErrors()){
            return badRequest(possibleResponse.render(editedQuestion, rForm.fill(filledForm.get()), ChoiceDao.listByQuestId(editedQuestion.id), editedQuestion.qcm, qForm, Utils.getConnectedUser(), "EDIT", filledForm.get()));
        }
        else{
            Choice c = filledForm.get();
            ChoiceDao.saveOrUpdate(c);
            return ok(possibleResponse.render(editedQuestion, rForm, ChoiceDao.listByQuestId(editedQuestion.id), editedQuestion.qcm, qForm.fill(editedQuestion), Utils.getConnectedUser(), "", null));
        }
    }

    public static Result retourChoix(Long id){
        return ok(possibleResponse.render(editedQuestion, rForm, ChoiceDao.listByQuestId(editedQuestion.id), editedQuestion.qcm, qForm.fill(editedQuestion), Utils.getConnectedUser(), "", null));
    }

    public static Result deleteResponse(Long id) {
        Choice c = ChoiceDao.getById(id);
        if(Utils.STATUS_OK.equals(c.status)){
           c.questionRef.qcm.maxScore.subtract(BigDecimal.ONE);
        }
        ChoiceDao.deleteResponse(id);
        return ok(possibleResponse.render(editedQuestion, rForm, ChoiceDao.listByQuestId(editedQuestion.id), editedQuestion.qcm, qForm, Utils.getConnectedUser(), "", null));
    }

}
