package controllers.dao;

import models.Qcm;
import models.Response;
import models.User;
import play.db.ebean.Model.Finder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 11/06/13
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
public class ResponseDao {
    private static Finder<Long, Response> finder = new Finder(Long.class, Response.class);

    public static void saveResponse(Response r) {
        r.save();
    }

    public static List<Response> listAllResponses() {
        return finder.all();
    }

    public static List<Response> listByUser(String user) {
        return finder.fetch("user").where().ilike("email", user).findList();
    }

    public static void deleteRespForUserAndQcm(Qcm evaluatedQcm, User user) {
        List<Response> resps = finder.fetch("user").fetch("question.qcm").where().eq("question.qcm.id", evaluatedQcm.id).eq("user.email", user.email).findList();
        for(Response rep : resps){
            rep.delete();
        }
    }
}
