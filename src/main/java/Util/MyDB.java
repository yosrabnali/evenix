package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDB {

    private static final String USER = "root";
    private static final String PWD = "";
    private static final String URL = "jdbc:mysql://localhost:3306/evenix";
    private static Connection connection;
    public static MyDB instance;

    public MyDB() {
        try {
            connection = DriverManager.getConnection(URL, USER, PWD);
            connection.setAutoCommit(true);  // Activation de l'auto-commit
            System.out.println("Connected to DB with auto-commit enabled.");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public static MyDB getInstance() {
        if (instance == null) {
            instance = new MyDB();
        }
        return instance;
    }

    public Connection getConnectionX() {
        return connection;
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                reconnect();
            }
        } catch (SQLException e) {
            System.out.println("Failed to check connection status: " + e.getMessage());
            reconnect();
        }
        return connection;
    }

    private static void reconnect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PWD);
            connection.setAutoCommit(true);
            System.out.println("Reconnected to the database.");
        } catch (SQLException e) {
            System.out.println("Failed to reconnect to the database: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }

}