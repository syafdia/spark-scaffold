package applications;

import config.Default;
import models.BaseModel;
import models.User;

import java.sql.Timestamp;
import java.util.*;

public class Auth {

    private Boolean valid = true;
    private Map userData = null;

    public static Object attempt(String username, String password) {
        BaseModel.open();

        User user = User.first("username = ?", username);

        BaseModel.close();

        if(user == null) {
            return null;
        }

        String hashedPassword = (String) user.get("password");
        String salt = (String) user.get("salt");
        String clientHashedPassword = Helper.sha256(password + salt);

        if(!hashedPassword.equals(clientHashedPassword)) {
            return null;
        }

        Map<String, Object> output = new HashMap<>();
        Timestamp sqlTime = (Timestamp) user.get("token_expire");

        output.put("username", user.get("username"));
        output.put("name", user.get("name"));
        output.put("isActive", user.get("is_active"));
        output.put("accessToken", user.get("access_token"));
        output.put("tokenExpire", Helper.getFormattedTime(sqlTime.getTime()));

        return output;
    }

    public int getUserId() {
        if(this.userData == null) {
            return 0;
        }

        return (int) this.userData.get("id");
    }

    public Object checkAuthentication(String accessToken) {
        Map<String, Object> output = new HashMap<>();

        BaseModel.open();

        User user = User.first("access_token = ?", accessToken);

        BaseModel.close();
        if(user == null) {
            output.put("statusCode", 401);
            return output;
        }

        if(! (Boolean) user.get("is_active")) {
            output.put("statusCode", 403);
            return output;
        }

        Timestamp sqlTime = (Timestamp) user.get("token_expire");

        if(this.isTokenExpire(sqlTime.getTime())) {
            this.updateUserToken((Integer) user.get("id"));
            output.put("statusCode", 403);
            return output;
        }

        BaseModel.open();

        Map<String, Object> userData = (Map<String, Object>) user.getFormatted();

        BaseModel.close();

        output.put("statusCode", 200);
        output.put("userData", userData);

        this.userData = userData;

        return output;
    }

    public Boolean checkAuthorization(String permission) {
        if(this.userData == null) {
            return false;
        }

        Map<String, Object> role = (Map<String, Object>) this.userData.get("role");
        List<String> permissions = Arrays.asList((String[]) role.get("permissions"));

        return permissions.contains(permission);
    }

    public String generateSalt() {
        String secret = Default.getSecret();
        return this.generateRandomString(secret);
    }

    public String generateToken() {
        return this.generateRandomString(Helper.getRandStr());
    }

    public String hashPassword(String password) {
        return Helper.sha256(password);
    }

    public Boolean isValid() {
        return this.valid;
    }

    public String updateTokenExpiredTime() {
        long tokenExpiredTime = Default.getTokenExpiredTime();
        long now = new Date().getTime();
        TimeZone timezone = TimeZone.getDefault();

        return Helper.getFormattedTime(now + tokenExpiredTime, timezone);
    }

    public Boolean isSuperAdministrator() {
        return this.hasRole(Default.SUPER_ADMINISTRATOR);
    }

    public Boolean isAdministrator() {
        return this.hasRole(Default.ADMINISTRATOR);
    }

    public Boolean isUser() {
        return this.hasRole(Default.USER);
    }

    private String generateRandomString(String secret) {
        long now = new Date().getTime();
        String message = Helper.getRandStr() + now;

        return Helper.sha256(message + secret);
    }

    private Boolean isTokenExpire(long tokenExpire) {
        long now = new Date().getTime();

        return tokenExpire < now;
    }

    private void updateUserToken(int userId) {
        BaseModel.open();

        User user = User.findById(userId);

        user.set("access_token", this.generateToken())
                .set("token_expire", this.updateTokenExpiredTime())
                .saveIt();

        BaseModel.close();
    }

    private Boolean hasRole(String roleName) {
        if(this.userData == null ) {
            return false;
        }
        Map<String, Object> userRole = (Map<String, Object>) userData.get("role");
        return userRole.get("name").equals(roleName);
    }

}
