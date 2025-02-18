package services.Locations;

import Entity.Locations.Location;
import Util.MyDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceLocation {
    private Connection con;

    public ServiceLocation() {
        con = MyDB.getInstance().getConnection();
    }

    // Ajouter une location
    public boolean add(Location location) {
        String query = "INSERT INTO location (idUser, datedebut, datefin) VALUES (?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, location.getIdUser());
            pst.setDate(2, Date.valueOf(location.getDatedebut()));
            pst.setDate(3, Date.valueOf(location.getDatefin()));
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;  // Return true if at least one row was affected
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la location : " + e.getMessage());
            return false;
        }
    }

    // Récupérer toutes les locations
    public List<Location> getAll() {
        List<Location> locations = new ArrayList<>();
        String query = "SELECT * FROM location";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                // Check for null values before calling toLocalDate()
                Date datedebutSql = rs.getDate("datedebut");
                LocalDate datedebut = (datedebutSql != null) ? datedebutSql.toLocalDate() : null;

                Date datefinSql = rs.getDate("datefin");
                LocalDate datefin = (datefinSql != null) ? datefinSql.toLocalDate() : null;

                Location location = new Location(
                        rs.getInt("idlocation"),
                        rs.getInt("idUser"), // Get idUser from result set
                        datedebut,  // Use LocalDate values
                        datefin      // Use LocalDate values
                );
                locations.add(location);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des locations : " + e.getMessage());
        }
        return locations;
    }

    // Modifier une location
    public boolean update(Location location) {
        String query = "UPDATE location SET idUser = ?, datedebut = ?, datefin = ? WHERE idlocation = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, location.getIdUser());
            pst.setDate(2, Date.valueOf(location.getDatedebut()));
            pst.setDate(3, Date.valueOf(location.getDatefin()));
            pst.setInt(4, location.getIdlocation());
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;  // Return true if at least one row was affected
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la location : " + e.getMessage());
            return false;
        }
    }

    // Supprimer une location
    public boolean delete(Location location) {
        String query = "DELETE FROM location WHERE idlocation = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, location.getIdlocation());
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;  // Return true if at least one row was affected
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la location : " + e.getMessage());
            return false;
        }
    }
}