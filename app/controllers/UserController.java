package controllers;

import controllers.dao.UserDao;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 13/06/13
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
public class UserController extends Controller {

    private static Form<User> uForm = new Form(User.class);

    public static Result index() {
        return ok(views.html.user.render(uForm));
    }

    public static Result createUser() {
        Form<User> filledForm = uForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(views.html.user.render(filledForm));
        }
        User u = filledForm.get();
        if (UserDao.findUserByMail(u.email) != null) {
          flash().put("error", "cet utilisateur existe déjà");
            return badRequest(views.html.user.render(filledForm));
        } else {
            UserDao.createUser(u);
            return redirect(routes.Application.index());
        }
    }

}
