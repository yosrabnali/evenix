package services.EventsServices;

import Entity.Events.Event;
import Entity.Events.Reservation;
import Util.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ServiceReservation {
    private Connection con;

    public ServiceReservation() {
        con = MyDB.getInstance().getConnection();
    }

    public void ajouterReservation(Reservation reservation) throws SQLException {
        String req = "INSERT INTO reservation (date, NBplace, Montant, Modepaiement, idevent) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setDate(1, new java.sql.Date(reservation.getDate().getTime()));
        pre.setInt(2, reservation.getNbPlaces());
        pre.setBigDecimal(3, reservation.getMontant());
        pre.setString(4, reservation.getModePaiement());
        pre.setInt(5, reservation.getIdEvent());
        pre.executeUpdate();
    }


    public List<Reservation> afficherReservationsByUser(int idUser) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation WHERE iduser = ?";
        PreparedStatement pst = con.prepareStatement(req);
        pst.setInt(1, idUser);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            Reservation reservation = new Reservation(
                    rs.getInt("idreservation"),
                    rs.getDate("date"),
                    rs.getInt("NBplace"),
                    rs.getBigDecimal("Montant"),
                    rs.getString("Modepaiement"),
                    rs.getInt("idevent")
            );
            reservations.add(reservation);
        }
        reservations.sort(Comparator.comparing(Reservation::getDate).reversed());
        return reservations;
    }


    public void supprimerReservation(int id) throws SQLException {
        String req = "DELETE FROM reservation WHERE idReservation = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, id);
        pre.executeUpdate();
    }

    public void modifierReservation(Reservation reservation) throws SQLException {
        String req = "UPDATE reservation SET idEvent = ?, idUser = ?, date = ?, nbPlaces = ?, montant = ?, modePaiement = ?, etatReservation = ? WHERE idReservation = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, reservation.getIdEvent());
        pre.setInt(2, reservation.getIdUser());
        pre.setDate(3, new java.sql.Date(reservation.getDate().getTime()));
        pre.setInt(4, reservation.getNbPlaces());
        pre.setBigDecimal(5, reservation.getMontant());
        pre.setString(6, reservation.getModePaiement());
        pre.setString(7, reservation.getEtatReservation());
        pre.setInt(8, reservation.getIdReservation());
        pre.executeUpdate();
    }
}