package controllers.Reclamations;

import Entity.Reclamations.Reclamation;
import controllers.UserMainLayoutController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ReclamationsServices.ServiceReclamation;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class EditReclamationController {
    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private Label messageLabel;
    @FXML private DatePicker datePickerReponse;

    private Reclamation selectedReclamation;
    private ServiceReclamation serviceReclamation = new ServiceReclamation();
    private LocalDate originalDate;
    private int idevent; // Nouveau champ pour idevent

    @FXML
    public void initialize() {}

    public void loadReclamationData(Reclamation reclamation) {
        this.selectedReclamation = reclamation;

        titreField.setText(reclamation.getTitre());
        descriptionField.setText(reclamation.getDescription());
        this.idevent = reclamation.getIdevent(); // Charger idevent

        // Sauvegarde de la date originale
        originalDate = (reclamation.getDate() != null) ? reclamation.getDate().toLocalDate() : LocalDate.now();
    }

    @FXML
    public void handleEditReclamation() {
        try {
            if (titreField.getText().isEmpty() || descriptionField.getText().isEmpty()) {
                messageLabel.setText("⚠️Please fill in all fields!");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            String newTitre = titreField.getText();
            String newDescription = descriptionField.getText();

            boolean isModified = !newTitre.equals(selectedReclamation.getTitre()) ||
                    !newDescription.equals(selectedReclamation.getDescription());

            selectedReclamation.setTitre(newTitre);
            selectedReclamation.setDescription(newDescription);

            if (isModified) {
                selectedReclamation.setDate(Date.valueOf(LocalDate.now()));
            }

            // Mettre à jour idevent dans la réclamation
            selectedReclamation.setIdevent(idevent);

            serviceReclamation.modifierReclamation(selectedReclamation);

            messageLabel.setText("✅ Complaint successfully updated!");
            messageLabel.setStyle("-fx-text-fill: green;");
        } catch (SQLException e) {
            messageLabel.setText("❌ Error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void handleCancel() {
        handleBack();
    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamations/ListReclamation.fxml"));
            Node listReclamationView = loader.load();

            UserMainLayoutController mainController = UserMainLayoutController.getInstance();
            if (mainController != null) {
                mainController.setCenterContent(listReclamationView);
            } else {
                System.out.println("❌ Error : Unable to retrieve UserMainLayoutController!");
            }
        } catch (IOException e) {
            System.out.println("❌ Error : Unable to access the complaints list.");
        }
    }
}