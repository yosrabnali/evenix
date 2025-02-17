package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainController {

    @FXML
    private Button homeButton, profileButton, eventButton, discussionButton, complaintButton, uploadImageButton;

    @FXML
    private ImageView profileImageView;

    @FXML
    public void initialize() {
        // Configuration des images pour les boutons
        try {
            homeButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/resources/images/home.png"))));
            profileButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/profile.png"))));
            eventButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/event.png"))));
            discussionButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/discussion.png"))));
            complaintButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/complaint.png"))));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des icônes : " + e.getMessage());
            e.printStackTrace();
        }

        // Image de profil par défaut
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/profile.png"));
            profileImageView.setImage(defaultImage);
        } catch (Exception e) {
            System.out.println("Image par défaut introuvable.");
        }

        // Gestion du bouton d'upload d'image
        uploadImageButton.setOnAction(e -> uploadProfileImage());
    }

    private void uploadProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo de profil");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        // Utilisation du stage existant pour ouvrir le file chooser
        Stage stage = (Stage) uploadImageButton.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                Image profileImage = new Image(new FileInputStream(selectedFile));
                profileImageView.setImage(profileImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
