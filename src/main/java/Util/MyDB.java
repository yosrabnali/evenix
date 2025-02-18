package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDB {

    private static final String USER = "root";
    private static final String PWD = "";
    private static final String URL = "jdbc:mysql://localhost:3306/evenix";

    private static Connection connection;
    private static MyDB instance;

    // Constructeur privé pour empêcher l'instanciation multiple
    private MyDB() {
        try {
            connection = DriverManager.getConnection(URL, USER, PWD);
            connection.setAutoCommit(true);  // Active l'auto-commit
            System.out.println("✅ Connexion à la base de données réussie.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
            throw new RuntimeException("Échec de connexion à la base de données", e);
        }
    }

    // Méthode pour récupérer l'instance unique
    public static MyDB getInstance() {
        if (instance == null) {
            instance = new MyDB();
        }
        return instance;
    }

    // Retourne la connexion à la base de données
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                reconnect();
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de la connexion : " + e.getMessage());
            reconnect();
        }
        return connection;
    }

    // Méthode privée pour rétablir la connexion
    private static void reconnect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PWD);
            connection.setAutoCommit(true);
            System.out.println("🔄 Reconnexion à la base de données réussie.");
        } catch (SQLException e) {
            System.err.println("❌ Impossible de reconnecter à la base de données : " + e.getMessage());
            throw new RuntimeException("Échec de la reconnexion à la base de données", e);
        }
    }
}
