package controllers;

import models.Response;
import play.db.ebean.Model;
import play.mvc.Controller;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 07/06/13
 * Time: 15:29
 * To change this template use File | Settings | File Templates.
 */
public class ResponseController extends Model {

    public static Finder<Long, Response> finder = new Finder(Long.class, Response.class);

    public static List<Response> listAllResp(){
        return finder.all();
    }

    public static List<Response> listByQuestId(Long id){
        return finder.where().eq("questionId", id).findList();
    }

    public static void save(Response r){
        r.save();
    }

    public static void deleteResponse(Long id){
        finder.ref(id).delete();
    }

}
