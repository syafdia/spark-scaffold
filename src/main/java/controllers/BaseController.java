package controllers;

import applications.Auth;
import applications.ResponseHandler;
import applications.ResponseMessage;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static spark.Spark.halt;

public class BaseController {
    protected ResponseHandler responseHandler = new ResponseHandler();
    protected ObjectMapper mapper = new ObjectMapper();
    protected Auth auth;

    protected Response res;
    protected Request req;

    protected Boolean userHasAuth = true;
    protected int invalidCode;

    protected int maxRows = 100;

    public void setHandler(Request req, Response res) {
        this.req = req;
        this.res = res;
    }

    public Object handleRequest(String className) {
        Class myClass = null;
        Object myObject = null;

        try {
            myClass = Class.forName("payloads." + className);
            myObject = myClass.newInstance();
            myObject = this.mapper.readValue(req.body(), myObject.getClass());
            return myObject;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return this.handleResponse(500);
        } catch (InstantiationException e) {
            e.printStackTrace();
            return this.handleResponse(500);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return this.handleResponse(500);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return this.handleResponse(400);
        } catch (JsonMappingException e) {
            return this.handleResponse(400);
        } catch (IOException e) {
            e.printStackTrace();
            return this.handleResponse(400);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return this.handleResponse(400, myClass.getClass().getFields());
        }
    }

    public String handleResponse(int statusCode, String message, Object data) {
        this.res.status(200);
        this.res.type("application/json");
        return this.responseHandler.generate(statusCode, message, data);
    }

    public String handleResponse(int statusCode, Object data) {
        this.res.status(200);
        this.res.type("application/json");
        return this.responseHandler.generate(statusCode, ResponseMessage.get(statusCode), data);
    }

    public String handleResponse(int statusCode) {
        this.res.status(200);
        this.res.type("application/json");
        return this.responseHandler.generate(statusCode, ResponseMessage.get(statusCode), null);
    }

    public void handleAuth(String permission) {
        String accessToken = this.req.headers("X-Token");

        this.auth = new Auth();

        Map authenticationData = (Map) this.auth.checkAuthentication(accessToken);
        int statusCode = (int) authenticationData.get("statusCode");

        if(statusCode != 200) {
            this.handleHalt(statusCode);
        }

        Boolean hasAuthorization = this.auth.checkAuthorization(permission);
        if(!hasAuthorization) {
            this.handleHalt(403);
        }
    }

    public List<String> getClassFields(Field[] fields) {
        List<String> output = new ArrayList<>();
        String message = " field is fillable";

        for (Field field:fields) {
            output.add(field.getName() + message);
        }

        return output;
    }

    private void handleHalt(int statusCode) {
        this.res.status(200);
        this.res.type("application/json");
        halt(this.responseHandler.generate(statusCode, ResponseMessage.get(statusCode), null));
    }
}
