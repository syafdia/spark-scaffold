package models;

import applications.Helper;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@BelongsTo(parent = Role.class, foreignKeyName = "role_id")
public class User extends Model implements ModelInterface {
    static{
        validatePresenceOf("name", "username", "password");
        validateNumericalityOf("role_id");
        validateRegexpOf("username", "\\b[a-zA-Z0-9]{4,20}\\b")
                .message("username must a-z A-Z 0-9 in range 4 - 20");
    }

    public static boolean isUsernameExist(String username) {
        boolean output;
        BaseModel.open();
        output = User.findFirst("username = ?", username).exists();
        BaseModel.close();
        return output;
    }

    @Override
    public Object getFormatted() {
        Map<String, Object> output = new HashMap<>();
        Map<String, Object> role = new HashMap<>();
        Timestamp sqlTime = (Timestamp) this.get("created_at");
        String[] permissions = this.parent(Role.class).get("permissions").toString().split(",");

        role.put("name", this.parent(Role.class).get("name"));
        role.put("alias", this.parent(Role.class).get("alias"));
        role.put("permissions", permissions);

        output.put("id", this.get("id"));
        output.put("username", this.get("username"));
        output.put("name", this.get("name"));
        output.put("createdAt", Helper.getFormattedTime(sqlTime.getTime()));
        output.put("role", role);

        return output;
    }
}
