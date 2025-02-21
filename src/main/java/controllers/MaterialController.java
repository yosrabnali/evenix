package controllers;

import QRcode.QRCodeGenerator;
import QRcode.QRScanner;
import chatbot.Chatbot;
import entities.Materiel;
import javafx.animation.*;
import javafx.application.Platform;
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
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.ExcelExporter;
import services.ExcelImporter;
import services.MaterielService;
import util.MyDB;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.util.Duration;



public class MaterialController {

        @FXML
        private GridPane grid;

        @FXML
        private VBox chosenMaterialCard;

        @FXML
        private Label materielNameLable;

        @FXML
        private Label materielPriceLabel;

        @FXML
        private ImageView materielImg;

        @FXML
        private Label quantityNameLable;

        @FXML
        private Button modifyBtn;

        @FXML
        private ImageView ClearBtn;

        @FXML
        private ImageView deleteBtn;
        @FXML
        private Button AddMaterialBTN;

        @FXML
        private Label DescriptionLabel;

        @FXML
        private Label categoryLabel;

        @FXML
        private Button searchBtn;

        @FXML
        private TextField searchField;

        @FXML
        private Button nextPageBtn;

        @FXML
        private Label pageNumberLabel;
        @FXML
        private ImageView RentBTN;
        @FXML
        private ImageView evenix;
        @FXML
        private Label evenixTXT;
        @FXML
        private ImageView successImage;
        @FXML
        private ImageView IAimage;
        @FXML
        private ImageView QRimg;
        @FXML
        private ImageView excelimg;
        @FXML
        private ImageView exportimg;
        @FXML
        private Label IAchat;
        @FXML
        private ImageView achaticon;
        @FXML
        private ImageView eventicon;
        @FXML
        private ImageView homeicon;
        @FXML
        private ImageView logouticon;
        @FXML
        private ImageView pubicon;
        @FXML
        private ImageView recicon;
        @FXML
        private Label exportTXT;
        @FXML
        private Label importTXT;


        private String userRole; // Rôle de l'utilisateur
        private int userId; // ID de l'utilisateur connecté

        private MaterielService materielService = new MaterielService();
        private Materiel selectedMateriel; // Pour stocker le matériel sélectionné

        Chatbot chatbot = new  Chatbot(); // Initialiser la classe Chatbot
        @FXML
        private void handleChatbotClick() {
                // Appeler la méthode pour afficher le chatbot
                chatbot.showChatbot();
        }
        @FXML
        public void initialize() {
                IAimage.setOnMouseClicked(e -> handleChatbotClick());
                excelimg.setOnMouseClicked(e -> handleImportExcel());
                exportimg.setOnMouseClicked(e ->handleExportExcel());
                grid.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 20px;");
                chosenMaterialCard.getStyleClass().add("chosen-Material-Card");

                // Cacher la carte de détail au début
                chosenMaterialCard.setVisible(false);
                chosenMaterialCard.setManaged(false);



                if (deleteBtn != null) {
                        deleteBtn.setOnMouseClicked(event -> deleteSelectedMateriel());
                }


                if (ClearBtn != null) {
                        ClearBtn.setOnMouseClicked(event -> clearChosenMaterial());


                }
                if (modifyBtn != null) {
                        modifyBtn.setOnAction(event -> {
                                if (selectedMateriel != null) {
                                        openEditMaterialView(selectedMateriel);
                                }
                                else {
                                        showWarningDialog("⚠ No material selected!");
                                }
                        });}


                clearChosenMaterial();
                loadMaterials();
                searchBtn.setOnAction(event -> searchMaterials());


                // Lancer la recherche automatiquement lorsqu'on tape dans le champ
                searchField.textProperty().addListener((observable, oldValue, newValue) -> searchMaterials());
                AddMaterialBTN.setOnAction(this::openAddMatView);
                nextPageBtn.setOnAction(e -> openCategoryManagement());
                RentBTN.setOnMouseClicked(event -> {
                        if (selectedMateriel != null) {
                                showLivraisonPopup(selectedMateriel.getIdMateriel(), selectedMateriel.getQuantite());
                        } else {
                                showWarningDialog("⚠ Aucun matériel sélectionné !");
                        }

                });
                //animation
                startRotationEvery2Seconds();
                ImageAnimation.animateTextFade(evenixTXT, 1.5, 1.0, 0.3, FadeTransition.INDEFINITE);
                ImageAnimation.animateTextFade(IAchat, 1.5, 1.0, 0.3, FadeTransition.INDEFINITE);
                ImageAnimation.animateTextFade(importTXT, 1.5, 1.0, 0.3, FadeTransition.INDEFINITE);
                ImageAnimation.animateTextFade(exportTXT, 1.5, 1.0, 0.3, FadeTransition.INDEFINITE);
                ////////////////////////////////////////
                ImageAnimation.addHoverEffect(IAimage);
                ImageAnimation.addHoverEffect(excelimg);
                ImageAnimation.addHoverEffect(exportimg);
                ImageAnimation.addHoverEffect(pubicon);
                ImageAnimation.addHoverEffect(homeicon);
                ImageAnimation.addHoverEffect(eventicon);
                ImageAnimation.addHoverEffect(recicon);
                ImageAnimation.addHoverEffect(achaticon);
                ImageAnimation.addHoverEffect(ClearBtn);
                ImageAnimation.addHoverEffect(deleteBtn);
                ImageAnimation.addHoverEffect(RentBTN);
                ImageAnimation.addHoverEffect(logouticon);






        }

