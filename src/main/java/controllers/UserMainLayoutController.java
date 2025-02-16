package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;
import java.io.IOException;

public class UserMainLayoutController {

    // Référence au BorderPane principal et à la zone centrale
    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private VBox centerContent;

    @FXML
    public void initialize() {
        // Initialisation du contenu par défaut (page d'accueil)
        showHomeContent();
    }

    @FXML
    private void handleHome(ActionEvent event) {
        showHomeContent();
    }

    @FXML
    private void handleEvent(ActionEvent event) {
        // Charge le contenu des événements (exemple avec un fichier FXML)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EventContent.fxml"));
            Node eventContent = loader.load();
            // Remplace le contenu central
            centerContent.getChildren().setAll(eventContent);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleDiscussion(ActionEvent event) {
        // Charge le contenu de discussion
        VBox discussionContent = new VBox(10);
        discussionContent.getChildren().add(new Label("Discussion Content"));
        centerContent.getChildren().setAll(discussionContent);
    }

    @FXML
    private void handleComplaint(ActionEvent event) {
        // Charge le contenu des plaintes
        VBox complaintContent = new VBox(10);
        complaintContent.getChildren().add(new Label("Complaint Content"));
        centerContent.getChildren().setAll(complaintContent);
    }

    // Méthode d'affichage du contenu "Home"
    private void showHomeContent() {
        VBox homeContent = new VBox(10);
        Label welcomeLabel = new Label("Bienvenue sur la page d'accueil !");
        welcomeLabel.setStyle("-fx-font-size: 24; -fx-text-fill: #333;");
        homeContent.getChildren().add(welcomeLabel);
        centerContent.getChildren().setAll(homeContent);
    }
}
