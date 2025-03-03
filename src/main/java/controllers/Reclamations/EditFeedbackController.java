package controllers.Reclamations;

import Entity.Reclamations.Feedback;
import Util.MyDB;
import controllers.UserMainLayoutController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ReclamationsServices.ServiceFeedback;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class EditFeedbackController {

    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> etatComboBox;
    @FXML private Button updateButton;
    @FXML private Button cancelButton;
    @FXML private Button backButton;

    private Feedback feedback;
    private ServiceFeedback serviceFeedback;
    private Connection connection;
    private LocalDate originalDate;

    @FXML
    public void initialize() {
        etatComboBox.getItems().addAll("Processed", "Pending", "Canceled ", "In Progress");
        initConnection();
    }

    private void initConnection() {
        try {
            connection = MyDB.getInstance().getConnection();
            serviceFeedback = new ServiceFeedback(); // ✅ Correction ici : pas de paramètre
            System.out.println("✅ Database connection successful (EditFeedbackController).");
        } catch (Exception e) {
            showAlert("Connection error",
                    "Unable to establish a connection to the database.\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
        populateFields();
    }

    private void populateFields() {
        if (feedback != null) {
            descriptionField.setText(feedback.getDescription());
            etatComboBox.setValue(feedback.getEtat());
            originalDate = (feedback.getDate() != null) ? feedback.getDate().toLocalDate() : LocalDate.now();
        }
    }

    @FXML
    private void handleUpdateAction() {
        if (feedback == null) {
            showAlert("Error", "No response to modify.", Alert.AlertType.ERROR);
            return;
        }

        String newDescription = descriptionField.getText().trim();
        String newEtat = etatComboBox.getValue();

        if (newDescription.isEmpty() || newEtat == null) {
            showAlert("Error", "Please fill in all fields.", Alert.AlertType.ERROR);
            return;
        }

        boolean isModified = !newDescription.equals(feedback.getDescription()) ||
                !newEtat.equals(feedback.getEtat());

        try {
            feedback.setDescription(newDescription);
            feedback.setEtat(newEtat);

            if (isModified) {
                feedback.setDate(Date.valueOf(LocalDate.now()));
            }

            serviceFeedback.updateFeedback(feedback);

            showAlert("Success", "Response successfully updated!", Alert.AlertType.INFORMATION);

            // ✅ Après modification, afficher la liste des feedbacks
            handleGoToListFeedback();

        } catch (SQLException e) {
            showAlert("Error", "Failed to update response:" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


    @FXML
    private void handleGoToListFeedback() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamations/ListFeedback.fxml"));
            Node listFeedbackView = loader.load();

            // Afficher ListFeedback dans centerContent de UserMainLayout
            UserMainLayoutController mainController = UserMainLayoutController.getInstance();
            if (mainController != null) {
                mainController.setCenterContent(listFeedbackView);
            } else {
                System.out.println("❌ Error : Unable to retrieve UserMainLayoutController!");
            }
        } catch (IOException e) {
            System.out.println("❌ Error while loading the response list: " + e.getMessage());
        }
    }


    @FXML
    private void handleCancelAction() {
        handleGoToListFeedback();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
