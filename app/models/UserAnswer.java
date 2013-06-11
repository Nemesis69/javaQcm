package models;

import play.db.ebean.Model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 11/06/13
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
public class UserAnswer extends Model {

    public List<String> choices;

    public Long questionId;

}
