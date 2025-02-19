package services;

import com.mysql.cj.jdbc.DatabaseMetaData;
import com.mysql.cj.jdbc.MysqlDataSource;
import entities.Role;
import entities.User;
import util.MYDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {
    private Connection connection;

    public UserService() {
        this.connection = MYDB.getInstance().getConnection();
    }

    @Override
    public void Ajouter(User user) {
        try {
            String checkQuery = "SELECT * FROM utilisateur WHERE email = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, user.getEmail());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("❌ L'email est déjà utilisé !");
                return;
            }

            String query = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, telephone, role) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getMotDePasse());
            stmt.setString(5, user.getTelephone());
            stmt.setString(6, user.getRole().getRoleName());  // Utilisation du nom du rôle
            stmt.executeUpdate();
            System.out.println("✅ Utilisateur ajouté avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Modifier(User user) {
        try {
            String query = "UPDATE utilisateur SET nom = ?, prenom = ?, mot_de_passe = ?, telephone = ?, role = ? WHERE email = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getMotDePasse());
            stmt.setString(4, user.getTelephone());
            stmt.setString(5, user.getRole().getRoleName());  // Utilisation du nom du rôle
            stmt.setString(6, user.getEmail());
            stmt.executeUpdate();
            System.out.println("✅ Utilisateur modifié avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean Supprimer(int Id) {
        return false;
    }

    @Override
    public boolean Supprimer(String email) {
        try {
            String query = "DELETE FROM utilisateur WHERE email = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, email);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Utilisateur supprimé !");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<User> Recuperer() {
        List<User> users = new ArrayList<>();
        try {
            String query = "SELECT * FROM utilisateur";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                users.add(new User(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("telephone"),
                        Role.fromString(rs.getString("role"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;

    }
    public boolean authenticateUser(String email, String password) {
        String query = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);  // Assurez-vous de sécuriser le mot de passe (voir ci-dessous)

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                // L'utilisateur a été trouvé, authentification réussie
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Si aucun utilisateur n'est trouvé ou en cas d'erreur
        return false;
    }


    public User getUserByEmail(String email) {
        try {
            String query = "SELECT * FROM utilisateur WHERE email = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("telephone"),
                        Role.fromString(rs.getString("role"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // Si l'utilisateur n'est pas trouvé
    }



}