package com.main.controllers;

import com.main.Entity.Article;
import com.main.Util.CloudinaryUploader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.main.services.PublicationService;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PostController {


    @FXML
    private Button ClearBtn, RentBTN, deleteBtn, modifyBtn, searchBtn;
    @FXML
    private Label AuteurLabel, ContenuLabel, TitreLablel ;
    @FXML
    private ImageView PostImg , imgProfile , AddMaterialBTN;
    @FXML
    private VBox chosenMaterialCard;
    @FXML
    private VBox articlecontainer;
    @FXML
    private HBox addImg ;
    @FXML
    private ScrollPane scroll;
    @FXML
    private TextField searchField;
    @FXML
    private TextArea commentinput;

    PublicationService publicationService = new PublicationService();
    private Article selectedMateriel;

    private String resourcesPath = "src/main/resources/uploads/";
    private String UploadedImageUrl;

    public String getUploadedImageUrl() {
        return UploadedImageUrl;
    }

    public void setUploadedImageUrl(String uploadedImageUrl) {
        UploadedImageUrl = uploadedImageUrl;
    }

    /*public PostController(){
                loadMaterials();
            }
        */
    public void initialize() {
        loadMaterials();
        chosenMaterialCard.getStyleClass().add("chosenMaterialCard");
        if (deleteBtn != null) {
            deleteBtn.setOnAction(event -> deleteSelectedMateriel());
        }

        clearChosenMaterial();
    }

    @FXML
    void addImgPost(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier Image", "*.png", "*.jpg", "*.jpeg")
        );
        File imgFile = fileChooser.showOpenDialog(null);
        String imageUrl = CloudinaryUploader.uploadImage(imgFile.getAbsolutePath());
        if(imageUrl != null) {
            UploadedImageUrl = imageUrl;
            showWarningDialog("Image Uploaded");
        }
    }

    @FXML
    void openAddMatView(MouseEvent event) {
        if (commentinput.getText().equals("")) {
            showWarningDialog("Status Vide :(");
        } else {
            Article p = new Article();
            p.setContenu(commentinput.getText());
            p.setUserId((long)1);
            p.setAuteur("Yesmine");
            p.setTitre("Post");
            if (getUploadedImageUrl() != null) {
                p.setImage(getUploadedImageUrl());
                System.out.println("Image URL : " + getUploadedImageUrl());
            } /*else if (getUploadedVideoUrl() != null) {
                p.setType_pub("video");
                p.setContenu(getUploadedVideoUrl());
                System.out.println("Video URL : " + getUploadedVideoUrl());
            }*/ else {
                p.setImage("");
            }
            try {
                publicationService.ajouter(p);
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadMaterials();
        }
    }

    public void loadMaterials() {
        try {
            List<Article> articles = publicationService.rechercher();
            articlecontainer.getChildren().clear();
            for (Article article : articles) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/Publication/Article.fxml"));
                VBox vbox = fxmlLoader.load();
                ArticleController controller = fxmlLoader.getController();
                controller.setArticleId(article.getId());
                controller.setData(article.getId());
                articlecontainer.getChildren().add(vbox);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createMaterialCard(Article m) {
        VBox card = new VBox();
        card.setPrefSize(400, 400);
        card.setMinSize(400, 400);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("Material-item");

        // Titre de l'article
        Label titreLabel = new Label(m.getTitre());
        titreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Image de l'article
        ImageView imageView = new ImageView();
        imageView.setFitHeight(200);
        imageView.setFitWidth(300);
        File imageFile = new File(m.getImage());
        imageView.setImage(imageFile.exists() ? new Image(imageFile.toURI().toString()) : new Image("file:src/main/resources/images/default.png"));

        // Ajout des éléments dans la carte
        card.getChildren().addAll(titreLabel, imageView);
        card.setOnMouseClicked(event -> setChosenMateriel(m));

        return card;
    }

    public void setChosenMateriel(Article article) {
        if (article == null) return;
        selectedMateriel = article;
        TitreLablel.setText("Titre:" + article.getTitre());
        AuteurLabel.setText("Auteur: " + article.getAuteur());
        ContenuLabel.setText("Contenu: " + article.getContenu());

        File imageFile = new File(article.getImage());
        PostImg.setImage(imageFile.exists() ? new Image(imageFile.toURI().toString()) : new Image("file:src/main/resources/uploads/default.png"));

        modifyBtn.setDisable(false);
        deleteBtn.setDisable(false);
        ClearBtn.setDisable(false);
        chosenMaterialCard.setVisible(true);
        chosenMaterialCard.setManaged(true);
    }

    private void deleteSelectedMateriel() {
        if (selectedMateriel == null) {
            showWarningDialog("⚠️ No Publication selected!");
            return;
        }

        // Demande de confirmation avant suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deletion Confirmation");
        alert.setHeaderText("Do you really want to delete this publication?");
        alert.setContentText("This action is irreversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Article articleP = selectedMateriel;
            boolean deleted = publicationService.supprimer(articleP); // Supprimer dans la BD

            if (deleted) {
                clearChosenMaterial(); // Vider les détails affichés
                loadMaterials(); // Rafraîchir la liste
            } else {
                showWarningDialog("❌ Deletion failed!");
            }
        }
    }

    /**
     * ✅ Afficher un avertissement
     */
    private void showWarningDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearChosenMaterial() {
        selectedMateriel = null; // Désélectionner

        if (AuteurLabel != null) AuteurLabel.setText("");
        if (ContenuLabel != null) ContenuLabel.setText("");
        if (TitreLablel != null) TitreLablel.setText("");
        if (PostImg != null) PostImg.setImage(null);

        // Désactiver les boutons
        if (modifyBtn != null) modifyBtn.setDisable(true);
        if (deleteBtn != null) deleteBtn.setDisable(true);
        if (ClearBtn != null) ClearBtn.setDisable(true);

        // Masquer la carte des détails
        chosenMaterialCard.setVisible(false);
        chosenMaterialCard.setManaged(false);

        System.out.println("🧹 Champs et boutons réinitialisés !");
    }

    private String userRole; // Rôle de l'utilisateur

    public void configureButtonsVisibility() {
        if ("Admin".equalsIgnoreCase(userRole)) {
            // Prestataire : tous les boutons visibles
            AddMaterialBTN.setVisible(false);
            modifyBtn.setVisible(false);
            //nextPageBtn.setVisible(true);
            deleteBtn.setVisible(true);

        } else if ("organisateur".equalsIgnoreCase(userRole)) {
            // Organisateur : seul "Rent" est invisible
            AddMaterialBTN.setVisible(false);
            modifyBtn.setVisible(false);
            //nextPageBtn.setVisible(false);
            deleteBtn.setVisible(false);
           // pageNumberLabel.setVisible(false);

        }
    }


}