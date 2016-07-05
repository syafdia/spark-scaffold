package config;

public class Database {
    private int port;
    private String driver;
    private String driverPackage;
    private String host;
    private String username;
    private String password;
    private String dbName;

    public Database(String driverPackage, String driver, String host, String dbName, String username, String password) {
        this.driverPackage = driverPackage;

        this.driver = driver;
        this.host = host;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
        this.port = 3306;
    }

    public String getDriverPackage() {
        return driverPackage;
    }

    public void setDriverPackage(String driverPackage) {
        this.driverPackage = driverPackage;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return this.dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbPath() {
        String dbPath = "jdbc:" + this.driver + "://" + this.host + "/" + this.dbName;
        return dbPath;
    }
}
