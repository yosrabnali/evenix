package controllers.Events;

import Entity.Events.Event;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

public class EventCardAdminController {

    @FXML
    private ImageView imageView;
    @FXML
    private Label lblTitle;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnEdit;

    private Event event;

    // Propriété observable pour le prix
    private final StringProperty priceProperty = new SimpleStringProperty();

    private EventAdminController parentController;

    public void setParentController(EventAdminController parentController) {
        this.parentController = parentController;
    }

    /**
     * Initialise la carte avec les données de l'événement.
     */
    public void setData(Event event) {
        this.event = event;
        lblTitle.setText(event.getTitre());


        // Chargement de l'image depuis un fichier local (ajoutez "file:" devant le chemin)
        Image image = new Image("file:" + event.getImage());
        imageView.setImage(image);

        // Optionnel : Arrondir l'image en appliquant un clip
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imageView.setClip(clip);

        // Gestion du clic sur le bouton "Book Now"
        btnEdit.setOnAction((ActionEvent ev) -> {
            System.out.println("Réservation demandée pour l'événement : " + event.getTitre());
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EditEvent.fxml"));
                Parent reservationView = loader.load();

                // Récupère le contrôleur de la page de réservation et lui passe l'événement
                EditEventController controller = loader.getController();
                controller.setEvent(event);

                // Remplacer le contenu central par la vue de réservation.
                VBox centerContent = (VBox)((Node)ev.getSource()).getScene().lookup("#centerContent");
                centerContent.getChildren().setAll(reservationView);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        btnDelete.setOnAction((ActionEvent ev) -> {
            if (parentController != null) {
                parentController.deleteEvent(event);
            }
        });
    }

}
