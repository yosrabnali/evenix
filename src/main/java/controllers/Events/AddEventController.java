package controllers.Events;

import Entity.Events.Event;
import javafx.scene.control.Alert;
import services.EventsServices.ServiceEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
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
import java.sql.Date;

public class AddEventController {

    @FXML
    private TextField txtTitre;
    @FXML
    private TextField txtDescription;
    @FXML
    private TextField txtNBplaces;
    @FXML
    private TextField txtPrix;

    // ComboBox pour l'état de l'événement
    @FXML
    private ComboBox<String> comboEtat;

    // ComboBox pour le type d'événement
    @FXML
    private ComboBox<String> comboType;

    @FXML
    private TextField txtImage;
    @FXML
    private TextField txtLieu;
    @FXML
    private DatePicker datePicker;

    // ComboBox cachée pour le rôle (test uniquement)
    @FXML
    private ComboBox<String> comboRole;

    private ServiceEvent serviceEvent = new ServiceEvent();

    @FXML
    public void initialize() {
        // Initialiser la liste déroulante pour le type d'événement
        comboType.getItems().addAll("Concert", "Conférence", "Exposition", "Atelier");
        // Initialiser la liste déroulante pour l'état de l'événement
        comboEtat.getItems().addAll("Disponible", "Complet", "Annulé");

        // Pour le test, initialiser le comboRole avec "Admin" ou "User"
        // Comme le champ est caché, vous pouvez définir directement la valeur
        comboRole.getItems().addAll("Admin", "User");
        comboRole.setValue("Admin"); // Par exemple, pour tester en tant qu'administrateur
    }

    @FXML
    private void handleChooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(((Node)event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            txtImage.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {

        if (txtTitre.getText().isEmpty() || txtDescription.getText().isEmpty() || datePicker.getValue() == null || txtImage.getText().isEmpty() ) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs et l'image doivent être remplis.");
            return;
        }

        try {
            // Conversion de la date sélectionnée en java.sql.Date
            Date date = Date.valueOf(datePicker.getValue());
            String titre = txtTitre.getText();
            String description = txtDescription.getText();

            double prix = Double.parseDouble(txtPrix.getText());
            String etat = comboEtat.getValue();
            int nbPlaces = Integer.parseInt(txtNBplaces.getText());
            String type = comboType.getValue();

            String image = txtImage.getText();
            String lieu = txtLieu.getText();


            // Création de l'objet Event
            Event e = new Event(date, titre, description, nbPlaces, prix, etat, type, image, lieu,1234);

            // Insérer l'événement dans la base
            serviceEvent.addEvent(e);

            // Revenir à la vue d'administration des événements
            goToEventAdmin(event);

        } catch (Exception ex) {
            ex.printStackTrace();
            // Vous pouvez afficher un message d'erreur à l'utilisateur ici
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        goToEventAdmin(event);
    }

    // Méthode de navigation pour retourner à EventAdmin.fxml
    private void goToEventAdmin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Events/EventAdmin.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
