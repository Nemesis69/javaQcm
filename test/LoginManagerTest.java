
import controllers.routes;
import org.junit.Test;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;


/**
 * Created with IntelliJ IDEA.
 * User: Nemesis
 * Date: 12/06/13
 * Time: 19:14
 * To change this template use File | Settings | File Templates.
 */
public class LoginManagerTest extends WithApplication {
    @Test
    public void loginTest(){
        Result result = callAction(
                routes.ref.LoginController.authenticate(),
                fakeRequest().withSession("email", "bob@example.com")
        );
        assertEquals(200, status((play.mvc.Result) result));
    }
}
