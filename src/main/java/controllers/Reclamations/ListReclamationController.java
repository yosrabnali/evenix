package controllers.Reclamations;

import Entity.Reclamations.Reclamation;
import controllers.UserMainLayoutController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ReclamationsServices.ServiceReclamation;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListReclamationController {
    @FXML private TableView<Reclamation> reclamationTable;
    @FXML private TableColumn<Reclamation, String> descriptionColumn;
    @FXML private TableColumn<Reclamation, String> titreColumn;
    @FXML private TableColumn<Reclamation, Date> dateColumn;
    @FXML private TableColumn<Reclamation, String> fichierColumn;
    @FXML private TableColumn<Reclamation, Integer> ideventColumn; // Nouvelle colonne pour idevent
    @FXML private TableColumn<Reclamation, Void> actionsColumn;
    @FXML private DatePicker datePickerFilter;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private static final Logger logger = Logger.getLogger(ListReclamationController.class.getName());

    @FXML
    public void initialize() {
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        fichierColumn.setCellValueFactory(new PropertyValueFactory<>("fichier"));
        ideventColumn.setCellValueFactory(new PropertyValueFactory<>("idevent")); // Ajout de la colonne idevent

        fichierColumn.setCellFactory(column -> new TableCell<>() {
            private final Button openFileButton = new Button("Open");

            {
                openFileButton.setOnAction(event -> {
                    Reclamation rec = getTableView().getItems().get(getIndex());
                    if (rec != null && rec.getFichier() != null && !rec.getFichier().isEmpty()) {
                        openFile(rec.getFichier());
                    }
                });
            }

            @Override
            protected void updateItem(String fichier, boolean empty) {
                super.updateItem(fichier, empty);
                if (empty || fichier == null || fichier.isEmpty()) {
                    setGraphic(null);
                } else {
                    setGraphic(openFileButton);
                }
            }
        });

        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final Button backButton = new Button();

            {
                editButton.setGraphic(createImageView("/icons/edit.png"));
                deleteButton.setGraphic(createImageView("/icons/delete.png"));
                backButton.setGraphic(createImageView("/icons/back.png"));

                editButton.setOnAction(event -> {
                    Reclamation rec = getTableView().getItems().get(getIndex());
                    if (rec != null) {
                        handleEditReclamation(rec);
                    }
                });

                deleteButton.setOnAction(event -> {
                    Reclamation rec = getTableView().getItems().get(getIndex());
                    if (rec != null) {
                        handleDeleteReclamation(rec);
                    }
                });

                backButton.setOnAction(event -> {
                    Reclamation rec = getTableView().getItems().get(getIndex());
                    if (rec != null) {
                        handleAddFeedback(rec);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(editButton, deleteButton, backButton);
                    buttons.setSpacing(5);
                    setGraphic(buttons);
                }
            }
        });

        afficherReclamations();
    }

    public void refreshTable() {
        // Réinitialiser la valeur du filtre de date
        datePickerFilter.setValue(null);

        afficherReclamations();
    }

    private ImageView createImageView(String path) {
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        return imageView;
    }

    private void afficherReclamations() {
        try {
            List<Reclamation> reclamations = serviceReclamation.afficherReclamations();
            ObservableList<Reclamation> obsList = FXCollections.observableArrayList(reclamations);
            reclamationTable.setItems(obsList);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while loading complaints", e);
        }
    }

    private void openFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                showAlert("Error", "Unable to open the file: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Error", "File not found.", Alert.AlertType.ERROR);
        }
    }

    private void handleEditReclamation(Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamations/EditReclamation.fxml"));
            Node editReclamationView = loader.load();

            EditReclamationController controller = loader.getController();
            controller.loadReclamationData(reclamation);

            UserMainLayoutController mainController = UserMainLayoutController.getInstance();
            if (mainController != null) {
                mainController.setCenterContent(editReclamationView);
            } else {
                System.out.println("❌ Error : Unable to retrieve UserMainLayoutController!");
            }
        } catch (IOException e) {
            System.out.println("❌ Error while loading the edit view: " + e.getMessage());
        }
    }

    private void handleDeleteReclamation(Reclamation reclamation) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete confirmation");
        confirmationAlert.setHeaderText("Delete the complaint");
        confirmationAlert.setContentText("Are you sure you want to delete this complaint?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceReclamation.supprimerReclamation(reclamation.getIdreclamation());
                    afficherReclamations();
                    showAlert("Success", "Complaint successfully deleted!", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    showAlert("Error", "Unable to delete the complaint:  " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void handleAddFeedback(Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamations/AddFeedback.fxml"));
            Node addFeedbackView = loader.load();

            AddFeedbackController controller = loader.getController();
            controller.setReclamationId(reclamation.getIdreclamation());

            UserMainLayoutController mainController = UserMainLayoutController.getInstance();
            if (mainController != null) {
                mainController.setCenterContent(addFeedbackView);
            } else {
                System.out.println("❌ Error : Unable to retrieve UserMainLayoutController!");
            }
        } catch (IOException e) {
            System.out.println("❌ Error while loading the feedback addition page: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleAddReclamation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamations/AddReclamation.fxml"));
            Node addReclamationView = loader.load();

            UserMainLayoutController mainController = UserMainLayoutController.getInstance();
            if (mainController != null) {
                mainController.setCenterContent(addReclamationView);
            } else {
                System.out.println("❌ Error : Unable to retrieve UserMainLayoutController!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void filtrerParDate() {
        if (datePickerFilter.getValue() == null) {
            afficherReclamations(); // Affiche toutes les réclamations si aucune date n'est sélectionnée
            return;
        }

        java.sql.Date selectedDate = java.sql.Date.valueOf(datePickerFilter.getValue());

        try {
            List<Reclamation> reclamations = serviceReclamation.afficherReclamations();
            ObservableList<Reclamation> filteredList = FXCollections.observableArrayList();

            for (Reclamation r : reclamations) {
                if (r.getDate() != null && r.getDate().compareTo(selectedDate) == 0) {
                    filteredList.add(r);
                }
            }

            reclamationTable.setItems(filteredList);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while filtering complaints", e);
        }
    }



}