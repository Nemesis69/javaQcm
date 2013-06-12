package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;


/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 12/06/13
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */

public class LoginController extends Controller {

    private static Form<User> lForm = new Form(User.class);

    public static Result index() {
        return ok(views.html.login.render(lForm));
    }

    public static Result authenticate() {
        Form<User> filledForm = lForm.bindFromRequest();
        if(filledForm.hasErrors()){
            return badRequest(views.html.login.render(filledForm));
        }
        else{
            User user = filledForm.get();
            session().clear();
            session().put("user", user.email);
            return redirect(routes.Application.index());
        }
    }

    public static Result logout(){
        session().clear();
        return redirect(routes.Application.index());
    }

}
