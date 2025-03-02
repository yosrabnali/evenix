package controllers.Reclamations;

import Entity.Reclamations.Feedback;
import Util.MyDB;
import controllers.UserMainLayoutController;
import javafx.scene.Node;
import services.ReclamationsServices.EmailService;
import services.ReclamationsServices.ServiceFeedback;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddFeedbackController {

    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> etatComboBox;
    @FXML private DatePicker dateField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private TextField userEmailField; // Champ pour l'email de l'utilisateur

    private int reclamationId; // ID de réclamation stocké mais non affiché
    private ServiceFeedback serviceFeedback;
    private Connection connection;

    private EmailService emailService; // Instance de EmailService

    @FXML
    public void initialize() {
        etatComboBox.getItems().addAll("Processed", "Pending", "Canceled ", "In Progress");

        // ✅ Définir la date d'aujourd'hui et la désactiver
        dateField.setValue(LocalDate.now());
        dateField.setDisable(true);

        initConnection();

        // Initialisation de EmailService (remplacez par vos identifiants)
        emailService = new EmailService("evenixgroup@gmail.com", "gbhc nnrx hvzr ekrb\n");
    }

    private void initConnection() {
        try {
            connection = MyDB.getInstance().getConnection();
            serviceFeedback = new ServiceFeedback();
            System.out.println("Database connection successful (AddFeedbackController).");
        } catch (Exception e) {
            showAlert(" Connection error",
                    "Unable to establish a connection to the database.\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void setReclamationId(int idreclamation) {
        this.reclamationId = idreclamation;
        System.out.println("ID Réclamation stored internally : " + idreclamation);
    }

    @FXML
    private void handleSaveButtonAction() {
        if (serviceFeedback == null) {
            showAlert("Error", "ServiceFeedback is not initialized.", Alert.AlertType.ERROR);
            return;
        }

        String description = descriptionField.getText().trim();
        String etat = etatComboBox.getValue();
        LocalDate localDate = LocalDate.now(); // Date automatique
        String userEmail = userEmailField.getText().trim();  // Récupérer l'email de l'utilisateur.

        if (description.isEmpty() || etat == null || userEmail.isEmpty()) {
            showAlert("Error", "Please fill in all fields, including the user's email.", Alert.AlertType.ERROR);
            return;
        }

        if (!userEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert("Error", "Invalid email address.", Alert.AlertType.ERROR);
            return;
        }

        try {
            Feedback feedback = new Feedback(0, description, etat, Date.valueOf(localDate), this.reclamationId);
            serviceFeedback.addFeedback(feedback);

            // Envoi de l'email à l'utilisateur
            try {
                String subject = "New response Added";
                String content = "A new response has been added to your complaint:\n\n" +
                        "Description : " + description + "\n" +
                        "Status : " + etat + "\n" +
                        "Date : " + localDate.toString();

                emailService.sendEmail(userEmail, subject, content);

            } catch (MessagingException e) {
                System.err.println("Error while sending the email: " + e.getMessage());
                showAlert("Error", "Response added, but the email failed to send. Check the console for more information.", Alert.AlertType.WARNING);
            }

            showAlert("Success", "Response successfully added!", Alert.AlertType.INFORMATION);

            // Réinitialiser les champs sans changer de page
            descriptionField.clear();
            etatComboBox.setValue(null);
            userEmailField.clear();

        } catch (SQLException e) {
            showAlert("Error", "Failed to add response: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


    @FXML
    private void handleCancelButtonAction() {
        descriptionField.clear();
        etatComboBox.getSelectionModel().clearSelection();
        userEmailField.clear(); //Effacer aussi le champ email
    }

    @FXML
    private void handleViewFeedbackList() {
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


    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}