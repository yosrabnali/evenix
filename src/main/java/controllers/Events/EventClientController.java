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
    private FlowPane flowPaneEvents;

    private ServiceEvent serviceEvent = new ServiceEvent();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadEvents();
    }

    private void loadEvents() {
        List<Event> events = serviceEvent.getAllEvents();
        flowPaneEvents.getChildren().clear();

        // Pour chaque événement, on crée une "carte" avec l'image, le titre, le lieu et la date, ainsi qu'un bouton "Réserver"
        for (Event e : events) {
            VBox card = new VBox(5);
            card.setStyle("-fx-border-color: gray; -fx-padding: 5; -fx-alignment: center;");

            // Affichage de la photo
            String imagePath = e.getImage();
            Image image = new Image("file:" + imagePath, 100, 100, true, true);
            ImageView imageView = new ImageView(image);

            // Affichage du titre
            Label lblTitle = new Label(e.getTitre());

            // Affichage du lieu
            Label lblLieu = new Label(e.getLieu());

            // Affichage de la date (formatée, ici en toString, à adapter si besoin)
            Label lblDate = new Label(e.getDate().toString());

            // Bouton Réserver
            Button btnReserve = new Button("Réserver");
            btnReserve.setOnAction(ev -> {
                navigateToReservationDetails(e, ev);
            });

            card.getChildren().addAll(imageView, lblTitle, lblLieu, lblDate, btnReserve);
            flowPaneEvents.getChildren().add(card);
        }
    }

    /**
     * Navigue vers la page de détails de l'événement pour la réservation.
     */
    private void navigateToReservationDetails(Event event, ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/ReservationDetails.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui passer l'événement
            ReservationDetailsController controller = loader.getController();
            controller.setEvent(event);

            // Changer de scène
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
