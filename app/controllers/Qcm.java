package controllers;

import models.Question;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 29/05/13
 * Time: 10:23
 * To change this template use File | Settings | File Templates.
 */
public class Qcm extends Controller{
    public static Result index(){
        return ok(views.html.qcm.render(buildQcm())) ;
    }

    public static List<Question> buildQcm(){
        List<Question> res = QuestionController.listAll();
        return res;
    }
}
