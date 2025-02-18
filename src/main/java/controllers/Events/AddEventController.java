
package controllers.Events;

import Entity.Events.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.EventsServices.ServiceEvent;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public class AddEventController {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker datePicker;
    @FXML private TextField lieuField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField nbPlacesField;
    @FXML private TextField prixField;
    @FXML private Button uploadImageButton;
    @FXML private Label imagePathLabel;
    @FXML private Button addButton;
    @FXML private Button cancelButton;

    private File selectedImage;

    @FXML
    public void initialize() {
        // Ajout des types d'événements
        typeComboBox.getItems().addAll("Concert", "Conférence", "Sport", "Théâtre", "Autre");

        // Gestion de l'upload d'image
        uploadImageButton.setOnAction(event -> choisirImage());

        // Gestion du bouton Ajouter
        addButton.setOnAction(event -> ajouterEvenement());

        // Gestion du bouton Annuler
        cancelButton.setOnAction(event -> fermerFenetre());
    }

    private void choisirImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        selectedImage = fileChooser.showOpenDialog(new Stage());

        if (selectedImage != null) {
            imagePathLabel.setText(selectedImage.getAbsolutePath());
        }
    }

    private void ajouterEvenement() {
        Event myEvent = new Event(
                1, // idEvent
                "Tech Conference 2024", // titre
                "A conference about the latest technology trends.", // description
                new Date(), // date
                "New York City", // lieu
                500, // NBplaces
                new BigDecimal("49.99"), // prix
                "Confirmed", // etat
                "Conference", // type
                "event_image.jpg" // image
        );

        ServiceEvent serviceEvent = new ServiceEvent();
        try {
            serviceEvent.ajouter(myEvent);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String titre = titreField.getText().trim();
        String description = descriptionField.getText().trim();
        LocalDate date = datePicker.getValue();
        String lieu = lieuField.getText().trim();
        String type = typeComboBox.getValue();
        String nbPlacesStr = nbPlacesField.getText().trim();
        String prixStr = prixField.getText().trim();

        // Validation des champs obligatoires
        if (titre.isEmpty() || description.isEmpty() || date == null || lieu.isEmpty() || type == null || nbPlacesStr.isEmpty() || prixStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        try {
            int nbPlaces = Integer.parseInt(nbPlacesStr);
            BigDecimal prix = new BigDecimal(prixStr);

            if (nbPlaces <= 0 || prix.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre de places et le prix doivent être positifs.");
                return;
            }

            // Ici, on peut sauvegarder l'événement dans la base de données (ajouter la logique correspondante)
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement ajouté avec succès !");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre de places et le prix doivent être des nombres valides.");
        }
    }

    private void fermerFenetre() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
