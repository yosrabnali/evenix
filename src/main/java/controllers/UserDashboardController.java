package controllers;

import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ExcelExporter;
import services.UserService;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UserDashboardController {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> nomColumn, prenomColumn, emailColumn, telephoneColumn, roleColumn;
    @FXML
    private TableColumn<User, Void> actionColumn;
    private ExcelExporter excelExporter; // Service d'exportation Excel
    @FXML
    private Button homeBtn, logoutBtn, searchBtn, prevPageBtn, nextPageBtn, addUserButton;
    @FXML
    private TextField searchField;
    @FXML
    private Label pageNumberLabel;

    private UserService userService = new UserService();
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private FilteredList<User> filteredUserList;

    @FXML
    public void initialize() {
        // Initialisation des colonnes et des données
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Configurer la colonne "Actions"
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button modifyButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            {
                // Gestion des événements pour le bouton "Modifier"
                modifyButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleModifyUser(user);
                });

                // Gestion des événements pour le bouton "Supprimer"
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(10, modifyButton, deleteButton));
                }
            }
        });

        // Charger les utilisateurs dans la table
        loadUserList();

        // Initialisation de la FilteredList
        filteredUserList = new FilteredList<>(userList, p -> true);

        // Lier la FilteredList à la TableView
        userTable.setItems(filteredUserList);

        // Ajouter un écouteur sur le champ de recherche pour filtrer dynamiquement
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredUserList.setPredicate(user -> {
                // Si le champ de recherche est vide, afficher tous les utilisateurs
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Convertir le texte de recherche en minuscules pour une recherche insensible à la casse
                String lowerCaseFilter = newValue.toLowerCase();

                // Vérifier si l'un des champs de l'utilisateur correspond au texte de recherche
                if (user.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getPrenom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getTelephone().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getRole().getRoleName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Aucune correspondance trouvée
            });
        });
    }

    @FXML
    private void goToDashboardPage() {
        // Logique pour aller au tableau de bord
        System.out.println("Aller au tableau de bord");
    }

    @FXML
    private void handleLogout() {
        // Logique pour déconnecter l'utilisateur
        System.out.println("Déconnexion");
    }

    @FXML
    private void handleSearchUser() {
        // Logique pour rechercher un utilisateur
        System.out.println("Rechercher un utilisateur");
    }

    @FXML
    private void goToPreviousPage() {
        // Logique pour aller à la page précédente
        System.out.println("Page précédente");
    }

    @FXML
    private void goToNextPage() {
        // Logique pour aller à la page suivante
        System.out.println("Page suivante");
    }

    @FXML
    private void goToAddUserPage() {
        try {
            // Charger la page d'ajout d'utilisateur
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/fxml/AddUser.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un utilisateur");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserList() {
        // Charger les utilisateurs depuis le service
        userList.setAll(userService.Recuperer());
    }

    private void handleModifyUser(User user) {
        try {
            // Charger la page de modification d'utilisateur
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/fxml/ModifierUtilisateur.fxml"));
            Parent root = loader.load();

            // Passer l'utilisateur à modifier au contrôleur de la page de modification
            ModifierUtilisateurController controller = loader.getController();
            controller.initData(user);

            Stage stage = new Stage();
            stage.setTitle("Modifier un utilisateur");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteUser(User user) {
        // Supprimer l'utilisateur
        boolean success = userService.Supprimer(user.getEmail());
        if (success) {
            loadUserList(); // Recharger la liste des utilisateurs
            System.out.println("Utilisateur supprimé : " + user.getNom());
        } else {
            System.out.println("Échec de la suppression de l'utilisateur : " + user.getNom());
        }
    }
    @FXML
    private void handleExportToExcel() {
        // Récupérer les utilisateurs affichés dans le tableau
        List<User> users = userTable.getItems();

        // Initialiser le service d'exportation
        excelExporter = new ExcelExporter(/* Passer la connexion à la base de données si nécessaire */);

        // Ouvrir une boîte de dialogue pour choisir l'emplacement du fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer sous");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("Utilisateurs.xlsx");

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            // Exporter les données vers Excel
            boolean success = excelExporter.exportToExcel(users, file);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Export réussi", "Le fichier Excel a été exporté avec succès : " + file.getAbsolutePath());
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur d'export", "Une erreur est survenue lors de l'exportation.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Export annulé", "L'exportation a été annulée par l'utilisateur.");
        }
    }

    // Méthode utilitaire pour afficher des alertes
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}