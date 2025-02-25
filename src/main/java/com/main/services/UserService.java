package com.main.services;

import com.main.Entity.Role;
import com.main.Entity.User;
import com.main.Util.MyDB;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;
import java.util.*;

public class UserService implements IService<User> {
    private Connection connection;

    public UserService() {
        this.connection = MyDB.getInstance().getConnection();
    }

    @Override
    public void ajouter(User user) throws Exception {

    }

    @Override
    public void modifier(User user) throws Exception {

    }

    @Override
    public boolean supprimer(User user) throws Exception {
        return false;
    }

    @Override
    public List<User> rechercher() throws Exception {
        return List.of();
    }


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

    // Méthode pour sauvegarder le token de réinitialisation
    public void saveResetToken(String email, String resetToken) {
        String query = "UPDATE utilisateur SET reset_token = ? WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, resetToken);
            stmt.setString(2, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la sauvegarde du token de réinitialisation : " + e.getMessage());
        }
    }

    // Méthode pour récupérer un utilisateur par son token de réinitialisation
    public User getUserByResetToken(String resetToken) {
        String query = "SELECT * FROM utilisateur WHERE reset_token = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, resetToken);
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
            System.err.println("Erreur SQL lors de la récupération de l'utilisateur par token : " + e.getMessage());
        }
        return null;
    }

    // Méthode pour mettre à jour le mot de passe
    // Méthode pour mettre à jour le mot de passe par numéro de téléphone
    public boolean updatePasswordByPhoneNumber(String phoneNumber, String newPassword) {
        String query = "UPDATE utilisateur SET mot_de_passe = ? WHERE telephone = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, phoneNumber);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour du mot de passe par numéro de téléphone : " + e.getMessage());
        }
        return false;
    }

    public User getUserById(int id) {
        String query = "SELECT * FROM utilisateur WHERE iduser = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
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
            System.err.println("Erreur SQL lors de la récupération de l'utilisateur par ID : " + e.getMessage());
        }
        return null; // Retourne null si l'utilisateur n'est pas trouvé
    }
    // Méthode pour récupérer un utilisateur par son numéro de téléphone
    public User findByPhoneNumber(String phoneNumber) {
        String query = "SELECT * FROM utilisateur WHERE telephone = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, phoneNumber);
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
            System.err.println("Erreur SQL lors de la récupération de l'utilisateur par numéro de téléphone : " + e.getMessage());
        }
        return null;
    }

    public class TokenGenerator {
        public static String genererToken() {
            SecureRandom random = new SecureRandom();
            return new BigInteger(130, random).toString(32);
        }
    }

        private Map<String, String> tokenStorage = new HashMap<>(); // Stockage temporaire des tokens

        public boolean emailExiste(String email) {
            // Vérifie si l'email existe dans la base de données (remplace par une vraie requête SQL)
            return true;
        }

        public String genererToken(String email) {
            String token = new BigInteger(130, new SecureRandom()).toString(32);
            tokenStorage.put(token, email);
            return token;
        }

        public boolean resetPassword(String token, String newPassword) {
            if (tokenStorage.containsKey(token)) {
                String email = tokenStorage.get(token);
                // Met à jour le mot de passe dans la base de données (remplace par une vraie requête SQL)
                System.out.println("Mot de passe réinitialisé pour : " + email);
                tokenStorage.remove(token);
                return true;
            }
            return false;
        }

}