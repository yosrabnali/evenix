package controllers.Events;

import Entity.Events.Event;
import Entity.Events.Reservation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    public void setReservationData(Reservation reservation, Event event) {
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
}
