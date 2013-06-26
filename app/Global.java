
import controllers.dao.DomainDao;
import controllers.dao.UserDao;
import models.Domain;
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
        LinkedHashMap<String, Object> l  = (LinkedHashMap)Yaml.load("initial-data.yml");
        if (UserDao.getFinder().findRowCount() == 0) {
            List<User> users = (List)l.get("users");
            for(User u : users){
                u.save();
            }
        }
        if(DomainDao.listAll().size() == 0){
            List<Domain> domains = (List)l.get("domains");
            for(Domain d : domains){
                d.save();
            }
        }
    }
}

