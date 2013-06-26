package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 29/05/13
 * Time: 10:39
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class Domain extends Model{

    @Id
    public Long id;

    @Column(unique = true)
    public String libelle;

}