        private void startRotationEvery2Seconds() {
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.seconds(2), event -> rotateImage()) // Exécute la rotation chaque 2 secondes
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
        private void openAddMatView(ActionEvent event) {
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/materiel/AddMaterial.fxml")); // ⚠️ Vérifie bien le chemin exact
                        Parent root = loader.load();

                        // 🔹 Récupérer le contrôleur de AddmatController
                        AddmatController addmatController = loader.getController();
                        addmatController.setMaterialController(this); // 🔄 Passer une référence du contrôleur actuel

                        Stage stage = new Stage();
                        stage.setTitle("Add a Material");
                        stage.setScene(new Scene(root));

                        // ✅ Ajouter un écouteur pour détecter la fermeture de la fenêtre et recharger la liste
                        stage.setOnHidden(e -> {
                                System.out.println("🔄 Fenêtre d'ajout fermée, rechargement des matériels...");
                                loadMaterials(); // 🔄 Mettre à jour la liste des matériels
                        });

                        stage.show();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }





        private void searchMaterials() {
                String searchText = searchField.getText().trim();

                // Vérifier et rétablir la connexion si nécessaire
                if (!MyDB.getInstance().isConnected()) {
                        System.out.println("🔄 Réouverture de la connexion pour la recherche...");
                        MyDB.getInstance().getConnection();
                }

                // Si le champ est vide, afficher tous les matériels
                List<Materiel> filteredMaterials = searchText.isEmpty() ?
                        materielService.Recuperer() :
                        materielService.searchByName(searchText);

                grid.getChildren().clear(); // Nettoyer l'affichage

                int column = 0;
                int row = 0;

                for (Materiel m : filteredMaterials) {
                        VBox materialBox = createMaterialCard(m);
                        grid.add(materialBox, column++, row);

                        if (column == 5) { // 5 éléments max par ligne
                                column = 0;
                                row++;
                        }
                }
        }









        /** ✅ Charger et afficher les matériels */
        public void loadMaterials() {
                List<Materiel> materiels = materielService.Recuperer();
                grid.getChildren().clear();
                grid.setHgap(25); // Espacement horizontal entre les cartes
                grid.setVgap(25); // Espacement vertical entre les cartes
                grid.setPadding(new Insets(25, 25, 25, 25)); // Ajouter des marges autour de la grille

                // Définir la structure du GridPane pour éviter des déformations
                grid.getColumnConstraints().clear();
                for (int i = 0; i < 5; i++) { // 4 colonnes max
                        ColumnConstraints colConstraints = new ColumnConstraints();
                        colConstraints.setPercentWidth(25); // Chaque colonne prend 25% de la largeur
                        grid.getColumnConstraints().add(colConstraints);
                }

                grid.getRowConstraints().clear();
                for (int i = 0; i < 5; i++) { // Prévoir 5 lignes visibles max
                        RowConstraints rowConstraints = new RowConstraints();
                        rowConstraints.setMinHeight(200); // Hauteur minimum des lignes
                        grid.getRowConstraints().add(rowConstraints);
                }

                int column = 0;
                int row = 0;

                for (Materiel m : materiels) {


                        VBox materialBox = createMaterialCard(m);
                        materialBox.setPrefWidth(280); // Augmenter la largeur de la carte
                        materialBox.setPrefHeight(350); // Augmenter la hauteur de la carte
                        materialBox.setAlignment(Pos.CENTER); // Centrer le contenu
                        materialBox.getStyleClass().add("material-card"); // Appliquer le style CSS

                        // Utiliser un conteneur pour le centrage
                        StackPane container = new StackPane(materialBox);
                        container.setAlignment(Pos.CENTER);
                        grid.add(container, column++, row);

                        if (column == 5) { // 4 cartes par ligne
                                column = 0;
                                row++;
                        }

                }

        }
        /** ✅ Créer une carte pour chaque matériel */
        private VBox createMaterialCard(Materiel m) {
                VBox card = new VBox();
                card.setPrefSize(180, 200);
                card.getStyleClass().add("Material-item");

                HBox titleBox = new HBox();
                titleBox.setSpacing(10);
                titleBox.setStyle("-fx-alignment: center-left; -fx-padding: 5px;");

                Label nameLabel = new Label(m.getNom());
                nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

                Label priceLabel = new Label("$" + m.getPrix());
                priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2e7d32; -fx-font-weight: bold;");

                titleBox.getChildren().addAll(nameLabel, priceLabel);

                ImageView imageView = new ImageView();
                imageView.setFitHeight(120);
                imageView.setFitWidth(120);
                File imageFile = new File(m.getImage());
                if (imageFile.exists()) {
                        imageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                        imageView.setImage(new Image("file:src/main/resources/images/default.png"));
                }

                card.getChildren().addAll(titleBox, imageView);
                card.setOnMouseClicked(event -> setChosenMateriel(m)); // Sélectionner un matériel au clic

                return card;
        }

