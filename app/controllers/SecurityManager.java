package controllers;

import controllers.dao.UserDao;
import models.User;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 12/06/13
 * Time: 19:08
 * To change this template use File | Settings | File Templates.
 */
public class SecurityManager extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        LoginController.redirectUri = ctx.request().uri();
        if("/admin".equals(ctx.request().uri())){
            User user = UserDao.findUserByMail(ctx.session().get("user"));
            if(user != null && user.isAdmin)
                return ctx.session().get("user");
            else
                return null;
        }
        return ctx.session().get("user");
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(routes.LoginController.index());
    }


}
