
import routes.UserRoute;

public class Routes {

    public static void register() {
        registerApiV1();
    }

    private static void registerApiV1() {
        String prefix = "api/";
        UserRoute.register(prefix);
    }
}
