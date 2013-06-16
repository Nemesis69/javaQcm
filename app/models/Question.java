package models;

import play.db.ebean.Model;

import javax.persistence.*;

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

    @OneToMany(mappedBy = "questionRef", cascade = CascadeType.ALL)
    public List<Choice> possibleResp;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "qcmId")
    public Qcm qcm;

}
