package com.main.controllers;

import com.main.Entity.Article;
import com.main.Entity.Commentaire;
import com.main.Util.CloudinaryUploader;
import com.main.services.CommentaireService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import com.main.services.PublicationService;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @FXML
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