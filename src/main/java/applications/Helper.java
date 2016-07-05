package applications;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.Default;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class Helper {
    public static String getFormattedTime(long time) {
        String timezone = Default.getTimezoneConfig();
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));

        Date dateTime = new Date(time);
        String timeStr = formatter.format(dateTime);

        return timeStr;
    }

    public static String getFormattedTime(long time, TimeZone timezone) {
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        formatter.setTimeZone(timezone);

        Date dateTime = new Date(time);
        String timeStr = formatter.format(dateTime);

        return timeStr;
    }

    public static Map objToMap(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> output = mapper.convertValue(obj, Map.class);
        return output;
    }

    public static String sha256(String message) {
        try {
            MessageDigest diggest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = diggest.digest(message.getBytes("UTF-8"));
            return convertByteArrayToHexString(hashedBytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return message;
    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    public static String getRandStr() {
        String randStr = UUID.randomUUID().toString().replaceAll("-", "");
        return sha256(randStr);
    }
}
