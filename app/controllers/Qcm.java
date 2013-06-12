package controllers;

import models.Choice;
import models.Question;
import models.Response;
import models.UserAnswer;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

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

    @Security.Authenticated(SecurityManager.class)
    public static Result index() {
        return ok(views.html.qcm.render(buildQcm(), qForm, score));
    }

    public static List<Question> buildQcm() {
        List<Question> res = QuestionDao.listAll();

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
            Response response = new Response();
            response.choice =  ChoiceDao.finder.byId(Long.valueOf(s));
            response.question = QuestionDao.getQuestion(Long.valueOf(questionIds[0]));
            response.user = session().get("user");
            ResponseDao.saveResponse(response);
        }
        return redirect(routes.Qcm.index());
    }

    public static Result score() {
        score = 0L;
        List<Response> responses = ResponseDao.listByUser(session().get("user"));
        for(Response response : responses){
            Choice c = response.choice;
            if("OK".equals(c.status))
                score += 1;
        }
        return redirect(routes.Qcm.index());
    }
}
