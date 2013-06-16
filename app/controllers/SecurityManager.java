package controllers;

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
        Map<String, String[]> headers = ctx.request().headers();
        LoginController.redirectUri = ctx.request().uri();
        return ctx.session().get("user");
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(routes.LoginController.index());
    }


}
