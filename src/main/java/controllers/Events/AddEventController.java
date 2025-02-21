package controllers.Events;

import Entity.Events.Event;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import services.EventsServices.ServiceEvent;
import javafx.fxml.FXML;
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
import java.time.LocalDate;

public class AddEventController {
    @FXML
    private FlowPane flowPaneEvents;
    @FXML
    private TextField txtTitre;
    @FXML
    private TextField txtDescription;
    @FXML
    private TextField txtNBplaces;
    @FXML
    private TextField txtPrix;
    @FXML
    private ComboBox<String> comboEtat;
    @FXML
    private ComboBox<String> comboType;
    @FXML
    private TextField txtImage;
    @FXML
    private ComboBox<String> txtLieu;
    @FXML
    private DatePicker datePicker;

    private final ServiceEvent serviceEvent = new ServiceEvent();

    @FXML
    public void initialize() {
        // Initialiser les ComboBox
        if (comboType != null) {
            comboType.getItems().addAll("Film", "Festival", "Theatre", "Ceremony","Concert");
        }
        if (comboEtat != null) {
            comboEtat.getItems().addAll("Disponible", "Complet", "Annulé");
        }
        if (txtLieu != null) {
            txtLieu.getItems().addAll(
                    "Ariana", "Béja", "Ben Arous", "Bizerte", "Gabès",
                    "Gafsa", "Jendouba", "Kairouan", "Kasserine", "Kebili",
                    "Kef", "Mahdia", "Manouba", "Medenine", "Monastir",
                    "Nabeul", "Sfax", "Sidi Bouzid", "Siliana", "Sousse",
                    "Tataouine", "Tozeur", "Tunis", "Zaghouan"
            );
        }
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee;");
                }
            }
        });

    }

    @FXML
    private void handleChooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            txtImage.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            // Vérification des champs obligatoires
            if (txtTitre.getText().isEmpty() || txtDescription.getText().isEmpty() ||
                    datePicker.getValue() == null || txtImage.getText().isEmpty() ||
                    comboEtat.getValue() == null || comboType.getValue() == null ||
                    txtNBplaces.getText().isEmpty() || txtPrix.getText().isEmpty() || txtLieu.getValue().isEmpty()) {

                showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
                return;
            }

            // Récupération des valeurs
            Date date = Date.valueOf(datePicker.getValue());
            String titre = txtTitre.getText();
            String description = txtDescription.getText();
            double prix = Double.parseDouble(txtPrix.getText());
            int nbPlaces = Integer.parseInt(txtNBplaces.getText());
            String etat = comboEtat.getValue();
            String type = comboType.getValue();
            String image = txtImage.getText();
            String lieu = txtLieu.getValue();

            // Création de l'objet Event
            Event e = new Event(date, titre, description, nbPlaces, prix, etat, type, image, lieu, 1234);

            // Insertion dans la base
            serviceEvent.addEvent(e);

            // Retour à l'interface précédente
            goToEventAdmin(event);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Veuillez entrer des valeurs numériques valides.");
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de l'ajout.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        goToEventAdmin(event);
    }

    private void goToEventAdmin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Main/UserMainLayout.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page.");
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
