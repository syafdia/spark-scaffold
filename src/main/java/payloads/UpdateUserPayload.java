package payloads;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UpdateUserPayload {
    public String password = null;
    public String name = null;
    public Integer roleId = null;

    public Map<String ,Object> getFilled(Map<String ,Object> payload) {
        return payload.entrySet()
                .stream()
                .filter(v -> v.getValue() != null)
                .collect(Collectors.toMap(v -> v.getKey(), v -> v.getValue()));
    }

}
