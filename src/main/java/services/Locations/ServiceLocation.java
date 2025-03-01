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

    // Add a location
    public boolean add(Location location) {
        String query = "INSERT INTO location (idUser, datedebut, datefin, statut) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, location.getUserId());
            pst.setDate(2, Date.valueOf(location.getDatedebut()));
            pst.setDate(3, Date.valueOf(location.getDatefin()));
            pst.setString(4, location.getStatus());  //Pass the status (which you must test)
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding the location: " + e.getMessage());
            return false;
        }
    }

    // Retrieve all locations
    public List<Location> getAll() {
        List<Location> locations = new ArrayList<>();
        String query = "SELECT idlocation, idUser, datedebut, datefin, statut FROM location";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                // Check for null values before calling toLocalDate()
                Date datedebutSql = rs.getDate("datedebut");
                LocalDate startDate = (datedebutSql != null) ? datedebutSql.toLocalDate() : null;

                Date datefinSql = rs.getDate("datefin");
                LocalDate endDate = (datefinSql != null) ? datefinSql.toLocalDate() : null;

                Location location = new Location(
                        rs.getInt("idlocation"),
                        rs.getInt("idUser"),
                        startDate,
                        endDate  // Use LocalDate values
                );
                // Set the field with RS data
                location.setStatus(rs.getString("statut"));

                locations.add(location);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving the locations: " + e.getMessage());
        }
        return locations;
    }

    // Update a location
    public boolean update(Location location) {
        String query = "UPDATE location SET idUser = ?, datedebut = ?, datefin = ?, statut = ? WHERE idlocation = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, location.getUserId());
            pst.setDate(2, Date.valueOf(location.getDatedebut()));
            pst.setDate(3, Date.valueOf(location.getDatefin()));
            pst.setString(4, location.getStatus());  //Pass the status.
            pst.setInt(5, location.getIdlocation()); // Set the parameter
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating the location: " + e.getMessage());
            return false;
        }
    }

    // Delete a location
    public boolean delete(Location location) {
        String query = "DELETE FROM location WHERE idlocation = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, location.getIdlocation());
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting the location: " + e.getMessage());
            return false;
        }
    }
}