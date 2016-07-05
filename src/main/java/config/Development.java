package config;

public class Development {
    /**
     * Database config;
     */
    private String dbUsername = ""; // Your database username
    private String dbPassword = ""; // Your database password
    private String dbHost = ""; // Your database host
    private String dbName = ""; // Your database name
    private String dbDriver = "mysql";
    private String dbDriverPkg = "com.mysql.jdbc.Driver";
    private int dbPort = 3306; // Your database port

    /**
     * Timezone config;
     */
    private String timezone = "UTC";

    /**
     * Secret config;
     */
    private String secret = "567839jhdyehUybdkjhf0387ch7797hhud";


    /**
     * Token expired
     */
    private long tokenExpiredTime = 1 * 24 * 60 * 60 * 1000;



    public Database getDatabaseConfig() {
        return new Database(dbDriverPkg, dbDriver, dbHost, dbName, dbUsername, dbPassword);
    }

    public String getTimezoneConfig() {
        return this.timezone;
    }

    public String getSecret() {
        return this.secret;
    }

    public long getTokenExpiredTime() {
        return this.tokenExpiredTime;
    }


}
