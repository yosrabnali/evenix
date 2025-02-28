package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.Interpolator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import java.io.IOException;


public class UserMainLayoutController {

    @FXML
    private BorderPane mainContent;

    @FXML
    private VBox centerContent;

    // ImageView pour le logo qui sera animé
    @FXML
    private ImageView evenix;

    // Boutons de la barre latérale gauche
    @FXML
    private ImageView eventIcon;

    @FXML
    private ImageView publicationIcon;

    @FXML
    private ImageView materielIcon;

    @FXML
    private ImageView logoutIcon;

    @FXML
    public void initialize() {
        startRotationEvery2Seconds();
        // Affichage du contenu par défaut (page d'accueil)
        showHomeContent();

        // Installation des tooltips pour les boutons de la barre latérale gauche
        Tooltip.install(eventIcon, new Tooltip("Event"));
        Tooltip.install(publicationIcon, new Tooltip("Publications"));
        Tooltip.install(materielIcon, new Tooltip("Material"));
        Tooltip.install(logoutIcon, new Tooltip("Logout"));
    }
    @FXML
    private void handleHome(MouseEvent event) {
        System.out.println("Home icon clicked");
        showHomeContent();
    }

    private void startRotationEvery2Seconds() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> rotateImage()) // Exécute la rotation toutes les 2 secondes
        );
        timeline.setCycleCount(Timeline.INDEFINITE); // Répète indéfiniment
        timeline.play();
    }

    private void rotateImage() {
        RotateTransition rotate = new RotateTransition(Duration.seconds(0.5), evenix); // Rotation en 0.5 sec
        rotate.setByAngle(360);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();
    }



    @FXML
    private void handleEvent(MouseEvent event) {
        System.out.println("Event icon clicked");
        VBox publicationContent = new VBox(10);
        publicationContent.getChildren().add(new Label("Event Content"));
        centerContent.getChildren().setAll(publicationContent);
    }
    @FXML
    private void handlePublication(MouseEvent event) {
        System.out.println("Publication icon clicked");
        VBox publicationContent = new VBox(10);
        publicationContent.getChildren().add(new Label("Publication Content"));
        centerContent.getChildren().setAll(publicationContent);
    }

    @FXML
    private void handlemateriel(MouseEvent event) {
        System.out.println("Complaint icon clicked");
        VBox complaintContent = new VBox(10);
        complaintContent.getChildren().add(new Label("Complaint Content"));
        centerContent.getChildren().setAll(complaintContent);
    }

    @FXML
    private void handleLocation(MouseEvent event) {
        System.out.println("Location icon clicked");
        System.out.println("Rent icon clicked (Loading LocationAdmin.fxml)");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Location/LocationAdmin.fxml")); // Assuming LocationAdmin.fxml is in the Location folder
            Node locationAdminContent = loader.load();
            centerContent.getChildren().setAll(locationAdminContent);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error dialog)
        }
    }
    @FXML
    private void handleLogout(MouseEvent event) {
        System.out.println("Logout icon clicked");
        VBox logoutContent = new VBox(10);
        logoutContent.getChildren().add(new Label("Vous avez été déconnecté."));
        centerContent.getChildren().setAll(logoutContent);
    }

    // Méthode pour afficher le contenu "Event"
    private void showHomeContent() {
        VBox homeContent = new VBox(10);
        Label welcomeLabel = new Label("Hello good people !");
        welcomeLabel.setStyle("-fx-font-size: 24; -fx-text-fill: #333;");
        homeContent.getChildren().add(welcomeLabel);
        centerContent.getChildren().setAll(homeContent);
    }
}