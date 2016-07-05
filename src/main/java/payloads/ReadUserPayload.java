package payloads;

import lombok.Data;

import java.util.Objects;

@Data
public class ReadUserPayload {
    public String username;
    public String name;
    public String created_at;
    public Object role;
}
