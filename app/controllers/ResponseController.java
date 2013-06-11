package controllers;

import models.Response;
import play.db.ebean.Model.Finder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 11/06/13
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
public class ResponseController {
    private static Finder<Long, Response> finder = new Finder(Long.class, Response.class);

    public static void saveResponse(Response r) {
        r.save();
    }

    public static List<Response> listAllResponses() {
        return finder.all();
    }
}
