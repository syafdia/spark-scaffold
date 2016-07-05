package payloads;

import config.Default;
import lombok.Data;

@Data
public class CreateUserPayload {
    public String username = null;
    public String password = null;
    public String name = null;
    public int roleId = Default.USER_ID;

    public boolean isValid() {
        if (!username.isEmpty() && !password.isEmpty() && !name.isEmpty()) return true;
        else return false;
    }
}
