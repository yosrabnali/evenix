package controllers.Events;

import Entity.Events.Event;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.StackPane;
import services.EventsServices.ServiceEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.io.IOException;
import java.util.stream.Collectors;

import static java.util.Locale.filter;

public class EventContentController implements Initializable {

    // Remplacez FlowPane par HBox pour un affichage en ligne (album horizontal)
    @FXML
    private HBox hboxEvents; // Assurez-vous que fx:id="hboxEvents" est défini dans votre FXML pour l'onglet "Events"

    private ServiceEvent serviceEvent = new ServiceEvent();

    // Pour l'onglet "Reservations", vous pouvez aussi utiliser un HBox (ou conserver FlowPane si vous préférez)
    @FXML
    private HBox hboxReservation; // fx:id="hboxReservation" dans votre FXML
    @FXML
    private Button searchButton; // Search button

    @FXML
    private Button resetButton; // Reset button

    @FXML
    private ComboBox<String> eventCategoryComboBox;

    @FXML
    private ComboBox<String> lieuComboBox;


    private List<Event> allEvents; // Store all events for filtering

    @FXML
    private DatePicker datePicker;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (hboxEvents == null) {
            System.err.println("Erreur : hboxEvents n'est pas injecté. Vérifiez le fichier FXML.");
        } else {
            loadEvents();
        }
    }

    private void loadEvents() {
        List<Event> events = serviceEvent.getAllEvents();
        hboxEvents.getChildren().clear();

        for (Event e : events) {
            try {
                allEvents = serviceEvent.getAllEvents();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EventCard.fxml"));
                // Charge la carte (un VBox dans ce cas)
                StackPane card = loader.load();

                // Récupère le contrôleur de la carte et initialise ses données
                EventCardController cardController = loader.getController();
                cardController.setData(e);

                // Ajoute la carte au HBox pour un affichage horizontal
                hboxEvents.getChildren().add(card);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Méthode de navigation vers la page de détails de réservation en passant l'événement sélectionné.
     */
    private void navigateToReservationDetails(Event event, ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/ReservationDetails.fxml"));
            Parent root = loader.load();

            // Récupère le contrôleur de la page de réservation et lui passe l'événement
            ReservationDetailsController controller = loader.getController();
            controller.setEvent(event);

            // Change la scène pour afficher la page de réservation
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Affiche la liste des réservations dans l'onglet "Reservations".
     */
    @FXML
    private void showReservationList() {
        System.out.println("showReservationList");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/ReservationList.fxml"));
            AnchorPane reservationPane = loader.load();
            hboxReservation.getChildren().clear();
            hboxReservation.getChildren().add(reservationPane);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void displayEvents(List<Event> events) {
        hboxEvents.getChildren().clear();
        for (Event e : events) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EventCard.fxml"));
                StackPane card = loader.load();

                EventCardController cardController = loader.getController();
                cardController.setData(e);

                hboxEvents.getChildren().add(card);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void filterEvents() {
        String selectedCategory = eventCategoryComboBox.getValue();
        String selectedlieu = lieuComboBox.getValue();

        LocalDate selectedDate = datePicker.getValue();

        List<Event> filteredEvents = allEvents.stream()
                // Filter by category if provided
                .filter(event -> selectedCategory == null || selectedCategory.isEmpty() ||
                        event.getType().toString().equalsIgnoreCase(selectedCategory))
                .filter(event -> selectedlieu == null || selectedlieu.isEmpty() ||
                        event.getLieu().toString().equalsIgnoreCase(selectedlieu))
                // Filter by date if provided
                .filter(event -> {
                    if (selectedDate == null) {
                        return true;
                    } else {
                        // event.getDate() returns a java.sql.Date
                        java.sql.Date eventDate = event.getDate();
                        LocalDate eventLocalDate = eventDate.toLocalDate(); // Use toLocalDate() directly
                        return eventLocalDate.equals(selectedDate);
                    }
                })
                .collect(Collectors.toList());

        displayEvents(filteredEvents);
    }



    /**
     * Reset the filter and display all events again.
     */
    @FXML
    private void resetFilter() {
        eventCategoryComboBox.setValue(null);
        displayEvents(allEvents);
    }
}
