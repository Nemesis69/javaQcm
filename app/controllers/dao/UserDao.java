package controllers.dao;

import models.User;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 13/06/13
 * Time: 09:38
 * To change this template use File | Settings | File Templates.
 */
public class UserDao {
    private static Finder<Long, User> finder = new Finder(Long.class, User.class);

    public static User findUserForAuth(String mail, String password){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("email", mail);
        parameters.put("password", password);
        return finder.where().allEq(parameters).findUnique();
    }

    public static User findUserByMail(String mail){
        return finder.where().eq("email", mail).findUnique();
    }

    public static Finder<Long, User> getFinder(){
        return finder;
    }

    public static void createUser(User u) {
        u.save();
    }
}
