package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import java.io.IOException;

public class UserMainLayoutController {

    @FXML
    private StackPane contentPane;
    @FXML
    private Button btnHome, btnProfile, btnEvent, btnDiscussions, btnComplaint, btnLogout;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblUserName;

    @FXML
    public void initialize() {
        // Au chargement, on peut afficher une page d'accueil
        loadViewInCenter("/views/UserHome.fxml"); // Par ex. si tu as un HomeView

        // Boutons de la sidebar
        btnHome.setOnAction(e -> loadViewInCenter("/views/UserHome.fxml"));
        btnProfile.setOnAction(e -> loadViewInCenter("/views/Profile.fxml"));
        btnEvent.setOnAction(e -> loadViewInCenter("/Events/EventClient.fxml"));
        btnDiscussions.setOnAction(e -> loadViewInCenter("/views/Discussions.fxml"));
        btnComplaint.setOnAction(e -> loadViewInCenter("/views/Complaint.fxml"));

        // Bouton Logout
        btnLogout.setOnAction(e -> {
            System.out.println("Logout clicked");
            // Par exemple, revenir à la page login ou fermer la fenêtre
        });
    }

    private void loadViewInCenter(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