        public void setChosenMateriel(Materiel materiel) {
                if (materiel == null) return;

                selectedMateriel = materiel; // Stocker le matériel sélectionné
                // Mettre à jour les labels avec les nouvelles informations
                materielNameLable.setText(materiel.getNom());
                materielPriceLabel.setText("$" + materiel.getPrix());
                quantityNameLable.setText("Quantity: " + materiel.getQuantite());
                DescriptionLabel.setText("Description: " + materiel.getDescription());

                // 🔹 Afficher la catégorie dans le Label
                categoryLabel.setText("Category: " + materielService.getCategoryName(materiel.getIdCategorie()));

                // Charger l'image
                if (materiel.getImage() != null && !materiel.getImage().isEmpty()) {
                        File imageFile = new File(materiel.getImage());
                        if (imageFile.exists()) {
                                materielImg.setImage(new Image(imageFile.toURI().toString()));
                        } else {
                                materielImg.setImage(new Image("file:src/main/resources/images/default.png"));
                        }
                } else {
                        materielImg.setImage(new Image("file:src/main/resources/images/default.png"));
                }
                // ✅ Vérifier les données avant affichage
                String nom = materiel.getNom() != null ? materiel.getNom() : "Inconnu";
                String prix = String.valueOf(materiel.getPrix());
                String description = materiel.getDescription() != null ? materiel.getDescription() : "Non spécifiée";
                String categorie = materielService.getCategoryName(materiel.getIdCategorie()) != null ? materielService.getCategoryName(materiel.getIdCategorie()) : "Non spécifiée";

                // ✅ Création d’un texte bien formaté pour le QR Code
                String qrData = "🔹 ID: " + materiel.getIdMateriel() +
                        "\n📌 Name: " + nom +
                        "\n💰 Price: " + prix + " DT" +
                        "\n🏷 Description: " + description +
                        "\n📂 Category: " + categorie;

                System.out.println("🔹 QR Code généré avec les données suivantes : \n" + qrData); // Debug pour voir si les données sont bien envoyées

                // ✅ Affichage du QR Code
                QRimg.setImage(QRCodeGenerator.generateQRCode(qrData, 150, 150));


                // ✅ Activer les boutons
                modifyBtn.setDisable(false);
                deleteBtn.setDisable(false);
                ClearBtn.setDisable(false);
                // Rendre la carte visible
                chosenMaterialCard.setVisible(true);
                chosenMaterialCard.setManaged(true);
        }





        /** ✅ Effacer les champs, désactiver les boutons et réinitialiser la sélection */
        @FXML
        private void clearChosenMaterial() {
                selectedMateriel = null; // Désélectionner

                if (materielNameLable != null) materielNameLable.setText("");
                if (materielPriceLabel != null) materielPriceLabel.setText("");
                if (quantityNameLable != null) quantityNameLable.setText("");
                if (DescriptionLabel != null) DescriptionLabel.setText("");
                if (materielImg != null) materielImg.setImage(null);
                if (categoryLabel != null) categoryLabel.setText(""); // Réinitialiser la ComboBox

                // Désactiver les boutons
                if (modifyBtn != null) modifyBtn.setDisable(true);
                if (deleteBtn != null) deleteBtn.setDisable(true);
                if (ClearBtn != null) ClearBtn.setDisable(true);

                // Masquer la carte des détails
                chosenMaterialCard.setVisible(false);
                chosenMaterialCard.setManaged(false);

                System.out.println("🧹 Champs et boutons réinitialisés !");
        }






        /** ✅ Supprimer un matériel */
        @FXML
        private void deleteSelectedMateriel() {
                if (selectedMateriel == null) {
                        showWarningDialog("⚠️ No material selected!");
                        return;
                }

                // Demande de confirmation avant suppression
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Deletion Confirmation");
                alert.setHeaderText("Do you really want to delete this material?");
                alert.setContentText("This action is irreversible.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                        int idMateriel = selectedMateriel.getIdMateriel(); // Récupérer l'ID du matériel
                        boolean deleted = materielService.Supprimer(idMateriel); // Supprimer dans la BD

                        if (deleted) {
                                showInformationDialog("✅ Material successfully deleted!");
                                clearChosenMaterial(); // Vider les détails affichés
                                loadMaterials(); // Rafraîchir la liste
                        } else {
                                showWarningDialog("❌ Deletion failed!");
                        }
                }
        }

