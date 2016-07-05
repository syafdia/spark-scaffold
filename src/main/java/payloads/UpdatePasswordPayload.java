package payloads;

import lombok.Data;

@Data
public class UpdatePasswordPayload {
    public int userId;
    public String oldPassword;
    public String newPassword;
}
