package services.EventsServices;

import Entity.Events.Event;
import Entity.Events.Reservation;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation {
    private List<Reservation> reservations;

    public ServiceReservation() {
        this.reservations = new ArrayList<>();
    }

    // Ajouter une réservation
    public void ajouterReservation(Reservation reservation) {
        reservations.add(reservation);
        System.out.println("Réservation ajoutée avec succès !");
    }

    // Afficher toutes les réservations
    public List<Reservation> afficherReservations() {
        return reservations;
    }

    // Modifier une réservation existante
    public boolean modifierReservation(int id, Reservation nouvelleReservation) {
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getIdReservation() == id) {
                reservations.set(i, nouvelleReservation);
                System.out.println("Réservation mise à jour !");
                return true;
            }
        }
        System.out.println("Réservation non trouvée !");
        return false;
    }

    // Supprimer une réservation
    public boolean supprimerReservation(int id) {
        return reservations.removeIf(reservation -> reservation.getIdReservation() == id);
    }

    // Récupérer une réservation par ID
    public Reservation getReservationById(int id) {
        for (Reservation r : reservations) {
            if (r.getIdReservation() == id) {
                return r;
            }
        }
        return null;
    }

    public void ajouterReservation(Event selectedEvent, int nbPlaces, String value)
    {
    }
}
