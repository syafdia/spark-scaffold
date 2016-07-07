import middlewares.AfterMiddleware;
import middlewares.BeforeMiddleware;
import spark.Spark;

import static spark.Spark.after;
import static spark.Spark.before;

public class Main {
    public static void main(String[] args) {
        Spark.staticFileLocation("/public");

        before((req, res) -> {
            BeforeMiddleware.register(req, res);
        });

        after((req, res) -> {
            AfterMiddleware.register(req, res);
        });

        Routes.register();
    }
}