        /** ✅ Afficher un message d'information */
        private void showInformationDialog(String message) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
        }

        /** ✅ Afficher un avertissement */
        private void showWarningDialog(String message) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
        }



        private void openCategoryManagement() {
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/materiel/CategorieGesture.fxml"));
                        Parent root = loader.load();

                        Stage stage = (Stage) grid.getScene().getWindow(); // Obtenir la fenêtre actuelle
                        stage.setScene(new Scene(root)); // Remplacer le contenu de la fenêtre
                        stage.show();
                        configureButtonsVisibility();

                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
        @FXML
        private void openEditMaterialView(Materiel materiel) {
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/materiel/AddMaterial.fxml"));
                        Parent root = loader.load();

                        AddmatController addmatController = loader.getController();
                        addmatController.setMaterialController(this);
                        addmatController.loadMaterielData(materiel); // Charger les données existantes

                        Stage stage = new Stage();
                        stage.setTitle("Edit Material");
                        stage.setScene(new Scene(root));

                        // Fermer la fenêtre de modification et recharger la liste après
                        stage.setOnHidden(e -> {
                                System.out.println("🔄 Modification terminée, mise à jour de la liste...");
                                loadMaterials();
                        });

                        stage.show();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
        public void setUser(int userId) {
                this.userId = userId;
                this.userRole = materielService.getUserRoleFromDB(userId); // Récupérer le rôle depuis la BD
                configureButtonsVisibility(); // Appliquer la visibilité après avoir récupéré le rôle
        }
        public void configureButtonsVisibility() {
                if ("prestataire".equalsIgnoreCase(userRole)) {
                        // Prestataire : tous les boutons visibles
                        AddMaterialBTN.setVisible(true);
                        modifyBtn.setVisible(true);
                        nextPageBtn.setVisible(true);
                        deleteBtn.setVisible(true);
                        RentBTN.setVisible(false); // Cacher "Rent"
                } else if ("organisateur".equalsIgnoreCase(userRole)) {
                        // Organisateur : seul "Rent" est visible
                        AddMaterialBTN.setVisible(false);
                        modifyBtn.setVisible(false);
                        nextPageBtn.setVisible(false);
                        deleteBtn.setVisible(false);
                        pageNumberLabel.setVisible(false);
                        RentBTN.setVisible(true);
                        importTXT.setVisible(false);
                        excelimg.setVisible(false);
                }
        }


        @FXML
        public void showLivraisonPopup(int idMateriel, int maxQte) {
                Platform.runLater(() -> {
                        Stage popupStage = new Stage();
                        popupStage.initOwner(RentBTN.getScene().getWindow());
                        popupStage.initModality(Modality.APPLICATION_MODAL);
                        popupStage.setTitle("Confirmer la livraison");

                        Label label = new Label("Entrez la quantité (max: " + maxQte + ") :");
                        TextField qteField = new TextField();
                        Button confirmButton = new Button("OK");
                        VBox layout = new VBox(10, label, qteField, confirmButton);
                        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

                        confirmButton.setOnAction(e -> {
                                try {
                                        int qte = Integer.parseInt(qteField.getText());
                                        if (qte > 0 && qte <= maxQte) {
                                                materielService.ajouterLivraison(idMateriel, qte);

                                                if (!MyDB.getInstance().isConnected()) {
                                                        System.out.println("🔄 Réouverture de la connexion...");
                                                        materielService = new MaterielService(); // Réinitialiser la connexion
                                                }

                                                materielService.diminuerQuantiteMateriel(idMateriel, qte);

                                                layout.getChildren().clear();

                                                // Fermer la popup après 1 seconde
                                                PauseTransition delay = new PauseTransition(Duration.seconds(1));
                                                delay.setOnFinished(event -> popupStage.close());
                                                delay.play();
                                        } else {
                                                label.setText("Quantité invalide !");
                                        }
                                } catch (NumberFormatException ex) {
                                        label.setText("Veuillez entrer un nombre valide !");
                                }
                        });

                        popupStage.setScene(new Scene(layout, 300, 200));
                        popupStage.showAndWait();
                });
        }



        @FXML
        private void handleImportExcel() {
                ExcelImporter importer = new ExcelImporter();
                importer.importFromExcel(new Stage());
        }
        @FXML
        private void handleExportExcel() {
                ExcelExporter exporter = new ExcelExporter();
                exporter.exportToExcel(new Stage());
        }










        }















