package controllers;

import models.Question;
import play.db.ebean.Model.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 29/05/13
 * Time: 11:14
 * To change this template use File | Settings | File Templates.
 */
public class QuestionController {
    private static Finder<Long, Question> finder = new Finder(Long.class, Question.class) ;

    protected static List<Question> listAll(){
        return finder.all();
    }

    public static void delete(Long id){
        finder.ref(id).delete();
    }

    public static void createQuestion(Question q){
        q.save();
    }
}
