package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class UserMainLayoutController {

    @FXML
    private BorderPane mainContent;

    @FXML
    private VBox centerContent;

    @FXML
    public void initialize() {
        // Affichage du contenu par défaut (page d'accueil)
        showHomeContent();
        // Si vous avez des conditions (par exemple, masquer certaines icônes pour un admin),
        // vous pouvez manipuler directement les ImageView ici, par exemple :
        //homeIcon.setVisible(true);
        // eventIcon.setVisible(true);
        // publicationIcon.setVisible(true);
        // complaintIcon.setVisible(true);
        // logoutIcon.setVisible(true);
    }

    @FXML
    private void handleHome(MouseEvent event) {
        System.out.println("Home icon clicked");
        showHomeContent();
    }

    @FXML
    private void handleEvent(MouseEvent event) {
        System.out.println("Event icon clicked");
        try {
             FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EventAdmin.fxml"));

            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EventContent.fxml"));
            Node eventContent = loader.load();
            centerContent.getChildren().setAll(eventContent);
        } catch (IOException ex) {
            ex.printStackTrace();
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
    private void handleComplaint(MouseEvent event) {
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

    // Méthode pour afficher le contenu "Home"
    private void showHomeContent() {
        VBox homeContent = new VBox(10);
        Label welcomeLabel = new Label("Bienvenue sur la page d'accueil !");
        welcomeLabel.setStyle("-fx-font-size: 24; -fx-text-fill: #333;");
        homeContent.getChildren().add(welcomeLabel);
        centerContent.getChildren().setAll(homeContent);
    }
}
