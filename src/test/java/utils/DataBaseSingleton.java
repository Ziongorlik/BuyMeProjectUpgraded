package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/***
 * The Class manage database connection instance
 */
public class DataBaseSingleton {
    private static Connection con;
    private static final String USER_NAME = "Up7KuhcCQh";
    private static final String DATABASE_NAME = "Up7KuhcCQh";
    private static final String PASSWORD = "Ha4tQ3oiry";
    private static final String PORT = "3306";
    private static final String SERVER = "remotemysql.com";

    public static Connection getConnection() throws SQLException {
        if (con == null){
            con = DriverManager.getConnection("jdbc:mysql://" + SERVER + ":" + PORT, USER_NAME, PASSWORD);
        }

        return con;
    }

    public static String getDatabaseName() {
        return DATABASE_NAME;
    }
}
