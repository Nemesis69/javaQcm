
import controllers.dao.UserDao;
import models.User;
import play.Application;
import play.GlobalSettings;
import play.libs.Yaml;

import java.util.LinkedHashMap;
import java.util.List;

public class Global extends GlobalSettings {
    @Override
    public void onStart(Application app) {
        // Check if the database is empty
        if (UserDao.getFinder().findRowCount() == 0) {
            LinkedHashMap<String, Object> l  = (LinkedHashMap)Yaml.load("initial-data.yml");
            List<User> users = (List)l.get("users");
            for(User u : users){
                u.save();
            }
        }
    }
}

