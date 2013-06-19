package controllers;

import controllers.dao.UserDao;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import java.security.NoSuchAlgorithmException;

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
        return ok(views.html.user.render(uForm, Utils.getConnectedUser()));
    }

    public static Result createUser() throws NoSuchAlgorithmException {
        Form<User> filledForm = uForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(views.html.user.render(filledForm, Utils.getConnectedUser()));
        }
        User u = filledForm.get();
        if (UserDao.findUserByMail(u.email) != null) {
          flash().put("error", "cet utilisateur existe déjà");
            return badRequest(views.html.user.render(filledForm, Utils.getConnectedUser()));
        } else {
            String pass = Utils.getMd5String(u);
            u.password = pass;
            UserDao.createUser(u);
            return redirect(routes.Application.index());
        }
    }

}
