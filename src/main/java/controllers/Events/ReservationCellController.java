package controllers.Events;

import Entity.Events.Event;
import Entity.Events.Reservation;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import services.EventsServices.ServiceReservation;

import java.sql.SQLException;

public class ReservationCellController {

    @FXML
    private Label lblReservationId;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblNbPlaces;
    @FXML
    private Label lblPaymentMethod;
    @FXML
    private Label lblEventName;
    @FXML
    private ImageView eventImageView;
    private Reservation reservation;
    private ServiceReservation serviceReservation;
    private Runnable refreshCallback;
    public void setReservationData(Reservation reservation, Event event, Runnable refreshCallback) {
        this.reservation = reservation;
        this.refreshCallback = refreshCallback; // Store the refresh callback

        lblReservationId.setText("ID: " + reservation.getIdReservation());
        lblDate.setText("Date: " + reservation.getDate().toString());
        lblNbPlaces.setText("Places: " + reservation.getNbPlaces());
        lblPaymentMethod.setText("Payment: " + reservation.getModePaiement());
        if (event != null) {
            lblEventName.setText("Event: " + event.getTitre());
            // Assuming the event image file name is stored in event.getImage()
            if (event.getImage() != null && !event.getImage().isEmpty()) {
                // The image is loaded from the /images/ folder in your resources
                Image image = new Image("file:" + event.getImage(), 100, 100, true, true);

                // Image img = new Image(getClass().getResourceAsStream("/images/" + event.getImage()));
                eventImageView.setImage(image);
            }
        } else {
            lblEventName.setText("Event: N/A");
        }
    }

    @FXML
    private void handleDelete() {
        if (reservation == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this reservation?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Delete Confirmation");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    serviceReservation = new ServiceReservation();
                    serviceReservation.supprimerReservation(reservation.getIdReservation());

                    if (refreshCallback != null) {
                        refreshCallback.run(); // Refresh the list after deletion
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to delete the reservation.");
                    errorAlert.show();
                }
            }
        });
    }
}
