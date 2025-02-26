package com.main.controllers;

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
       // showEventContent();


        // Installation des tooltips pour les boutons de la barre latérale gauche
        Tooltip.install(eventIcon, new Tooltip("Event"));
        Tooltip.install(publicationIcon, new Tooltip("Publications"));
        Tooltip.install(materielIcon, new Tooltip("Material"));
        Tooltip.install(logoutIcon, new Tooltip("Logout"));
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
       /* System.out.println("Event icon clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EventAdmin.fxml"));
          // FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EventContent.fxml"));
            Node eventContent = loader.load();
            centerContent.getChildren().setAll(eventContent);
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
    }
    @FXML
    private void handlePublication(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Publication/Post-view.fxml"));
            Node publicationContent = loader.load();
            centerContent.getChildren().setAll(publicationContent);
        } catch (IOException ex) {
            ex.printStackTrace();
            // Optionally, you can show an error message to the user
            Label errorLabel = new Label("Error loading publication content");
            centerContent.getChildren().setAll(errorLabel);
    }}

    @FXML
    private void handlemateriel(MouseEvent event) {
        System.out.println("Complaint icon clicked");
        VBox complaintContent = new VBox(10);
        complaintContent.getChildren().add(new Label("Complaint Content"));
        centerContent.getChildren().setAll(complaintContent);
    }

    @FXML
    private void handleLogout(MouseEvent event) {
        System.out.println("Logout icon clicked");
        VBox logoutContent = new VBox(10);
        logoutContent.getChildren().add(new Label("Vous avez été déconnecté."));
        centerContent.getChildren().setAll(logoutContent);
    }

    // Méthode pour afficher le contenu "Event"
    private void showEventContent() {
        try {
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EventContent.fxml"));
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EventAdmin.fxml"));
            Node eventContent = loader.load();
            centerContent.getChildren().setAll(eventContent);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
