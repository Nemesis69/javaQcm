package utils;

/**
 * Created with IntelliJ IDEA.
 * User: NBE08314
 * Date: 19/06/13
 * Time: 16:49
 * To change this template use File | Settings | File Templates.
 */
public enum CategoryEnum {

    JAVA("Java"), DOT_NET("dotNet");

    private String cat;

    CategoryEnum(String cat) {
        this.cat = cat;
    }

    public String getCategory(){
        return this.cat;
    }
}
