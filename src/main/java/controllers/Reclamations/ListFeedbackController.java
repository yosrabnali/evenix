package controllers.Reclamations;

import Entity.Reclamations.Feedback;
import Util.MyDB;
import controllers.UserMainLayoutController;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import services.ReclamationsServices.ServiceFeedback;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import services.ReclamationsServices.ServiceReport;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListFeedbackController {
    private static final Logger logger = Logger.getLogger(ListFeedbackController.class.getName());

    @FXML
    private TableView<Feedback> feedbackTable;
    @FXML
    private TableColumn<Feedback, String> descriptionColumn;
    @FXML
    private TableColumn<Feedback, String> etatColumn;
    @FXML
    private TableColumn<Feedback, Date> dateColumn;
    @FXML
    private TableColumn<Feedback, Void> actionsColumn;
    @FXML
    private DatePicker datePickerFilter;
    @FXML
    private ComboBox<String> etatFilter;
    @FXML
    private ComboBox<String> formatReportFilter; // Nouveau ComboBox pour le format du rapport

    private ServiceFeedback serviceFeedback;
    private ServiceReport serviceReport;

    @FXML
    private void initialize() {
        serviceFeedback = new ServiceFeedback();
        serviceReport = new ServiceReport();

        // Configurer les colonnes
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        etatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Style et gestionnaire d'événement pour le DatePicker
        datePickerFilter.setStyle("-fx-font-size: 14; -fx-pref-width: 140px;");
        datePickerFilter.setOnAction(event -> handleFilterByDate());

        // Filtre par état
        etatFilter.setItems(FXCollections.observableArrayList("Processed", "Pending", "Canceled ", "In Progress"));
        etatFilter.setOnAction(event -> handleFilterByEtat());

        // Filtre pour le format du rapport
        formatReportFilter.setItems(FXCollections.observableArrayList("PDF", "Excel"));
        formatReportFilter.setOnAction(event -> handleGenerateReport());

        // Colonne "actions"
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();

            {
                // Vérification des icônes
                Image editIcon = loadImage("/icons/edit.png");
                if (editIcon == null) {
                    editButton.setText("Modifier");
                } else {
                    ImageView editIconView = new ImageView(editIcon);
                    editIconView.setFitWidth(16);
                    editIconView.setFitHeight(16);
                    editButton.setGraphic(editIconView);
                }

                Image deleteIcon = loadImage("/icons/delete.png");
                if (deleteIcon == null) {
                    deleteButton.setText("Supprimer");
                } else {
                    ImageView deleteIconView = new ImageView(deleteIcon);
                    deleteIconView.setFitWidth(16);
                    deleteIconView.setFitHeight(16);
                    deleteButton.setGraphic(deleteIconView);
                }

                editButton.setOnAction(event -> {
                    Feedback feedback = getTableView().getItems().get(getIndex());
                    if (feedback != null) {
                        handleEditFeedback(feedback);
                    }
                });

                deleteButton.setOnAction(event -> {
                    Feedback feedback = getTableView().getItems().get(getIndex());
                    if (feedback != null) {
                        handleDeleteFeedback(feedback);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(editButton, deleteButton);
                    buttons.setSpacing(5);
                    setGraphic(buttons);
                }
            }
        });

        loadFeedbackData();
    }

    public void loadFeedbackData() {
        try {
            List<Feedback> feedbacks = serviceFeedback.getAllFeedbacks();
            ObservableList<Feedback> feedbackObservableList = FXCollections.observableArrayList(feedbacks);
            feedbackTable.setItems(feedbackObservableList);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load responses:" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleFilterByDate() {
        if (datePickerFilter.getValue() != null) {
            Date selectedDate = Date.valueOf(datePickerFilter.getValue());
            try {
                List<Feedback> filteredFeedbacks = serviceFeedback.getFeedbacksByDate(selectedDate);
                ObservableList<Feedback> feedbackObservableList = FXCollections.observableArrayList(filteredFeedbacks);
                feedbackTable.setItems(feedbackObservableList);
            } catch (SQLException e) {
                showAlert("Error", "Unable to filter responses: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            loadFeedbackData();
        }
    }

    private void handleEditFeedback(Feedback feedback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamations/EditFeedback.fxml"));
            Node editFeedbackView = loader.load();

            // Récupérer le contrôleur et passer les données du feedback
            EditFeedbackController controller = loader.getController();
            controller.setFeedback(feedback);

            // Afficher EditFeedback dans centerContent de UserMainLayout
            UserMainLayoutController mainController = UserMainLayoutController.getInstance();
            if (mainController != null) {
                mainController.setCenterContent(editFeedbackView);
            } else {
                System.out.println("❌ Error : Unable to retrieve UserMainLayoutController!");
            }
        } catch (IOException e) {
            System.out.println("❌ Error while loading the response edit page: " + e.getMessage());
        }
    }

    private void handleDeleteFeedback(Feedback feedback) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete confirmation");
        confirmationAlert.setHeaderText("Delete the response");
        confirmationAlert.setContentText("Are you sure?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceFeedback.deleteFeedback(feedback.getIdfeedback());
                    loadFeedbackData();
                } catch (SQLException e) {
                    showAlert("Error", "Unable to delete the response: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.out.println("❌ Error : Unable to load the image" + path);
            return null;
        }
    }

    @FXML
    private void handleGoToListReclamation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamations/ListReclamation.fxml"));
            Node listReclamationView = loader.load();

            // Afficher ListReclamation dans centerContent de UserMainLayout
            UserMainLayoutController mainController = UserMainLayoutController.getInstance();
            if (mainController != null) {
                mainController.setCenterContent(listReclamationView);
            } else {
                System.out.println("❌ Error : Unable to retrieve UserMainLayoutController!");
            }
        } catch (IOException e) {
            System.out.println("❌ Error while loading the complaints list: " + e.getMessage());
        }
    }


    @FXML
    private void handleFilterByEtat() {
        String selectedEtat = etatFilter.getValue();
        if (selectedEtat != null && !selectedEtat.isEmpty()) {
            try {
                List<Feedback> filteredFeedbacks = serviceFeedback.getFeedbacksByEtat(selectedEtat);
                ObservableList<Feedback> feedbackObservableList = FXCollections.observableArrayList(filteredFeedbacks);
                feedbackTable.setItems(feedbackObservableList);
            } catch (SQLException e) {
                showAlert("Error", " Unable to filter responses: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            loadFeedbackData();
        }
    }

    @FXML
    private void handleRefresh() {
        // Réinitialiser les filtres
        datePickerFilter.setValue(null);
        etatFilter.setValue(null);

        // Recharger toutes les données sans filtre
        loadFeedbackData();
    }

    @FXML
    private void handleGenerateReport() {
        String format = formatReportFilter.getValue();
        if (format == null) {
            showAlert("Error", "Please select a format (PDF or Excel).", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save the report");
        String extension = format.equals("PDF") ? ".pdf" : ".xlsx";
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(format + " Files", "*" + extension));

        File file = fileChooser.showSaveDialog(feedbackTable.getScene().getWindow());

        if (file != null) {
            try {
                String filePath = file.getAbsolutePath();
                if (!filePath.endsWith(extension)) {
                    filePath += extension;
                    file = new File(filePath);
                }

                if (format.equals("PDF")) {
                    serviceReport.generatePDFReport(file.getAbsolutePath());
                } else if (format.equals("Excel")) {
                    serviceReport.generateExcelReport(file.getAbsolutePath());
                }
                logger.log(Level.INFO, " Report successfully generated: " + file.getAbsolutePath());
                showAlert("Success", "The report has been successfully generated!", Alert.AlertType.INFORMATION);
            } catch (SQLException | IOException | com.itextpdf.text.DocumentException e) {
                logger.log(Level.SEVERE, "Error while generating the report", e);
                showAlert("Error", "An error occurred while generating the report: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}