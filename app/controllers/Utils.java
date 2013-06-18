package controllers;

import controllers.dao.UserDao;
import models.User;
import play.mvc.Controller;

/**
 * Created with IntelliJ IDEA.
 * User: NBE08314
 * Date: 18/06/13
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */
public class Utils extends Controller{

    static User getConnectedUser() {
        User requestUser = UserDao.findUserByMail(Controller.request().username());
        return requestUser!=null?requestUser:UserDao.findUserByMail(session().get("user"));
    }
}
