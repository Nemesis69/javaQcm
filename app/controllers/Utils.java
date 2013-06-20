package controllers;

import controllers.dao.UserDao;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    static String getMd5String(User user) throws NoSuchAlgorithmException {
        MessageDigest msgDig = MessageDigest.getInstance("MD5");
        msgDig.update(user.password.getBytes(Charset.forName("UTF8")));
        byte[] hash = msgDig.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hash.length; ++i) {
            sb.append(Integer.toHexString((hash[i] & 0xFF) | 0x100).substring(1,3));
        }
        return sb.toString();
    }
}
