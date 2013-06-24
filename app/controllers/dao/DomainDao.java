package controllers.dao;

import models.Domain;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: NBE08314
 * Date: 24/06/13
 * Time: 10:00
 * To change this template use File | Settings | File Templates.
 */
public class DomainDao {

    private static Finder<Long, Domain> finder = new Model.Finder(Long.class, Domain.class);

    public static List<Domain> listAll(){
        return finder.all();
    }

    public static Domain getById(Long id) {
        return finder.byId(id);
    }
}
