package com.main.controllers;

import com.main.Entity.Article;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.main.services.PublicationService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class AddPostController {

    @FXML
    private TextField auteurField;

    @FXML
    private TextArea contenuArea;

    @FXML
    private ImageView imagePreview;

    @FXML
    private TextField titreField;

    private File imageFile;

    private Article materielToEdit = null;
    private String resourcesPath = "src/main/resources/uploads/";
    private String imagePath = "";
    private final PublicationService materielService = new PublicationService();

    // 🔥 Ajout de l'instance de PostController
    private PostController postController;

    // 🔥 Méthode pour passer l'instance de PostController
    public void setPostController(PostController controller) {
        this.postController = controller;
    }

    @FXML
    void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (auteurField.getText().isEmpty() || titreField.getText().isEmpty() ||
                contenuArea.getText().isEmpty() || imagePath.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            return;
        }

        try {
            String auteur = auteurField.getText();
            String titre = titreField.getText();
            String contenu = contenuArea.getText();

            if (materielToEdit == null) {
                // 🔹 Ajouter un nouveau Article
                // uploadImage -> resources / uploads
                //
                Image image = imagePreview.getImage();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                String  newFileName = resourcesPath+UUID.randomUUID().toString() + ".png";
                File imageFilePath = new File(newFileName);
                ImageIO.write(bufferedImage,"PNG", imageFilePath);
                //

                Article article = new Article(auteur, titre, contenu, 1L, newFileName);
                materielService.ajouter(article);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Article added successfully!");
            } else {
                // 🔹 Modifier le matériel existant
                materielToEdit.setAuteur(auteur);
                materielToEdit.setTitre(titre);
                materielToEdit.setImage(imagePath);
                materielToEdit.setContenu(contenu);

                materielService.modifier(materielToEdit);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Article updated successfully!");

                // 🔄 Utilisation de l'instance de PostController pour mettre à jour la carte
                postController.setChosenMateriel(materielToEdit);
            }

            closeWindow(event);

            // 🔄 Utilisation de l'instance de PostController pour rafraîchir la liste
            postController.loadMaterials();

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding/updating.");
        }
    }

    private void closeWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close(); // ❌ Ferme l'onglet d'ajout
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void onSelectImageClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        imageFile = fileChooser.showOpenDialog(null);
        if (imageFile != null) {
            imagePath = imageFile.toURI().toString(); // 🔥 Met à jour imagePath
            imagePreview.setImage(new Image(imagePath));
            imagePreview.setFitHeight(200);
            imagePreview.setFitWidth(200);
            imagePreview.setPreserveRatio(true);
        }
    }
}
