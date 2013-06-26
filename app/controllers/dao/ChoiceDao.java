package controllers.dao;

import models.Choice;
import play.db.ebean.Model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 07/06/13
 * Time: 15:29
 * To change this template use File | Settings | File Templates.
 */
public class ChoiceDao extends Model {

    public static Finder<Long, Choice> finder = new Finder(Long.class, Choice.class);

    public static List<Choice> listAllChoices(){
        return finder.all();
    }

    public static List<Choice> listByQuestId(Long id){
        return finder.where().eq("questionId", id).findList();
    }

    public static void saveOrUpdate(Choice r){
        if(r.id == null)
            r.save();
        else
            r.update();
    }

    public static void deleteResponse(Long id){
        finder.ref(id).delete();
    }

    public static Choice getById(Long id) {
        return finder.byId(id);
    }
}
