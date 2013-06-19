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
 * Date: 12/06/13
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */

public class LoginController extends Controller {

    private static Form<User> lForm = new Form(User.class);

    static String redirectUri;

    public static Result index() {
        return ok(views.html.login.render(lForm, Utils.getConnectedUser()));
    }

    public static Result authenticate() throws NoSuchAlgorithmException {
        Form<User> filledForm = lForm.bindFromRequest();
        if(filledForm.hasErrors()){
            return badRequest(views.html.login.render(filledForm, Utils.getConnectedUser()));
        }
        else{
            User user = filledForm.get();
            session().clear();
            String pass = Utils.getMd5String(user);
            User dbUser = UserDao.findUserForAuth(user.email, pass);
            if(dbUser != null)
                session().put("user", user.email);
            else{
                flash().put("logError", "User not found");
                return badRequest(views.html.login.render(filledForm, Utils.getConnectedUser()));
            }
            if(redirectUri != null && (redirectUri.contains("admin") && dbUser.isAdmin))
                return redirect(routes.Admin.index());
            else if(redirectUri != null &&(redirectUri.contains("admin") && !dbUser.isAdmin)){
                flash().put("AccessError", "Vous n'avez pas les autorisations pour accéder à cet écran");
                return badRequest(views.html.login.render(filledForm, Utils.getConnectedUser()));
            }
            else if (redirectUri != null &&(redirectUri.contains("qcm")))
                return redirect(routes.QcmController.index());
            else
                return redirect(routes.Application.index());
        }
    }


    public static Result logout(){
        session().clear();
        return redirect(routes.Application.index());
    }

}
