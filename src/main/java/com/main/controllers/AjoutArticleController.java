package com.main.controllers;


import com.main.Entity.Article;
import com.main.services.ArticleService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class AjoutArticleController {
    private static List<Article> articles = new ArrayList<>();
    private ArticleService articleService = new ArticleService();

    @FXML
    private Label welcomeText;
    @FXML
    private TextField auteurField;
    @FXML
    private TextField titreField;
    @FXML
    private TextArea contenuArea;
    @FXML
    private ImageView imagePreview;
    private File imageFile;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private void onSelectImageClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        imageFile = fileChooser.showOpenDialog(null);
        if (imageFile != null) {
            imagePreview.setImage(new Image(imageFile.toURI().toString()));
            imagePreview.setFitHeight(200);
            imagePreview.setFitWidth(200);
            imagePreview.setPreserveRatio(true);
        }
    }

    @FXML
    private void onAjouterButtonClick() {
        if (!validateInputs()) {
            return;
        }

        try {
            String auteur = auteurField.getText();
            String titre = titreField.getText();
            String contenu = contenuArea.getText();
            
            // Au lieu de stocker l'objet Image, on stocke le chemin de l'image
            String imagePath = null;
            if (imageFile != null) {
                try {
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    String extension = imageFile.getName().substring(imageFile.getName().lastIndexOf("."));
                    imagePath = timestamp + extension;
                    
                    Path destination = Path.of("src/main/resources/uploads/" + imagePath);
                    Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.err.println("Erreur lors de la copie de l'image: " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du traitement de l'image");
                    return;
                }
            }

            Article article = new Article(auteur, titre, contenu, 1L, imagePath);
            articleService.ajouter(article);
            
            // Réinitialiser les champs
            clearFields();
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'article a été ajouté avec succès !");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de l'article: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'ajout de l'article.");
        }
    }

    public void onVoirArticlesClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("articles-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = auteurField.getScene();  // Utiliser la scène existante
        scene.setRoot(root);
    }

    @FXML
    private void onRetourClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/articles-view.fxml"));
            Parent root = fxmlLoader.load();
            // Obtenir la scène actuelle à partir de n'importe quel élément de la vue
            Scene scene = titreField.getScene(); // ou n'importe quel autre élément @FXML
            scene.setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à la liste des articles : ");
            e.printStackTrace();
        }
    }

    public static List<Article> getArticles() {
        return articles;
    }

    public static void setArticles(List<Article> newArticles) {
        articles = newArticles;
    }

    @FXML
    public void initialize() {
        setupValidationListeners();
    }

    private void setupValidationListeners() {
        // Validation en temps réel pour l'auteur
        auteurField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-ZÀ-ÿ\\s]*$")) {
                showErrorMessage("L'auteur ne doit contenir que des lettres et des espaces");
            }
        });

        // Validation en temps réel pour le titre
        titreField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-ZÀ-ÿ\\s]*$")) {
                showErrorMessage("Le titre ne doit contenir que des lettres et des espaces");
            }
        });

        // Validation en temps réel pour la description
        contenuArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                showErrorMessage("La description ne doit pas dépasser 20 caractères");
                // Garder les 20 premiers caractères
                contenuArea.setText(newValue.substring(0, 100));
            }
        });
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        boolean isValid = true;

        // Vérification des champs vides
        if (auteurField.getText().trim().isEmpty()) {
            errorMessage.append("Le champ auteur est obligatoire\n");
            isValid = false;
        }
        if (titreField.getText().trim().isEmpty()) {
            errorMessage.append("Le champ titre est obligatoire\n");
            isValid = false;
        }
        if (contenuArea.getText().trim().isEmpty()) {
            errorMessage.append("Le contenu est obligatoire\n");
            isValid = false;
        }

        // Validation du format des champs
        if (!auteurField.getText().trim().isEmpty() && !auteurField.getText().matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            errorMessage.append("L'auteur ne doit contenir que des lettres et des espaces\n");
            isValid = false;
        }

        if (!titreField.getText().trim().isEmpty() && !titreField.getText().matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            errorMessage.append("Le titre ne doit contenir que des lettres et des espaces\n");
            isValid = false;
        }

        if (!contenuArea.getText().trim().isEmpty() && contenuArea.getText().length() > 100) {
            errorMessage.append("La description ne doit pas dépasser 100 caractères\n");
            isValid = false;
        }

        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errorMessage.toString());
        }

        return isValid;
    }

    private void showErrorMessage(String message) {
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        // Créer un Popup pour afficher le message
        Popup popup = new Popup();
        popup.getContent().add(errorLabel);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        
        // Positionner et afficher le popup
        Node source = auteurField.getScene().getWindow().getScene().getRoot();
        popup.show(source.getScene().getWindow());
        
        // Cacher le popup après 2 secondes
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), evt -> popup.hide()));
        timeline.play();
    }

    private void clearFields() {
        auteurField.clear();
        titreField.clear();
        contenuArea.clear();
        imagePreview.setImage(null);
        imageFile = null;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
