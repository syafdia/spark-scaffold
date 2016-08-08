package controllers;

import applications.Auth;
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
        this.setHandler(req, res);
        this.handleAuth("R_USER");

        int userId;

        try {
            userId = Integer.parseInt(req.params(":id"));
        } catch(NumberFormatException e) {
            userId = 0;
        }

        // Disable checking others user data if current user role is "User"
        if(this.auth.isUser() && this.auth.getUserId() != userId) {
            return handleResponse(403);
        }

        BaseModel.open();
        User user = User.findById(userId);
        BaseModel.close();

        if(user == null) {
            return this.handleResponse(404);
        }

        Map output;
        BaseModel.open();
        output = (Map) user.getFormatted();
        BaseModel.close();

        return this.handleResponse(200, output);
    }

    @Override
    public String getAll(Request req, Response res) {
        this.setHandler(req, res);
        this.handleAuth("R_USER");

        if(!this.auth.isSuperAdministrator()) {
            return this.handleResponse(403);
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

        if(limit - offset > this.maxRows) {
            return this.handleResponse(400, "Maximum data allowed is " + this.maxRows);
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

        return this.handleResponse(200, output);
    }

    @Override
    public String store(Request req, spark.Response res){
        this.setHandler(req, res);
        this.handleAuth("C_USER");

        CreateUserPayload userPayload;

        try {
            userPayload = (CreateUserPayload) this.handleRequest("CreateUserPayload");
        } catch (ClassCastException e) {
            return this.handleResponse(400, this.getClassFields(CreateUserPayload.class.getFields()));
        }

        if(!userPayload.isValid()) {
            return this.handleResponse(400);
        }

        if(User.isUsernameExist(userPayload.username)) {
            return this.handleResponse(400, "User already exist");
        }

        String salt = this.auth.generateSalt();
        String hashedPassword = this.auth.hashPassword(userPayload.password + salt);

        BaseModel.open();
        User newUser = new User()
                .set("username", userPayload.username)
                .set("password", hashedPassword)
                .set("is_active", 1)
                .set("salt", salt)
                .set("role_id", userPayload.roleId)
                .set("name", userPayload.name)
                .set("access_token", this.auth.generateToken())
                .set("token_expire", this.auth.updateTokenExpiredTime());

        if(!newUser.save()) {
            return this.handleResponse(400, newUser.errors());
        }

        BaseModel.close();

        return this.handleResponse(201);
    }

    @Override
    public String update(Request req, Response res) {
        this.setHandler(req, res);
        this.handleAuth("U_USER");

        int userId;

        try {
            userId = Integer.parseInt(req.params(":id"));
        } catch(NumberFormatException e) {
            userId = 0;
        }

        UpdateUserPayload userPayload;

        try {
            userPayload = (UpdateUserPayload) this.handleRequest("UpdateUserPayload");
        } catch (ClassCastException e) {
            return this.handleResponse(400, this.getClassFields(UpdateUserPayload.class.getFields()));
        }

        Map updateData = userPayload.getFilled(Helper.objToMap(userPayload));

        // Disable updating the others users data if current user role is not "Superadministrator"
        if(!this.auth.isSuperAdministrator()) {
            if((this.auth.getUserId() != userId)) {
                return this.handleResponse(403);
            }
        }

        Boolean updated;

        BaseModel.open();
        User user = User.findById(userId);

        if(user == null) {
            BaseModel.close();
            return this.handleResponse(404);
        }

        updated = user.fromMap(updateData).save();
        BaseModel.close();

        if(!updated) {
            return this.handleResponse(400, user.errors());
        }

        return this.handleResponse(200);
    }

    @Override
    public String destroy(Request req, Response res) {
        this.setHandler(req, res);
        this.handleAuth("D_USER");

        int userId;

        try {
            userId = Integer.parseInt(req.params(":id"));
        } catch(NumberFormatException e) {
            userId = 0;
        }

        if(!this.auth.isSuperAdministrator()) {
            return this.handleResponse(403);
        }

        Boolean deleted;

        BaseModel.open();
        User user = User.findById(userId);
        if(user == null) {
            BaseModel.close();
            return this.handleResponse(404);
        }

        deleted = user.delete();
        BaseModel.close();

        if(!deleted) {
            return this.handleResponse(400, user.errors());
        }

        return this.handleResponse(200);
    }

    public String logIn(Request req, Response res) {
        this.setHandler(req, res);

        LogInUserPayload userPayload;

        try {
            userPayload = (LogInUserPayload) this.handleRequest("LogInUserPayload");
        } catch (ClassCastException e) {
            return this.handleResponse(400, this.getClassFields(LogInUserPayload.class.getFields()));
        }

        if(!userPayload.isValid()) {
            return this.handleResponse(400);
        }

        Object loginData = Auth.attempt(userPayload.username, userPayload.password);

        if(loginData == null) {
            return this.handleResponse(401);
        }

        return this.handleResponse(200, loginData);
    }

    public String updatePassword(Request req, Response res) {
        this.setHandler(req, res);
        this.handleAuth("U_PASSWORD");

        UpdatePasswordPayload updatePasswordPayload;

        try {
            updatePasswordPayload = (UpdatePasswordPayload) this.handleRequest("UpdatePasswordPayload");
        } catch (ClassCastException e) {
            return this.handleResponse(400, this.getClassFields(UpdatePasswordPayload.class.getFields()));
        }

        // Disable updating the others users data if current user role is not "Superadministrator"
        if(!this.auth.isSuperAdministrator()) {
            if(this.auth.getUserId() != updatePasswordPayload.userId) {
                return this.handleResponse(403);
            }
        }

        BaseModel.open();
        User user = User.findById(updatePasswordPayload.userId);

        if(user == null) {
            BaseModel.close();
            return this.handleResponse(404);
        }
        Boolean updated;

        BaseModel.close();

        if(!isOldPasswordMatch((String) user.get("username"), updatePasswordPayload.oldPassword)) {
            return this.handleResponse(400, "Password doesn't match");
        }

        String salt = this.auth.generateSalt();
        String hashedPassword = this.auth.hashPassword(updatePasswordPayload.newPassword + salt);

        BaseModel.open();
        updated = user.set("salt", salt).set("password", hashedPassword).save();
        BaseModel.close();

        if(!updated) {
            return this.handleResponse(400, user.errors());
        }

        return this.handleResponse(200);
    }

    private Boolean isOldPasswordMatch(String username, String oldPassword) {
        Object loginData = this.auth.attempt(username, oldPassword);

        return loginData != null;
    }
}
