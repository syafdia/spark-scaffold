package config;

public class Default {

    public static final String SUPER_ADMINISTRATOR = "superadministrator";
    public static final String ADMINISTRATOR = "administrator";
    public static final String USER = "user";

    public static final int SUPER_ADMINISTRATOR_ID = 1;
    public static final int ADMINISTRATOR_ID = 1;
    public static final int USER_ID = 1;

    /**
     * Change to "Development" or "Production"
     */
    private static Development config = new Development();

    public static Database getDatabaseConfig() {
        return config.getDatabaseConfig();
    }

    public static String getTimezoneConfig() {
        return config.getTimezoneConfig();
    }

    public static String getSecret() {
        return config.getSecret();
    }

    public static long getTokenExpiredTime() {
        return config.getTokenExpiredTime();
    }
}
