package Entity.Events;

import java.math.BigDecimal;
import java.util.Date;

public class Reservation {
    private int idReservation;
    private int idEvent;
    private int idUser;
    private Date date;
    private int nbPlaces;
    private BigDecimal montant;
    private String modePaiement;
    private String etatReservation;

    public Reservation(int idReservation, int idEvent, int idUser, Date date, int nbPlaces, BigDecimal montant, String modePaiement, String etatReservation) {
        this.idReservation = idReservation;
        this.idEvent = idEvent;
        this.idUser = idUser;
        this.date = date;
        this.nbPlaces = nbPlaces;
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.etatReservation = etatReservation;
    }
    public Reservation( int idEvent, int idUser, Date date, int nbPlaces, BigDecimal montant, String modePaiement, String etatReservation) {

        this.idEvent = idEvent;
        this.idUser = idUser;
        this.date = date;
        this.nbPlaces = nbPlaces;
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.etatReservation = etatReservation;
    }

    // Getters & Setters
    public int getIdReservation() { return idReservation; }
    public void setIdReservation(int idReservation) { this.idReservation = idReservation; }

    public int getIdEvent() { return idEvent; }
    public void setIdEvent(int idEvent) { this.idEvent = idEvent; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getNbPlaces() { return nbPlaces; }
    public void setNbPlaces(int nbPlaces) { this.nbPlaces = nbPlaces; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }

    public String getEtatReservation() { return etatReservation; }
    public void setEtatReservation(String etatReservation) { this.etatReservation = etatReservation; }
}
