package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.Interpolator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import java.io.IOException;

public class UserMainLayoutController {

    @FXML
    private BorderPane mainContent;

    @FXML
    private VBox centerContent;

    @FXML
    private ImageView evenix;

    @FXML
    private ImageView eventIcon;

    @FXML
    private ImageView publicationIcon;

    @FXML
    private ImageView complaintIcon;

    @FXML
    private ImageView materielIcon;

    @FXML
    private ImageView logoutIcon;

    private static UserMainLayoutController instance;

    public UserMainLayoutController() {
        if (instance == null) {
            instance = this;
        }
    }

    public static UserMainLayoutController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        startRotationEvery2Seconds();

        Tooltip.install(eventIcon, new Tooltip("Event"));
        Tooltip.install(publicationIcon, new Tooltip("Publications"));
        Tooltip.install(materielIcon, new Tooltip("Material"));
        Tooltip.install(complaintIcon, new Tooltip("Complaints"));
        Tooltip.install(logoutIcon, new Tooltip("Logout"));
    }

    public void setCenterContent(Node node) {
        if (mainContent != null) {
            mainContent.setCenter(node);
        } else {
            System.out.println("❌ Erreur : mainContent n'est pas encore initialisé !");
        }
    }

    private void startRotationEvery2Seconds() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> rotateImage())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void rotateImage() {
        RotateTransition rotate = new RotateTransition(Duration.seconds(0.5), evenix);
        rotate.setByAngle(360);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();
    }

    @FXML
    private void handleEvent(MouseEvent event) {
        System.out.println("Event icon clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EventContent.fxml"));
            Node eventContent = loader.load();
            centerContent.getChildren().setAll(eventContent);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleComplaint(MouseEvent event) {
        System.out.println("Complaint icon clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamations/AddReclamation.fxml"));
            Node complaintContent = loader.load();
            centerContent.getChildren().setAll(complaintContent);
        } catch (IOException ex) {
            ex.printStackTrace();
            // Optionally, you can show an error message to the user
            Label errorLabel = new Label("Error loading publication content");
            centerContent.getChildren().setAll(errorLabel);
        }
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
        System.out.println("Materiel icon clicked");
        VBox materielContent = new VBox(10);
        materielContent.getChildren().add(new Label("Materiel Content"));
        centerContent.getChildren().setAll(materielContent);
    }

    @FXML
    private void handleLogout(MouseEvent event) {
        System.out.println("Logout icon clicked");
        VBox logoutContent = new VBox(10);
        logoutContent.getChildren().add(new Label("Vous avez été déconnecté."));
        centerContent.getChildren().setAll(logoutContent);
    }
}
