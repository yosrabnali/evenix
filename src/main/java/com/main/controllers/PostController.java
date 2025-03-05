package com.main.controllers;

import com.main.Entity.Article;
import com.main.Util.CloudinaryUploader;
import com.main.services.CommentaireService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import com.main.services.PublicationService;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class PostController {




    @FXML
    private Button ClearBtn, RentBTN, deleteBtn, modifyBtn, searchBtn;
    @FXML
    private Label AuteurLabel, ContenuLabel, TitreLablel;
    @FXML
    private ImageView PostImg, imgProfile, AddPostBTN;
    @FXML
    private VBox chosenMaterialCard;
    @FXML
    private VBox articlecontainer;
    @FXML
    private VBox comment;
    @FXML
    private HBox addImg;
    @FXML
    private HBox commentSection;
    ;
    @FXML
    private ScrollPane scroll;
    @FXML
    private TextField searchField;
    @FXML
    private TextArea commentinput;
    @FXML
    private ScrollPane commentScrolll;
    @FXML
    private TextArea commentTextArea;
    private long publicationId;
    @FXML
    private VBox sideMenu;


   // private String UploadedImageUrl;
    private boolean isEditMode = false;
    private Article currentArticle;

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    public Article getCurrentArticle() {
        return currentArticle;
    }

    public void setCurrentArticle(Article currentArticle) {
        this.currentArticle = currentArticle;
    }









    public long getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(long publicationId) {
        this.publicationId = publicationId;
    }

    PublicationService publicationService = new PublicationService();
    CommentaireService cs = new CommentaireService();
    private Article selectedMateriel;

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
        sideMenu.setVisible(false);
        sideMenu.setManaged(false);
        loadMaterials();
        /*if (deleteBtn != null) {
            deleteBtn.setOnAction(event -> deleteSelectedMateriel());
        }

        clearChosenMaterial();*/
    }

    @FXML
    void addImgPost(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier Image", "*.png", "*.jpg", "*.jpeg")
        );
        File imgFile = fileChooser.showOpenDialog(null);
        String imageUrl = CloudinaryUploader.uploadImage(imgFile.getAbsolutePath());
        if (imageUrl != null) {
            UploadedImageUrl = imageUrl;
            showWarningDialog("Image Uploaded");
        }
    }



    /* @FXML
    void openAddMatView(MouseEvent event) {
        if (commentinput.getText().equals("")) {
            showWarningDialog("Status Vide :(");
        } else {
            Article p = new Article();
            p.setContenu(commentinput.getText());
            p.setUserId((long) 1);
            p.setAuteur("Yesmine");
            p.setTitre("Post");
            if (getUploadedImageUrl() != null) {
                p.setImage(getUploadedImageUrl());
                System.out.println("Image URL : " + getUploadedImageUrl());
            } *//*else if (getUploadedVideoUrl() != null) {
                p.setType_pub("video");
                p.setContenu(getUploadedVideoUrl());
                System.out.println("Video URL : " + getUploadedVideoUrl());
            }*//* else {
        p.setImage("");
    }
            try {
        publicationService.ajouter(p);
    } catch (Exception e) {
        e.printStackTrace();
    }
    loadMaterials();
}
    }*/

    @FXML
    void openAddMatView(MouseEvent event) {
        if (isEditMode) {
            updateArticle();
        } else {
            createNewArticle();
        }
    }

    private void updateArticle() {
        try {
            currentArticle.setContenu(commentinput.getText());
            if (UploadedImageUrl != null) {
                currentArticle.setImage(UploadedImageUrl);
            }
            publicationService.modifier(currentArticle);
            showSuccessDialog("Article modifié avec succès!");
            returnToMainView();
        } catch (Exception e) {
            e.printStackTrace();
            showWarningDialog("Erreur lors de la modification");
        }
    }

    private void createNewArticle() {
        if (commentinput.getText().isEmpty()) {
            showWarningDialog("Status Vide :(");
            return;
        }

        Article p = new Article();
        p.setContenu(commentinput.getText());
        p.setUserId((long) 1);
        p.setAuteur("Yesmine");
        p.setTitre("Post");
        p.setImage(UploadedImageUrl != null ? UploadedImageUrl : "");

        try {
            publicationService.ajouter(p);
            showSuccessDialog("Article publié!");
            returnToMainView();
        } catch (Exception e) {
            e.printStackTrace();
            showWarningDialog("Erreur lors de la publication");
        }
    }

    public void initEditData(Article article) {
        this.currentArticle = article;
        this.isEditMode = true;
        commentinput.setText(article.getContenu());
        if (article.getImage() != null && !article.getImage().isEmpty()) {
            // Ajouter la logique pour afficher l'image existante
        }
    }

    private void returnToMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/main/views/main-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) commentinput.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    //avant

    public void loadMaterials() {
        try {
            List<Article> articles = publicationService.rechercher();
            articlecontainer.getChildren().clear();
            for (Article article : articles) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/Publication/AddArticle.fxml"));
                VBox vbox = fxmlLoader.load();
                AddArticleController controller = fxmlLoader.getController();
                controller.setArticleId(article.getId());
                controller.setPublicationId(article.getId());
                controller.setData(article.getId());
                articlecontainer.getChildren().add(vbox);
            }
        } catch (Exception e) {
            e.printStackTrace();
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


    private String userRole; // Rôle de l'utilisateur

    public void configureButtonsVisibility() {
        if ("Admin".equalsIgnoreCase(userRole)) {
            // Prestataire : tous les boutons visibles
            AddPostBTN.setVisible(false);
            modifyBtn.setVisible(false);
            //nextPageBtn.setVisible(true);
            deleteBtn.setVisible(true);

        } else if ("Client".equalsIgnoreCase(userRole)) {

            AddPostBTN.setVisible(false);
            modifyBtn.setVisible(false);
            //nextPageBtn.setVisible(false);
            deleteBtn.setVisible(false);
            // pageNumberLabel.setVisible(false);

        }
    }

//recuperer les données de l'article à afficher'

    public void setArticleData(Article article) {
        if (article != null) {
            ContenuLabel.setText(article.getTitre());
            ContenuLabel.setText(article.getContenu());
        }
    }


}