package controllers.Events;

import Entity.Events.Event;
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

public class EditEventController {

    @FXML
    private TextField txtTitre;
    @FXML
    private TextField txtDescription;
    @FXML
    private TextField txtNBplaces;
    @FXML
    private TextField txtPrix;

    // ComboBox for event status
    @FXML
    private ComboBox<String> comboEtat;
    // ComboBox for event type
    @FXML
    private ComboBox<String> comboType;

    @FXML
    private TextField txtImage;
    @FXML
    private TextField txtLieu;
    @FXML
    private DatePicker datePicker;

    // Hidden ComboBox for role (for testing purposes)
    @FXML
    private ComboBox<String> comboRole;

    private ServiceEvent serviceEvent = new ServiceEvent();

    // The event to be edited
    private Event eventToEdit;

    @FXML
    public void initialize() {
        // Initialize the dropdown for event type
        comboType.getItems().addAll("Concert", "Conférence", "Exposition", "Atelier");
        // Initialize the dropdown for event status
        comboEtat.getItems().addAll("Disponible", "Complet", "Annulé");

        // For testing, initialize the comboRole with "Admin" or "User"
        comboRole.getItems().addAll("Admin", "User");
        comboRole.setValue("Admin");
    }

    /**
     * This method is used to pass the event to be edited into the controller.
     * It also loads the event data into the form fields.
     */
    public void setEvent(Event event) {
        this.eventToEdit = event;
        txtTitre.setText(event.getTitre());
        txtDescription.setText(event.getDescription());
        txtNBplaces.setText(String.valueOf(event.getNBplaces()));
        txtPrix.setText(String.valueOf(event.getPrix()));
        comboEtat.setValue(event.getEtat());
        comboType.setValue(event.getType());
        txtImage.setText(event.getImage());
        txtLieu.setText(event.getLieu());
        // Convert java.sql.Date to LocalDate for the DatePicker
        if (event.getDate() != null) {
            datePicker.setValue(event.getDate().toLocalDate());
        }
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
            // Convert the selected date to java.sql.Date
            Date date = Date.valueOf(datePicker.getValue());
            String titre = txtTitre.getText();
            String description = txtDescription.getText();
            int nbPlaces = Integer.parseInt(txtNBplaces.getText());
            double prix = Double.parseDouble(txtPrix.getText());
            String etat = comboEtat.getValue();
            String type = comboType.getValue();
            String image = txtImage.getText();
            String lieu = txtLieu.getText();

            // Update the eventToEdit object with new values
            eventToEdit.setDate(date);
            eventToEdit.setTitre(titre);
            eventToEdit.setDescription(description);
            eventToEdit.setNBplaces(nbPlaces);
            eventToEdit.setPrix(prix);
            eventToEdit.setEtat(etat);
            eventToEdit.setType(type);
            eventToEdit.setImage(image);
            eventToEdit.setLieu(lieu);

            // Update the event in the database
            serviceEvent.updateEvent(eventToEdit);

            // Navigate back to the event administration view
            goToEventAdmin(event);
        } catch (Exception ex) {
            ex.printStackTrace();
            // Optionally display an error message to the user here
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        goToEventAdmin(event);
    }

    // Method to navigate back to EventAdmin.fxml
    private void goToEventAdmin(ActionEvent event) {
        try {
            // Ensure that the EventAdmin.fxml is located in your resources folder
            Parent root = FXMLLoader.load(getClass().getResource("/Events/EventAdmin.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
