package controllers.Events;

import Entity.Events.Event;
import services.EventsServices.ServiceEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.io.IOException;

public class EventClientController implements Initializable {

    @FXML
    private FlowPane flowPaneEvents; // Ce nœud doit être défini dans le FXML avec fx:id="flowPaneEvents"

    private ServiceEvent serviceEvent = new ServiceEvent();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Vérifiez l'injection du FlowPane
        if (flowPaneEvents == null) {
            System.err.println("Erreur : flowPaneEvents n'est pas injecté. Vérifiez le fichier FXML.");
        } else {
            loadEvents();
        }
    }
    private void loadEvents() {
        List<Event> events = serviceEvent.getAllEvents();
        flowPaneEvents.getChildren().clear();

        for (Event e : events) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EventCard.fxml"));
                VBox card = loader.load();

                // Récupère le contrôleur de la carte et initialise ses données
                EventCardController cardController = loader.getController();
                cardController.setData(e);

                flowPaneEvents.getChildren().add(card);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Navigue vers la page de détails de réservation en passant l'événement sélectionné.
     */
    private void navigateToReservationDetails(Event event, ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/ReservationDetails.fxml"));
            Parent root = loader.load();

            // Récupère le contrôleur de la page de réservation et lui passe l'événement
            ReservationDetailsController controller = loader.getController();
            controller.setEvent(event);

            // Changer la scène pour afficher la page de réservation
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
