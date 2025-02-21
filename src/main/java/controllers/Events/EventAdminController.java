package controllers.Events;

import Entity.Events.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import services.EventsServices.ServiceEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EventAdminController implements Initializable {

    @FXML
    private FlowPane flowPaneEvents; // Conteneur pour afficher les événements

    @FXML
    private Button btnAddEvent;

    private final ServiceEvent serviceEvent = new ServiceEvent();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadEvents();
    }
    private void loadEvents() {
        List<Event> events = serviceEvent.getAllEvents();

        flowPaneEvents.getChildren().clear();

        for (Event e : events) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EventCardAdmin.fxml"));
                // Charge la carte (un VBox dans ce cas)
                StackPane card = loader.load();

                // Récupère le contrôleur de la carte et initialise ses données
                EventCardAdminController cardController = loader.getController();
                cardController.setData(e);
                cardController.setParentController(this);

                // Ajoute la carte au HBox pour un affichage horizontal
                flowPaneEvents.getChildren().add(card);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

     void deleteEvent(Event event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Confirmation");
        confirmAlert.setHeaderText("Delete Event");
        confirmAlert.setContentText("Are you sure you want to delete this event?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            serviceEvent.deleteEvent(event.getIdevent());
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Deleted");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("Event deleted successfully!");
            infoAlert.showAndWait();
            loadEvents(); // Recharger les événements après suppression
        }
    }


    @FXML
    private void handleAddEvent(ActionEvent event) {
        try {
            Parent newContent = FXMLLoader.load(getClass().getResource("/Events/AddEvent.fxml"));
            flowPaneEvents.getChildren().setAll(newContent);
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the add event screen.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
