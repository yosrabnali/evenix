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

public class EventCardController {

    @FXML
    private ImageView imageView;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblLieu;
    @FXML
    private Label descriptiontxt;

    @FXML
    private Label lblDate;
    @FXML
    private Label lblPrice; // Label pour le prix dynamique
    @FXML
    private Button btnReserve;
    @FXML
    private Button btncompleted;
    @FXML
    private Button btncanceled;

    private Event event;

    // Propriété observable pour le prix
    private final StringProperty priceProperty = new SimpleStringProperty();



    /**
     * Initialise la carte avec les données de l'événement.
     */
    public void setData(Event event) {
        this.event = event;

        btnReserve.setVisible(false);
        btnReserve.setManaged(false);
        btncompleted.setVisible(false);
        btncompleted.setManaged(false);
        btncanceled.setVisible(false);
        btncanceled.setManaged(false);
        System.out.println("-----------------------------------");

        System.out.println(event.getEtat());
        if ("Disponible".equals(event.getEtat())) {
            btnReserve.setVisible(true);
            btnReserve.setManaged(true);
        } else if ("Annulé".equals(event.getEtat())) {
            btncanceled.setVisible(true);
            btncanceled.setManaged(true);
        } else if ("Complet".equals(event.getEtat())) {
            btncompleted.setVisible(true);
            btncompleted.setManaged(true);
        }


        lblTitle.setText(event.getTitre());
        lblLieu.setText(event.getLieu());
        lblDate.setText(event.getDate().toString());
        descriptiontxt.setText(event.getDescription());
        // Mettre à jour le prix via la propriété observable.
        // Utilisez getPrix() si votre classe Event définit le prix ainsi.
        lblPrice.setText(String.valueOf(event.getPrix())+" dt");

        // Chargement de l'image depuis un fichier local (ajoutez "file:" devant le chemin)
        Image image = new Image("file:" + event.getImage());
        imageView.setImage(image);

        // Optionnel : Arrondir l'image en appliquant un clip
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imageView.setClip(clip);

        // Gestion du clic sur le bouton "Book Now"
        btnReserve.setOnAction((ActionEvent ev) -> {
            System.out.println("Réservation demandée pour l'événement : " + event.getTitre());
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/ReservationDetails.fxml"));
                Parent reservationView = loader.load();

                // Récupère le contrôleur de la page de réservation et lui passe l'événement
                ReservationDetailsController controller = loader.getController();
                controller.setEvent(event);

                // Remplacer le contenu central par la vue de réservation.
                VBox centerContent = (VBox)((Node)ev.getSource()).getScene().lookup("#centerContent");
                centerContent.getChildren().setAll(reservationView);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    // Méthode pour mettre à jour dynamiquement le prix si nécessaire
    public void setPrice(String newPrice) {
        priceProperty.set(newPrice);
    }
}
