package routes;

import controllers.UsersController;
import models.BaseModel;
import models.User;

import static spark.Spark.*;

public class UserRoute {
    private static UsersController usersController = new UsersController();

    public static void register(String prefix) {
        post(prefix + "/users", (req, res) -> usersController.store(req, res));
        get(prefix + "/users", (req, res) -> usersController.getAll(req, res));
        post(prefix + "/login", (req, res) -> usersController.logIn(req, res));
        get(prefix + "/users/:id", (req, res) -> usersController.getOne(req, res));
        put(prefix + "/users/:id", (req, res) -> usersController.update(req, res));
        delete(prefix + "/users/:id", (req, res) -> usersController.destroy(req, res));
        post(prefix + "/update_password", (req, res) -> usersController.updatePassword(req, res));
    }
}
