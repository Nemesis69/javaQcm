package controllers.dao;

import models.Qcm;
import play.db.ebean.Model.Finder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 14/06/13
 * Time: 09:21
 * To change this template use File | Settings | File Templates.
 */
public class QcmDao {
    private static Finder<Long, Qcm> finder = new Finder(Long.class, Qcm.class);

    public static List<Qcm> listAll(){
        return finder.all();
    }

    public static Qcm findById(Long id){
        return finder.ref(id);
    }

    public static void saveOrUpdate(Qcm qcm) {
        if(qcm.id == null)
            qcm.save();
        else
            qcm.update();
    }

    public static Qcm findByName(String name){
        return finder.where().eq("name", name).findUnique();
    }

    public static void delete(Long id) {
        finder.ref(id).delete();
    }
}
