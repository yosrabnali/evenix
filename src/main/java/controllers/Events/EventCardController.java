package controllers.Events;

import Entity.Events.Event;
import javafx.animation.ScaleTransition;
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
import javafx.util.Duration;
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
    private Label lblPrice;
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
     * Méthode d'initialisation appelée automatiquement après l'injection des éléments FXML.
     */
    @FXML
    public void initialize() {
        // Animation de mise à l'échelle sur l'image lors du clic
        imageView.setOnMouseClicked(clickEvent -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(300), imageView);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(1.2);
            st.setToY(1.2);
            st.setCycleCount(2); // agrandissement puis retour
            st.setAutoReverse(true);
            st.play();
        });
    }

    /**
     * Initialise la carte avec les données de l'événement.
     */
    public void setData(Event event) {
        this.event = event;

        // Masquer par défaut les boutons
        btnReserve.setVisible(false);
        btnReserve.setManaged(false);
        btncompleted.setVisible(false);
        btncompleted.setManaged(false);
        btncanceled.setVisible(false);
        btncanceled.setManaged(false);

        System.out.println("-----------------------------------");
        System.out.println(event.getEtat());

        // Afficher le bouton approprié selon l'état de l'événement
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
        lblPrice.setText(String.valueOf(event.getPrix()) + " dt");

        // Chargement de l'image depuis un fichier local (ajouter "file:" devant le chemin)
        Image image = new Image("file:" + event.getImage());
        imageView.setImage(image);

        // Arrondir l'image en appliquant un clip
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

                // Récupère le contrôleur de la page de réservation et lui transmet l'événement
                ReservationDetailsController controller = loader.getController();
                controller.setEvent(event);

                // Remplacer le contenu central par la vue de réservation
                VBox centerContent = (VBox) ((Node) ev.getSource()).getScene().lookup("#centerContent");
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
