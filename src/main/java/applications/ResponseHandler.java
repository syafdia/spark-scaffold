package applications;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

public class ResponseHandler {
    private int statusCode;
    private String message;
    private Object data;
    private ObjectMapper mapper = new ObjectMapper();

    @Data
    class ResponseFormatterOk {
        public int statusCode;
        public String message;
        public Object data;

        public ResponseFormatterOk(int statusCode, String message, Object data) {
            this.statusCode = statusCode;
            this.message = message;
            this.data = data;
        }
    }

    @Data
    class ResponseFormatterOkNoData {
        public int statusCode;
        public String message;

        public ResponseFormatterOkNoData(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }
    }

    @Data
    class ResponseFormatterNok {
        public int statusCode;
        public String error;
        public Object errorData;

        public ResponseFormatterNok(int statusCode, String message, Object data) {
            this.statusCode = statusCode;
            this.error = message;
            this.errorData = data;
        }
    }

    @Data
    class ResponseFormatterNokNoData {
        public int statusCode;
        public String error;

        public ResponseFormatterNokNoData(int statusCode, String message) {
            this.statusCode = statusCode;
            this.error = message;
        }
    }

    private String getServerErrorResponse() {
        String message = ResponseMessage.get(500);
        return "{\"statusCode\":500, \"message\":\"" + message + "\"}";
    }

    public String generate(int statusCode, String message, Object data) {
        try {
            if(statusCode >= 200 && statusCode < 300) {
                if(data != null) {
                    return mapper.writeValueAsString(new ResponseFormatterOk(statusCode, message, data));
                }
                return mapper.writeValueAsString(new ResponseFormatterOkNoData(statusCode, message));
            }

            if(data != null) {
                return mapper.writeValueAsString(new ResponseFormatterNok(statusCode, message, data));
            }
            return mapper.writeValueAsString(new ResponseFormatterNokNoData(statusCode, message));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return this.getServerErrorResponse();
        }
    }
}
