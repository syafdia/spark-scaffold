package payloads;

import lombok.Data;

@Data
public class LogInUserPayload {
    public String username;
    public String password;

    public boolean isValid() {
        if (!username.isEmpty() && !password.isEmpty()) return true;
        else return false;
    }
}
