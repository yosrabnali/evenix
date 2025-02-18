package com.main.services;



import com.main.Entity.Commentaire;
import com.main.Util.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class CommentaireService implements IService<Commentaire> {

    private final Connection connection = MyDB.getInstance().getConnection();

    @Override
    public void ajouter(Commentaire commentaire) throws SQLException {
        String req = "INSERT INTO commentaire (contenu, article_id, user_id, auteur, created_at) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, commentaire.getContenu());
            pst.setLong(2, commentaire.getArticleId());
            pst.setLong(3, commentaire.getUserId());
            pst.setString(4, commentaire.getAuteur());
            pst.setTimestamp(5, Timestamp.valueOf(commentaire.getCreatedAt()));
            
            pst.executeUpdate();
            System.out.println("Commentaire ajouté avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du commentaire: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(Commentaire commentaire) {
        String req = "UPDATE commentaire SET contenu=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, commentaire.getContenu());
            pst.setLong(2, commentaire.getId());

            pst.executeUpdate();
            System.out.println("Commentaire modifié avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du commentaire: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Commentaire commentaire) {
        String req = "DELETE FROM commentaire WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setLong(1, commentaire.getId());
            pst.executeUpdate();
            System.out.println("Commentaire supprimé avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du commentaire: " + e.getMessage());
        }
    }

    @Override
    public List<Commentaire> rechercher() throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        String req = "SELECT * FROM commentaire";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Commentaire commentaire = new Commentaire();
                commentaire.setId(rs.getLong("id"));
                commentaire.setContenu(rs.getString("contenu"));
                commentaire.setArticleId(rs.getLong("article_id"));
                commentaire.setUserId(rs.getLong("user_id"));
                commentaire.setAuteur(rs.getString("auteur"));
                
                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    commentaire.setCreatedAt(timestamp.toLocalDateTime());
                } else {
                    commentaire.setCreatedAt(LocalDateTime.now());
                }
                
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche des commentaires: " + e.getMessage());
            throw e;
        }
        return commentaires;
    }
}
