package middlewares;

import applications.ResponseHandler;
import spark.Request;
import spark.Response;

import static spark.Spark.halt;

public class ContentCheckerMiddleware {

    public static void run(Request req, Response res) {
        checkContentType(req, res);
    }

    private static void checkContentType(Request req, Response res) {
        String contentType = req.headers("Content-Type");
        ResponseHandler responseHandler = new ResponseHandler();

        if(contentType != "application/json") {
            res.type("application/json");
            halt(400, responseHandler.generate(400, "Content-Type is not JSON", null));
        }
    }
}
