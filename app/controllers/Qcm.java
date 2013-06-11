package controllers;

import models.Choice;
import models.Question;
import models.Response;
import models.UserAnswer;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

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
public class Qcm extends Controller {

    private static Form qForm = Form.form(UserAnswer.class);
    private static Long score = Long.valueOf(0);

    public static Result index() {
        return ok(views.html.qcm.render(buildQcm(), qForm, score));
    }

    public static List<Question> buildQcm() {
        List<Question> res = QuestionController.listAll();

        return res;
    }

    public static Result validateResponses() {
        Form<UserAnswer> filledForm = qForm.bindFromRequest();

        UserAnswer uAns = filledForm.get();
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] checkedVal = map.get("selectedChoices");
        String[] questionIds = map.get("questionId");
        List<String> l = Arrays.asList(checkedVal);
        for (String s : l) {
            Choice c = ChoiceController.finder.byId(Long.valueOf(s));
            if ("OK".equals(c.status)) {
                score += 1;
            }
            Response response = new Response();
            response.choice = c;
            response.question = QuestionController.getQuestion(Long.valueOf(questionIds[0]));
            ResponseController.saveResponse(response);

        }
        return redirect(routes.Qcm.index());
    }

    public static Result score() {
        return TODO;
    }
}
