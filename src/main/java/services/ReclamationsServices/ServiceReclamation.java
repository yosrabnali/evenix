package services.ReclamationsServices;

import Entity.Reclamations.Reclamation;
import Util.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation {
    private Connection con;

    public ServiceReclamation() {
        con = MyDB.getInstance().getConnection();
    }

    public void ajouterReclamation(Reclamation r) throws SQLException {
        String req = "INSERT INTO reclamation (iduser, description, titre, date, fichier, idevent) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, r.getIduser());
            ps.setString(2, r.getDescription());
            ps.setString(3, r.getTitre());
            ps.setDate(4, r.getDate());
            ps.setString(5, r.getFichier());
            ps.setInt(6, r.getIdevent()); // Ajout de idevent
            ps.executeUpdate();
        }
    }

    public List<Reclamation> afficherReclamations() throws SQLException {
        List<Reclamation> list = new ArrayList<>();
        String req = "SELECT idreclamation, iduser, description, titre, date, fichier, idevent FROM reclamation";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(req);
        while (rs.next()) {
            list.add(new Reclamation(
                    rs.getInt("idreclamation"),
                    rs.getInt("iduser"),
                    rs.getString("description"),
                    rs.getString("titre"),
                    rs.getDate("date"),
                    rs.getString("fichier"),
                    rs.getInt("idevent") // Ajout de idevent
            ));
        }
        return list;
    }

    public void modifierReclamation(Reclamation r) throws SQLException {
        String req = "UPDATE reclamation SET description=?, titre=?, date=?, fichier=?, idevent=? WHERE idreclamation=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, r.getDescription());
            ps.setString(2, r.getTitre());
            ps.setDate(3, r.getDate());
            ps.setString(4, r.getFichier());
            ps.setInt(5, r.getIdevent()); // Ajout de idevent
            ps.setInt(6, r.getIdreclamation());
            ps.executeUpdate();
        }
    }

    public void supprimerReclamation(int id) throws SQLException {
        String req = "DELETE FROM reclamation WHERE idreclamation=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}