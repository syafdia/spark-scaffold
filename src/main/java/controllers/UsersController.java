package controllers;

import applications.Helper;
import models.BaseModel;
import models.User;
import payloads.CreateUserPayload;
import payloads.LogInUserPayload;
import payloads.UpdatePasswordPayload;
import payloads.UpdateUserPayload;
import spark.Request;
import spark.Response;

import java.util.*;

public class UsersController extends BaseController implements ControllerInterface{

    @Override
    public String getOne(Request req, Response res) {
        super.setHandler(req, res);
        super.setAuth("R_USER");

        int userId;

        try {
            userId = Integer.parseInt(req.params(":id"));
        } catch(NumberFormatException e) {
            userId = 0;
        }

        Map authUserData = super.auth.getUserData();

        // Disable checking others user data if current user role is "User"
        if(super.auth.isUser() && userId != (int) authUserData.get("id")) {
            return handleResponse(403);
        }

        BaseModel.open();
        User user = User.findById(userId);
        BaseModel.close();

        if(user == null) {
            return super.handleResponse(404);
        }

        Map output;
        BaseModel.open();
        output = (Map) user.getFormatted();
        BaseModel.close();

        return super.handleResponse(200, output);
    }

    @Override
    public String getAll(Request req, Response res) {
        super.setHandler(req, res);
        super.setAuth("R_USER");

        if(!super.auth.isSuperAdministrator()) {
            return super.handleResponse(403);
        }

        int offset;
        int limit;

        try {
            offset = req.queryMap().get("offset").integerValue();
            limit = req.queryMap().get("limit").integerValue();
        } catch (NullPointerException e) {
            offset = 0;
            limit = 10;
        }

        if(limit - offset > super.maxRows) {
            return super.handleResponse(400, "Maximum data allowed is " + this.maxRows);
        }

        ArrayList<Map> output = new ArrayList<>();

        BaseModel.open();

        List<User> users = User.findAll()
                                .offset(offset)
                                .limit(limit)
                                .orderBy("created_at desc");

        for (User user: users) {
            output.add((Map) user.getFormatted());
        }

        BaseModel.close();

        return super.handleResponse(200, output);
    }

    @Override
    public String store(Request req, spark.Response res){
        super.setHandler(req, res);
        super.setAuth("C_USER");

        CreateUserPayload userPayload;

        try {
            userPayload = (CreateUserPayload) super.handleRequest("CreateUserPayload");
        } catch (ClassCastException e) {
            return super.handleResponse(400, super.getClassFields(CreateUserPayload.class.getFields()));
        }

        if(!userPayload.isValid()) {
            return super.handleResponse(400);
        }

        if(User.isUsernameExist(userPayload.username)) {
            return super.handleResponse(400, "User already exist");
        }

        String salt = super.auth.generateSalt();
        String hashedPassword = super.auth.hashPassword(userPayload.password + salt);

        BaseModel.open();
        User newUser = new User()
                .set("username", userPayload.username)
                .set("password", hashedPassword)
                .set("salt", salt)
                .set("role_id", userPayload.roleId)
                .set("name", userPayload.name)
                .set("access_token", super.auth.generateToken())
                .set("token_expire", super.auth.updateTokenExpiredTime());

        if(!newUser.save()) {
            return super.handleResponse(400, newUser.errors());
        }

        BaseModel.close();

        return super.handleResponse(201);
    }

    @Override
    public String update(Request req, Response res) {
        super.setHandler(req, res);
        super.setAuth("U_USER");

        int userId;

        try {
            userId = Integer.parseInt(req.params(":id"));
        } catch(NumberFormatException e) {
            userId = 0;
        }

        UpdateUserPayload userPayload;

        try {
            userPayload = (UpdateUserPayload) super.handleRequest("UpdateUserPayload");
        } catch (ClassCastException e) {
            return super.handleResponse(400, super.getClassFields(UpdateUserPayload.class.getFields()));
        }

        Map updateData = userPayload.getFilled(Helper.objToMap(userPayload));

        // Disable updating others user data if current user role is not "Superadministrator"
        if(!super.auth.isSuperAdministrator()) {
            Map authUserData = super.auth.getUserData();
            if((int) authUserData.get("id") != userId) {
                return super.handleResponse(403);
            }
        }

        Boolean updated;

        BaseModel.open();
        User user = User.findById(userId);

        if(user == null) {
            BaseModel.close();
            return super.handleResponse(404);
        }

        updated = user.fromMap(updateData).save();
        BaseModel.close();

        if(!updated) {
            return super.handleResponse(400, user.errors());
        }

        return super.handleResponse(200);
    }

    @Override
    public String destroy(Request req, Response res) {
        super.setHandler(req, res);
        super.setAuth("D_USER");

        int userId;

        try {
            userId = Integer.parseInt(req.params(":id"));
        } catch(NumberFormatException e) {
            userId = 0;
        }

        if(!super.auth.isSuperAdministrator()) {
            return super.handleResponse(403);
        }
        Boolean deleted;

        BaseModel.open();
        User user = User.findById(userId);
        if(user == null) {
            BaseModel.close();
            return super.handleResponse(404);
        }

        deleted = user.delete();
        BaseModel.close();

        if(!deleted) {
            return super.handleResponse(400, user.errors());
        }

        return super.handleResponse(200);
    }

    public String logIn(Request req, Response res) {
        super.setHandler(req, res);

        LogInUserPayload userPayload;

        try {
            userPayload = (LogInUserPayload) super.handleRequest("LogInUserPayload");
        } catch (ClassCastException e) {
            return super.handleResponse(400, super.getClassFields(LogInUserPayload.class.getFields()));
        }

        if(!userPayload.isValid()) {
            return super.handleResponse(400);
        }

        Object loginData = super.auth.attempt(userPayload.username, userPayload.password);

        if(loginData == null) {
            return super.handleResponse(401);
        }

        return super.handleResponse(200, loginData);
    }

    public String updatePassword(Request req, Response res) {
        super.setHandler(req, res);
        super.setAuth("U_PASSWORD");

        UpdatePasswordPayload updatePasswordPayload;

        try {
            updatePasswordPayload = (UpdatePasswordPayload) super.handleRequest("UpdatePasswordPayload");
        } catch (ClassCastException e) {
            return super.handleResponse(400, super.getClassFields(UpdatePasswordPayload.class.getFields()));
        }

        // Disable updating others user data if current user role is not "Superadministrator"
        if(!super.auth.isSuperAdministrator()) {
            Map authUserData = super.auth.getUserData();
            if((int) authUserData.get("id") != updatePasswordPayload.userId) {
                return super.handleResponse(403);
            }
        }

        BaseModel.open();
        User user = User.findById(updatePasswordPayload.userId);

        if(user == null) {
            BaseModel.close();
            return super.handleResponse(404);
        }
        Boolean updated;

        BaseModel.close();

        if(!isOldPasswordMatch((String) user.get("username"), updatePasswordPayload.oldPassword)) {
            return super.handleResponse(400, "Password doesn't match");
        }

        String salt = super.auth.generateSalt();
        String hashedPassword = super.auth.hashPassword(updatePasswordPayload.newPassword + salt);

        BaseModel.open();
        updated = user.set("salt", salt).set("password", hashedPassword).save();
        BaseModel.close();

        if(!updated) {
            return super.handleResponse(400, user.errors());
        }

        return super.handleResponse(200);
    }

    private Boolean isOldPasswordMatch(String username, String oldPassword) {
        Object loginData = super.auth.attempt(username, oldPassword);

        return loginData != null;
    }
}
