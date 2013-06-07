package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.*;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 29/05/13
 * Time: 10:35
 * To change this template use File | Settings | File Templates.
 */


@Entity
public class Question extends Model{

    @Id
    public Long id;

    @Required()
    public String text;

    public Long domainId;

    /*@OneToMany(mappedBy = )
    public List<Response> possibleResp;*/

}
