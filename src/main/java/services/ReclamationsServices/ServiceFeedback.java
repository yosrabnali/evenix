package services.ReclamationsServices;

import Entity.Reclamations.Feedback;
import Util.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceFeedback {

    private Connection conn;

    public ServiceFeedback() {
        this.conn = MyDB.getInstance().getConnection();
    }

    /**
     * ✅ Ajouter un feedback
     */
    public void addFeedback(Feedback feedback) throws SQLException {
        String query = "INSERT INTO feedback(description, etat, date, idreclamation) VALUES(?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, feedback.getDescription());
            stmt.setString(2, feedback.getEtat());
            stmt.setDate(3, feedback.getDate());
            stmt.setInt(4, feedback.getIdreclamation());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Adding the feedback failed, no rows affected.");
            }
        }
    }

    /**
     * ✅ Récupérer tous les feedbacks
     */
    public List<Feedback> getAllFeedbacks() throws SQLException {
        List<Feedback> feedbacks = new ArrayList<>();
        String query = "SELECT * FROM feedback";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                feedbacks.add(new Feedback(
                        rs.getInt("idfeedback"),
                        rs.getString("description"),
                        rs.getString("etat"),
                        rs.getDate("date"),
                        rs.getInt("idreclamation") // ✅ Correction : ajout de `idreclamation`
                ));
            }
        }
        return feedbacks;
    }

    /**
     * ✅ Mettre à jour un feedback
     */
    public void updateFeedback(Feedback feedback) throws SQLException {
        String query = "UPDATE feedback SET description = ?, etat = ?, date = ?, idreclamation = ? "
                + "WHERE idfeedback = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, feedback.getDescription());
            stmt.setString(2, feedback.getEtat());
            stmt.setDate(3, feedback.getDate());
            stmt.setInt(4, feedback.getIdreclamation());
            stmt.setInt(5, feedback.getIdfeedback());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Update failed, no matching feedback found.");
            }
        }
    }

    /**
     * ✅ Supprimer un feedback
     */
    public void deleteFeedback(int idfeedback) throws SQLException {
        String query = "DELETE FROM feedback WHERE idfeedback = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idfeedback);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new SQLException("Deletion failed, no matching feedback found.");
            }
        }
    }

    /**
     * ✅ Vérifier si une réclamation existe
     */
    public boolean checkReclamationExists(int idReclamation) throws SQLException {
        String query = "SELECT COUNT(*) FROM reclamation WHERE idreclamation = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idReclamation);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * ✅ Récupérer les feedbacks filtrés par date
     */
    public List<Feedback> getFeedbacksByDate(Date date) throws SQLException {
        List<Feedback> feedbacks = new ArrayList<>();
        String query = "SELECT * FROM feedback WHERE date = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) { // ✅ Correction de `con` en `conn`
            ps.setDate(1, date);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                feedbacks.add(new Feedback(
                        rs.getInt("idfeedback"),
                        rs.getString("description"),
                        rs.getString("etat"),
                        rs.getDate("date"),
                        rs.getInt("idreclamation") // ✅ Correction du constructeur
                ));
            }
        }
        return feedbacks;
    }

    public List<Feedback> getFeedbacksByEtat(String etat) throws SQLException {
        List<Feedback> feedbacks = new ArrayList<>();
        String query = "SELECT * FROM feedback WHERE etat = ?";
        try (PreparedStatement pstmt = MyDB.getInstance().getConnection().prepareStatement(query)) {
            pstmt.setString(1, etat);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                feedbacks.add(new Feedback(
                        rs.getInt("idfeedback"),
                        rs.getString("description"),
                        rs.getString("etat"),
                        rs.getDate("date"),
                        rs.getInt("idreclamation") // ✅ Correction du constructeur
                ));
            }
        }
        return feedbacks;
    }

}
