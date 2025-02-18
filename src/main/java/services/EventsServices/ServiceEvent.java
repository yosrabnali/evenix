package services.EventsServices;

import Entity.Events.Event;
import Util.MyDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvent {

    private Connection cnx;

    public ServiceEvent() {
        // Récupère la connexion depuis la classe MyDB (Singleton)
        this.cnx = MyDB.getInstance().getConnection();
    }

    // CREATE
    public void addEvent(Event e) {
        // Table "evenement" et colonnes : idevent (auto-incr), date, titre, description, NBplaces, prix,
        // etat, type, image, lieu, iduser
        String req = "INSERT INTO evenement (date, titre, description, NBplaces, prix, etat, type, image, lieu, iduser) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setDate(1, e.getDate());          // java.sql.Date
            pst.setString(2, e.getTitre());
            pst.setString(3, e.getDescription());
            pst.setInt(4, e.getNBplaces());
            pst.setDouble(5, e.getPrix());
            pst.setString(6, e.getEtat());
            pst.setString(7, e.getType());
            pst.setString(8, e.getImage());
            pst.setString(9, e.getLieu());
            pst.setInt(10, e.getIduser());
            pst.executeUpdate();
            System.out.println("Event ajouté avec succès !");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // READ (All)
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        // Sélection de tous les champs de la table "evenement"
        String req = "SELECT * FROM evenement";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                Event e = new Event();
                e.setIdevent(rs.getInt("idevent"));     // PK
                e.setDate(rs.getDate("date"));          // java.sql.Date
                e.setTitre(rs.getString("titre"));
                e.setDescription(rs.getString("description"));
                e.setNBplaces(rs.getInt("NBplaces"));
                e.setPrix(rs.getDouble("prix"));
                e.setEtat(rs.getString("etat"));
                e.setType(rs.getString("type"));
                e.setImage(rs.getString("image"));
                e.setLieu(rs.getString("lieu"));
                e.setIduser(rs.getInt("iduser"));
                events.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return events;
    }
    public Event getEventById(int id) {
        String req = "SELECT * FROM evenement WHERE idevent = ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Event e = new Event();
                e.setIdevent(rs.getInt("idevent"));
                e.setDate(rs.getDate("date"));
                e.setTitre(rs.getString("titre"));
                e.setDescription(rs.getString("description"));
                e.setNBplaces(rs.getInt("NBplaces"));
                e.setPrix(rs.getDouble("prix"));
                e.setEtat(rs.getString("etat"));
                e.setType(rs.getString("type"));
                e.setImage(rs.getString("image"));
                e.setLieu(rs.getString("lieu"));
                e.setIduser(rs.getInt("iduser"));
                return e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE
    public void updateEvent(Event e) {
        String req = "UPDATE evenement SET date=?, titre=?, description=?, NBplaces=?, prix=?, "
                + "etat=?, type=?, image=?, lieu=?, iduser=? WHERE idevent=?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setDate(1, e.getDate());
            pst.setString(2, e.getTitre());
            pst.setString(3, e.getDescription());
            pst.setInt(4, e.getNBplaces());
            pst.setDouble(5, e.getPrix());
            pst.setString(6, e.getEtat());
            pst.setString(7, e.getType());
            pst.setString(8, e.getImage());
            pst.setString(9, e.getLieu());
            pst.setInt(10, e.getIduser());
            pst.setInt(11, e.getIdevent());
            pst.executeUpdate();
            System.out.println("Event mis à jour avec succès !");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // DELETE
    public void deleteEvent(int idevent) {
        String req = "DELETE FROM evenement WHERE idevent=?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, idevent);
            pst.executeUpdate();
            System.out.println("Event supprimé avec succès !");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}