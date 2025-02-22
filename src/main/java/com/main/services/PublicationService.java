package com.main.services;

import com.main.Entity.Article;
import com.main.Util.MyDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class PublicationService implements IService<Article> {
    private final Connection connection = MyDB.getInstance().getConnection();
    private PreparedStatement pst;

    @Override
    public void ajouter(Article article) throws SQLException {
        String req = "INSERT INTO articles (titre, contenu, auteur, user_id, created_at) VALUES (?, ?, ?, ?, ?)";
        if (hasImageColumn()) {
            req = "INSERT INTO articles (titre, contenu, auteur, user_id, image, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        }
        
        try {
            pst = connection.prepareStatement(req);
            pst.setString(1, article.getTitre());
            pst.setString(2, article.getContenu());
            pst.setString(3, article.getAuteur());
            pst.setLong(4, article.getUserId());
            
            if (hasImageColumn()) {
                pst.setString(5, article.getImage());
                pst.setTimestamp(6, Timestamp.valueOf(article.getCreatedAt()));
            } else {
                pst.setTimestamp(5, Timestamp.valueOf(article.getCreatedAt()));
            }
            
            pst.executeUpdate();
            System.out.println("Article ajouté avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'article: " + e.getMessage());
            throw e;
        }
    }

    private boolean hasImageColumn() {
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SHOW COLUMNS FROM articles LIKE 'image'");
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void modifier(Article article) throws SQLException {
        String req = "UPDATE articles SET titre=?, contenu=?, image=? WHERE id=?";
        try {
            pst = connection.prepareStatement(req);
            pst.setString(1, article.getTitre());
            pst.setString(2, article.getContenu());
            pst.setString(3, article.getImage()); // Stocke le chemin de l'image comme String
            pst.setLong(4, article.getId());
            pst.executeUpdate();
            System.out.println("Article modifié avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l'article: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean supprimer(Article article)  {
        try {
            // D'abord supprimer tous les commentaires associés
            String deleteCommentsQuery = "DELETE FROM commentaire WHERE article_id = ?";
            PreparedStatement deleteComments = connection.prepareStatement(deleteCommentsQuery);
            deleteComments.setLong(1, article.getId());
            deleteComments.executeUpdate();

            // Ensuite supprimer les likes associés
            String deleteLikesQuery = "DELETE FROM `like` WHERE article_id = ?";
            PreparedStatement deleteLikes = connection.prepareStatement(deleteLikesQuery);
            deleteLikes.setLong(1, article.getId());
            deleteLikes.executeUpdate();

            // Enfin, supprimer l'article
            String deleteArticleQuery = "DELETE FROM articles WHERE id = ?";
            PreparedStatement deleteArticle = connection.prepareStatement(deleteArticleQuery);
            deleteArticle.setLong(1, article.getId());
            deleteArticle.executeUpdate();

            System.out.println("Article et ses dépendances supprimés avec succès");
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'article: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Article> rechercher(){
        List<Article> articles = new ArrayList<>();
        String req = "SELECT * FROM articles";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Article article = new Article();
                article.setId(rs.getLong("id"));
                article.setTitre(rs.getString("titre"));
                article.setContenu(rs.getString("contenu"));
                article.setAuteur(rs.getString("auteur"));
                article.setUserId(rs.getLong("user_id"));
                
                // Gérer le cas où la colonne image n'existe pas encore
                try {
                    String imagePath = rs.getString("image");
                    article.setImage(imagePath);
                } catch (SQLException e) {
                    // Si la colonne n'existe pas, on continue sans image
                    article.setImage(null);
                }
                
                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    article.setCreatedAt(timestamp.toLocalDateTime());
                } else {
                    article.setCreatedAt(LocalDateTime.now());
                }
                
                articles.add(article);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche des articles: " + e.getMessage());

        }
        return articles;
    }

    public Article getById(Long id) throws SQLException {
        String req = "SELECT * FROM articles WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Article article = new Article();
                article.setId(rs.getLong("id"));
                article.setTitre(rs.getString("titre"));
                article.setAuteur(rs.getString("auteur"));
                article.setContenu(rs.getString("contenu"));
                article.setImage(rs.getString("image")); // Récupère le chemin de l'image comme String
                
                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    article.setCreatedAt(timestamp.toLocalDateTime());
                } else {
                    article.setCreatedAt(LocalDateTime.now());
                }
                
                return article;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'article : " + e.getMessage());
            throw e;
        }
        return null;
    }
}
