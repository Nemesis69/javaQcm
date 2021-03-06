package models;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import utils.CategoryEnum;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 14/06/13
 * Time: 09:12
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Qcm extends Model {

    @Id
    public Long id;

    @Required()
    @Column(unique = true)
    public String name;

    @OneToMany(mappedBy = "qcm", cascade = CascadeType.ALL)
    public List<Question> questions;

    @Column(name = "category")
    public CategoryEnum category;

    public BigDecimal maxScore;
}
