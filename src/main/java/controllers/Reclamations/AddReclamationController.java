package controllers.Reclamations;

import Entity.Reclamations.Reclamation;
import Util.MyDB;
import controllers.UserMainLayoutController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ReclamationsServices.ServiceReclamation;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class AddReclamationController {
    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker datePicker;
    @FXML private Label messageLabel;
    @FXML private Label fileNameLabel;

    private ServiceReclamation serviceReclamation = new ServiceReclamation();
    private ListReclamationController listController;
    private int idUserConnecte = getValidUserId();
    private File selectedFile;
    private int idevent; // Nouveau champ pour idevent

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        datePicker.setDisable(true);
    }

    public void setListController(ListReclamationController listController) {
        this.listController = listController;
    }

    public void setIdevent(int idevent) {
        this.idevent = idevent; // Définir idevent
    }

    private int getValidUserId() {
        try {
            Connection connection = MyDB.getInstance().getConnection();
            String query = "SELECT iduser FROM utilisateur LIMIT 1";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("iduser");
            } else {
                System.out.println("⚠ No user found in the database!");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error while retrieving the user: " + e.getMessage());
            return -1;
        }
    }

    @FXML
    private void handleFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF, Images", "*.pdf", "*.png", "*.jpg", "*.jpeg"));
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            fileNameLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    public void handleAddReclamation() {
        if (titreField.getText().isEmpty() || descriptionField.getText().isEmpty()) {
            showAlert("Error", "Please fill in all fields.", Alert.AlertType.ERROR);
            return;
        }

        if (idUserConnecte == -1) {
            showAlert("Error", "❌ No valid user found!", Alert.AlertType.ERROR);
            return;
        }

        // File is optional, 'fichier' can be null if no file is selected
        String fichier = (selectedFile != null) ? selectedFile.getAbsolutePath() : null;

        Reclamation reclamation = new Reclamation(
                idUserConnecte,
                descriptionField.getText(),
                titreField.getText(),
                Date.valueOf(LocalDate.now()),
                fichier,  // Now using 'fichier'
                idevent    // Ajout de idevent
        );

        try {
            serviceReclamation.ajouterReclamation(reclamation);
            showAlert("success", "Complaint successfully added!", Alert.AlertType.INFORMATION);
            handleCancel();
            if (listController != null) {
                listController.refreshTable();
            }
        } catch (SQLException e) {
            showAlert("Error", "❌ Error : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleCancel() {
        titreField.clear();
        descriptionField.clear();
        fileNameLabel.setText("No file selected.");
        selectedFile = null;
    }

    @FXML
    public void handleViewListReclamation() {
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
            e.printStackTrace();
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