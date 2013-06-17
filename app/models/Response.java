package models;

import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 11/06/13
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class Response extends Model {

    @Id
    public Long id;

    @OneToOne
    @JoinColumn(name="choiceId")
    public Choice choice;

    @ManyToOne
    @JoinColumn(name="questionId")
    public Question question;

    @ManyToOne
    @JoinColumn(name = "userId")
    public User user;
}
