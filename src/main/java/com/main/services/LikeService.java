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
        String req = "INSERT INTO `like` (article_id, user_id,Reactiontype) VALUES (?, ?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setLong(1, like.getArticleId());
            pst.setLong(2, like.getUserId());
            pst.setLong(3, like.getReactiontype());

            pst.executeUpdate();
            System.out.println("Like ajouté avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du like: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Like obj) throws Exception {
        String req = "UPDATE `like` SET article_id =?, user_id =?, Reactiontype =? WHERE id =?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setLong(1,obj.getArticleId());
        ps.setLong(2,obj.getUserId());
        ps.setLong(3,obj.getReactiontype());
        ps.setLong(4,obj.getId());
        ps.executeUpdate();
        ps.close();

    }

    @Override
    /*public boolean supprimer(Like like) {
        String req = "DELETE FROM `Like` WHERE article_id = ? AND user_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setLong(1, like.getArticleId());
            ps.setLong(2, like.getUserId());
            ps.executeUpdate();
            System.out.println("Like supprimé avec succès");
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du like: " + e.getMessage());
            return false;
        }
    }*/
    public boolean supprimer(Like like)throws Exception {
        String req = "DELETE FROM `like` WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setLong(1, like.getId());
        ps.executeUpdate();
        ps.close();
        return true;

    }

    @Override
    public List<Like> rechercher() throws Exception {
        List<Like> likes = new ArrayList<>();
        String req = "SELECT * FROM `Like`";
        PreparedStatement pst = connection.prepareStatement(req);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            Like like = new Like(
                    rs.getLong("id"),
                    rs.getLong("article_id"),
                    rs.getLong("user_id"),
                    rs.getLong("Reactiontype")

            );
            likes.add(like);

        }
        return likes;
    }

    public Like getUserReaction(int publicationId, int userId) throws Exception  {
        String req = "SELECT * FROM `like` WHERE article_id =? AND user_id =?";
        PreparedStatement pst = connection.prepareStatement(req);
        pst.setLong(1, publicationId);
        pst.setLong(2, userId);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            Like like = new Like(
                    rs.getLong("id"),
                    rs.getLong("article_id"),
                    rs.getLong("user_id"),
                    rs.getLong("Reactiontype")
            );
            return like;
        }

        return null;
    }

    public void toggleReaction(long publicationId, long userId, long newReactionType) {
        try {
            Like existReaction = getUserReaction((int) publicationId, (int) userId);
            if (existReaction == null) {
                ajouter(new Like(publicationId, userId, newReactionType));
            } else if (existReaction.getReactiontype() == newReactionType) {
                supprimer(existReaction);
            } else {
                existReaction.setReactiontype(newReactionType);
                modifier(existReaction);
            }
        } catch (Exception e) {
            System.out.println("Error toggling reaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int countReactions(long publicationId)throws Exception  {
       // String req = " SELECT COUNT(*) as total FROM  ' Like ' WHERE article_id =? AND Reactiontype <> ?";
        String req = "SELECT COUNT(*) as total FROM `Like` WHERE article_id = ? AND Reactiontype <> ?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setLong(1, publicationId);
        ps.setLong(2, 0); // Ignore likes without reaction
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("total");
        }

        return 0;
    }

    public List<Integer> getTop3Reactions(long publicationId, long userId) throws Exception {
      //  String req = "SELECT  Reactiontype COUNT(*) as total FROM  'Like ' WHERE article_id =? AND Reactiontype <>? GROUP BY Reactiontype ORDER BY total DESC LIMIT 3";
        String req = "SELECT Reactiontype, COUNT(*) as total FROM `Like` WHERE article_id = ? AND Reactiontype <> ? GROUP BY Reactiontype ORDER BY total DESC LIMIT 3";

        PreparedStatement ps = connection.prepareStatement(req);
        ps.setLong(1, publicationId);
        ps.setLong(2, userId);
        ResultSet rs = ps.executeQuery();
        List<Integer> topReactions = new ArrayList<>();
        while (rs.next()) {
            topReactions.add(rs.getInt("Reactiontype"));
        }
        return topReactions;
    }
}
