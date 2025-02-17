package controllers;

import entities.Categorie;
import entities.Materiel;
import javafx.scene.image.ImageView;
import services.MaterielService;
import services.CategorieService;
import javafx.scene.control.Alert;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javafx.scene.image.Image;

public class AddmatController {

    @FXML
    private ComboBox<String> comboCategorie;

    @FXML
    private TextField txtDescription;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextField txtQuantity;

    @FXML
    private ImageView materialImg;

    private String imagePath = "";  // ✅ Stocker le chemin de l'image ici

    private Materiel materielToEdit = null;


    private final MaterielService materielService = new MaterielService();
    private final CategorieService categorieService = new CategorieService();

    @FXML
    public void initialize() {
        // Charger les catégories dynamiquement depuis la base de données
        List<Categorie> categories = categorieService.Recuperer();
        for (Categorie categorie : categories) {
            comboCategorie.getItems().add(categorie.getService());
        }
    }

    @FXML
    private void handleChooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");

        // Filtrer les fichiers pour afficher uniquement les images
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Ouvrir la boîte de dialogue
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Définir un dossier où enregistrer les images
                File destDir = new File("src/main/resources/images/materielsimg/");
                if (!destDir.exists()) destDir.mkdirs(); // Créer le dossier s'il n'existe pas

                // Copier l'image sélectionnée dans "src/main/resources/images/materielsimg/"
                File destFile = new File(destDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // ✅ Enregistrer le chemin relatif
                imagePath = "src/main/resources/images/materielsimg/" + selectedFile.getName();

                // Afficher l'image dans `materialImg`
                Image image = new Image(destFile.toURI().toString());
                materialImg.setImage(image);

                System.out.println("✅ Image copiée et affichée : " + imagePath);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de copier l'image.");
            }
        } else {
            System.out.println("❌ Aucune image sélectionnée.");
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (txtName.getText().isEmpty() || txtDescription.getText().isEmpty() || txtPrice.getText().isEmpty() ||
                txtQuantity.getText().isEmpty() || comboCategorie.getValue() == null || imagePath.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            return;
        }

        try {
            String nom = txtName.getText();
            String description = txtDescription.getText();
            double prix = Double.parseDouble(txtPrice.getText());
            int quantite = Integer.parseInt(txtQuantity.getText());
            String nomCategorie = comboCategorie.getValue();
            int idCategorie = categorieService.getIdCategorieByName(nomCategorie);

            if (idCategorie == -1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid category.");
                return;
            }

            if (materielToEdit == null) {
                // 🔹 Ajouter un nouveau matériel
                Materiel materiel = new Materiel(nom, description, prix, imagePath, quantite, idCategorie, 1);
                materielService.Ajouter(materiel);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Material added successfully!");
            } else {
                // 🔹 Modifier le matériel existant
                materielToEdit.setNom(nom);
                materielToEdit.setDescription(description);
                materielToEdit.setPrix(prix);
                materielToEdit.setQuantite(quantite);
                materielToEdit.setImage(imagePath);
                materielToEdit.setIdCategorie(idCategorie);

                materielService.Modifier(materielToEdit);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Material updated successfully!");

                // 🔄 **Mettre à jour la carte immédiatement après modification**
                materialController.setChosenMateriel(materielToEdit);
            }

            closeWindow(event);
            materialController.loadMaterials(); // 🔄 Rafraîchir la liste

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Price and quantity must be valid numbers.");
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding/updating.");
        }
    }


    private MaterialController materialController; // Stocke une référence au contrôleur principal

    public void setMaterialController(MaterialController controller) {
        this.materialController = controller;
    }


    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void closeWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close(); // ❌ Ferme l'onglet d'ajout
    }

    private void reloadMaterielList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/materiel/materiel-view.fxml")); // ⚠️ Vérifie le chemin exact
            Parent root = loader.load();

            MaterialController materialController = loader.getController();
            materialController.loadMaterials(); // 🔄 Rafraîchir la liste des matériels
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMaterielData(Materiel materiel) {
        if (materiel != null) {
            materielToEdit = materiel; // Stocker l'objet pour modification
            txtName.setText(materiel.getNom());
            txtDescription.setText(materiel.getDescription());
            txtPrice.setText(String.valueOf(materiel.getPrix()));
            txtQuantity.setText(String.valueOf(materiel.getQuantite()));
            comboCategorie.setValue(categorieService.getNomCategorieById(materiel.getIdCategorie()));
            imagePath = materiel.getImage();

            // Charger l'image si disponible
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    materialImg.setImage(new Image(imageFile.toURI().toString()));
                }
            }
        }
    }



}
