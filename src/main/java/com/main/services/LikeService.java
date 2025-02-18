package com.main.services;


import com.main.Entity.Like;
import com.main.Util.MyDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LikeService implements IService<Like> {

    private final Connection connection = MyDB.getInstance().getConnection();

    @Override
    public void ajouter(Like like) {
        String req = "INSERT INTO `like` (article_id, user_id) VALUES (?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setLong(1, like.getArticleId());
            pst.setLong(2, like.getUserId());

            pst.executeUpdate();
            System.out.println("Like ajouté avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du like: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Like like) {
        System.out.println("Les likes ne peuvent pas être modifiés.");
    }

    @Override
    public void supprimer(Like like) {
        String req = "DELETE FROM `Like` WHERE article_id = ? AND user_id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setLong(1, like.getArticleId());
            pst.setLong(2, like.getUserId());
            pst.executeUpdate();
            System.out.println("Like supprimé avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du like: " + e.getMessage());
        }
    }

    @Override
    public List<Like> rechercher() {
        List<Like> likes = new ArrayList<>();
        String req = "SELECT * FROM `Like`";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Like like = new Like(
                        rs.getLong("id"),
                        rs.getLong("article_id"),
                        rs.getLong("user_id")
                );
                likes.add(like);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des likes: " + e.getMessage());
        }
        return likes;
    }
}
