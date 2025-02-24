package services;

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
    public boolean Ajouter(User user) {
        try {
            // Vérifier si l'email existe déjà
            String checkQuery = "SELECT * FROM utilisateur WHERE email = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, user.getEmail());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("❌ L'email est déjà utilisé !");
                        return false;
                    }
                }
            }

            // Ajouter l'utilisateur
            String query = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, telephone, role) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, user.getNom());
                stmt.setString(2, user.getPrenom());
                stmt.setString(3, user.getEmail());
                stmt.setString(4, user.getMotDePasse());
                stmt.setString(5, user.getTelephone());
                stmt.setString(6, user.getRole().getRoleName()); // ✅ Utilisation de getRoleName()
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✅ Utilisateur ajouté avec succès !");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean Modifier(User user) {
        try {
            String query;
            if (user.getMotDePasse() == null || user.getMotDePasse().isEmpty()) {
                // Ne pas mettre à jour le mot de passe
                query = "UPDATE utilisateur SET nom = ?, prenom = ?, telephone = ?, role = ? WHERE email = ?";
            } else {
                // Mettre à jour le mot de passe
                query = "UPDATE utilisateur SET nom = ?, prenom = ?, mot_de_passe = ?, telephone = ?, role = ? WHERE email = ?";
            }

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, user.getNom());
                stmt.setString(2, user.getPrenom());
                if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
                    stmt.setString(3, user.getMotDePasse());
                    stmt.setString(4, user.getTelephone());
                    stmt.setString(5, user.getRole().getRoleName());
                    stmt.setString(6, user.getEmail());
                } else {
                    stmt.setString(3, user.getTelephone());
                    stmt.setString(4, user.getRole().getRoleName());
                    stmt.setString(5, user.getEmail());
                }
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✅ Utilisateur modifié avec succès !");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la modification de l'utilisateur : " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean Supprimer(int Id) {
        try {
            String query = "DELETE FROM utilisateur WHERE iduser = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Id);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✅ Utilisateur supprimé !");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression de l'utilisateur : " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean Supprimer(String email) {
        try {
            String query = "DELETE FROM utilisateur WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✅ Utilisateur supprimé !");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression de l'utilisateur : " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<User> Recuperer() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM utilisateur";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("iduser"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("telephone"),
                        Role.fromString(rs.getString("role")) // ✅ Conversion de String en Role
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération des utilisateurs : " + e.getMessage());
        }
        return users;
    }

    // Méthode pour authentifier un utilisateur
    public User authenticate(String email, String password) {
        String query = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("iduser"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("telephone"),
                            Role.fromString(rs.getString("role"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'authentification : " + e.getMessage());
        }
        return null;
    }

    // Méthode pour récupérer un utilisateur par son email
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM utilisateur WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("iduser"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("telephone"),
                            Role.fromString(rs.getString("role"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération de l'utilisateur par email : " + e.getMessage());
        }
        return null;
    }
}