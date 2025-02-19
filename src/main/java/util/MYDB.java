package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MYDB {
    private final String URL = "jdbc:mysql://localhost:3306/evenix-1";
    private final String USER = "root";
    private final String PASSWORD = "";
    private static Connection connection;
    private static MYDB instance;

    private MYDB() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion établie ✅");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion ❌");
            e.printStackTrace();
        }
    }

    public static MYDB getInstance() {
        if (instance == null)
            instance = new MYDB();
        return instance;
    }

    public static Connection getConnection() {
        return connection;
    }
}