package controllers.Events;

import Entity.Events.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



import Entity.Events.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class EventCardController {

    @FXML private ImageView imageView;
    @FXML private Label lblTitle;
    @FXML private Label lblLieu;
    @FXML private Label lblDate;
    @FXML private Button btnReserve;

    private Event event;

    /**
     * Initialise la carte avec les données de l'événement.
     */
    public void setData(Event event) {
        this.event = event;
        lblTitle.setText(event.getTitre());
        lblLieu.setText(event.getLieu());
        lblDate.setText(event.getDate().toString());
        Image image = new Image("file:" + event.getImage(), 100, 100, true, true);
        imageView.setImage(image);

        btnReserve.setOnAction((ActionEvent ev) -> {
            System.out.println("Réservation demandée pour l'événement : " + event.getTitre());
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/ReservationDetails.fxml"));
                Parent reservationView = loader.load();

                // Récupère le contrôleur de la page de réservation et lui passe l'événement
                ReservationDetailsController controller = loader.getController();
                controller.setEvent(event);

                // Plutôt que de changer la scène entière, on recherche le conteneur actuel par son fx:id (ici "centerContent")
                // Assurez-vous que l'id "centerContent" est bien défini dans votre layout principal.
                VBox centerContent = (VBox)((Node)ev.getSource()).getScene().lookup("#centerContent");
                centerContent.getChildren().setAll(reservationView);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

    }
}
