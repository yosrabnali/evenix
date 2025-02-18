package controllers.Events;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import Entity.Events.Event;
import services.EventsServices.ServiceReservation;
import java.math.BigDecimal;

public class ReservationController {

    @FXML private Label eventTitle;
    @FXML private Label eventDate;
    @FXML private Label eventLieu;
    @FXML private Label eventPlaces;
    @FXML private Label eventPrice;
    @FXML private TextField nbPlacesField;
    @FXML private ComboBox<String> paymentMethod;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private Event selectedEvent;
    private final ServiceReservation serviceReservation = new ServiceReservation();

    public void setEventDetails(Event event) {
        this.selectedEvent = event;
        eventTitle.setText(event.getTitre());
        eventDate.setText(event.getDate().toString());
        eventLieu.setText(event.getLieu());
        eventPlaces.setText(String.valueOf(event.getNBplaces()));
        eventPrice.setText(event.getPrix() + "€");
    }

    @FXML
    public void initialize() {
        paymentMethod.getItems().addAll("Carte Bancaire", "PayPal", "Espèces");
        confirmButton.setOnAction(e -> confirmerReservation());
        cancelButton.setOnAction(e -> annulerReservation());
    }

    private void confirmerReservation() {
        try {
            int nbPlaces = Integer.parseInt(nbPlacesField.getText().trim());
            if (nbPlaces <= 0 || nbPlaces > selectedEvent.getNBplaces()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Nombre de places invalide.");
                return;
            }

            serviceReservation.ajouterReservation(selectedEvent, nbPlaces, paymentMethod.getValue());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation confirmée !");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un nombre valide.");
        }
    }

    private void annulerReservation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous annuler ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // Retourner à la liste des événements
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
