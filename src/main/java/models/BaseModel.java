package models;

import config.Database;
import config.Default;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.InitException;

public class BaseModel {
    public static void open() {
        Database c = Default.getDatabaseConfig();
        try {
            Base.open(c.getDriverPackage(), c.getDbPath(), c.getUsername(), c.getPassword());
        } catch (InitException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        Base.close();
    }
}
