package middlewares;

import spark.Request;
import spark.Response;

public class BeforeMiddleware {
    public static void register(Request req, Response res) {
        // Register before middleware
        ContentCheckerMiddleware.run(req, res);
    }
}
