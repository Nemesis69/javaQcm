package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 29/05/13
 * Time: 10:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Choice extends Model {

    @Id
    public Long id;

    @Constraints.Required()
    public String libelle;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "questionId", nullable = false)
    public Question questionRef;
}
