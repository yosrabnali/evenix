package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDB {
    private static final String URL = "jdbc:mysql://localhost:3306/evenix";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static MyDB instance;
    private Connection connection;

    private MyDB() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion à la base de données établie.");
        } catch (SQLException e) {
            System.out.println("❌ Erreur de connexion : " + e.getMessage());
        }
    }

    public static MyDB getInstance() {
        if (instance == null) {
            instance = new MyDB();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("🔄 Connexion rétablie.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la réouverture de la connexion : " + e.getMessage());
        }
        return connection;
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
