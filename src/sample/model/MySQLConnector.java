package sample.model;

import java.sql.*;

import java.sql.DriverManager;

public class MySQLConnector {

    public static Connection conn;
    private static final String driver = "com.mysql.jdbc.Driver";
    //String driver = "";
    private static final String db = "U04n6X";
    private static final String url = "jdbc:mysql://52.206.157.109/ " + db;
    private static final String user = "U04n6X";
    private static final String pass = "53688291768";

    public static void makeConnection() throws Exception {

        try {
            //Class.forName(driver);
            conn = (Connection) DriverManager.getConnection(url, user, pass);
            System.out.println("Connected to database : " + db);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }

    }

}
