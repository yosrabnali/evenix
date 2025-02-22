package services.EventsServices;

import Entity.Events.Event;
import Util.MyDB;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        String req = "INSERT INTO evenement (date, titre, description, NBplaces, prix, etat, type, image, lieu, iduser,latitude,longitude) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
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
            pst.setDouble(11, e.getLatitude());
            pst.setDouble(12, e.getLongitude());
            pst.executeUpdate();
            System.out.println("Event ajouté avec succès !");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // READ (All)
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String req = "SELECT * FROM evenement";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                Event e = new Event();
                e.setIdevent(rs.getInt("idevent"));
                e.setDate(rs.getDate("date")); // java.sql.Date
                e.setTitre(rs.getString("titre"));
                e.setDescription(rs.getString("description"));
                e.setNBplaces(rs.getInt("NBplaces"));
                e.setPrix(rs.getDouble("prix"));
                e.setEtat(rs.getString("etat"));
                e.setType(rs.getString("type"));
                e.setImage(rs.getString("image"));
                e.setLieu(rs.getString("lieu"));
                e.setIduser(rs.getInt("iduser"));
                e.setLatitude(rs.getDouble("latitude"));
                e.setLongitude(rs.getDouble("longitude"));
                events.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Filter out events whose date is before today.
        List<Event> futureEvents = events.stream()
                .filter(event -> {
                    if (event.getDate() == null) {
                        return false;
                    }
                    LocalDate eventDate = event.getDate().toLocalDate();
                    return !eventDate.isBefore(LocalDate.now());
                })
                .collect(Collectors.toList());

        // Sort events in descending order (most recent dates first)
        futureEvents.sort(Comparator.comparing(Event::getDate));

        return futureEvents;
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
                + "etat=?, type=?, image=?, lieu=?, iduser=?, latitude=?, longitude=? WHERE idevent=?";
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
            pst.setDouble(11, e.getLatitude());
            pst.setDouble(12, e.getLongitude());
            pst.setInt(13, e.getIdevent()); // Fix: set the event ID for the WHERE clause

            pst.executeUpdate();
            System.out.println("Event mis à jour avec succès !");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateEvent(int nbplaces, int eventid) {
        String req = "UPDATE evenement SET NBplaces=? WHERE idevent=?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, nbplaces);
            pst.setInt(2, eventid);
            pst.executeUpdate();
            System.out.println("NBplaces mis à jour avec succès !");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public void updateEventEtat(String etat, int eventid) {
        String req = "UPDATE evenement SET etat=? WHERE idevent=?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, etat);
            pst.setInt(2, eventid);
            pst.executeUpdate();
            System.out.println("Etat mis à jour avec succès !");
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