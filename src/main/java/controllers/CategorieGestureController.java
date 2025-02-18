package controllers;

import entities.Categorie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.CategorieService;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseButton;

import java.io.IOException;
import java.util.Comparator;

public class CategorieGestureController {

    @FXML
    private Button AddBTN;

    @FXML
    private Button ClearBtn;

    @FXML
    private TextField TXTservice;

    @FXML
    private TableView<Categorie> categoryTableview;

    @FXML
    private TableColumn<Categorie, String> columnservice;

    @FXML
    private TableColumn<Categorie, Void> columnaction;

    @FXML
    private Button searchBtn;
    @FXML
    private Button prevPageBtn;

    @FXML
    private TextField searchField;
    @FXML
    private Button btnSort;

    private final CategorieService categorieService = new CategorieService();
    private ObservableList<Categorie> categoriesList = FXCollections.observableArrayList();
    private int userId=1;

    @FXML
    public void initialize() {
        loadCategories();

        AddBTN.setOnAction(this::handleAddCategory);
        columnservice.setCellValueFactory(new PropertyValueFactory<>("service"));
        prevPageBtn.setOnAction(e -> goBackToMaterials());
        btnSort.setOnAction(e -> handleSortCategories());

        // 🔥 Assigner la colonne des actions
        setupDeleteButton();

        // 🔍 Ajouter la recherche dynamique
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchCategories(newValue));

        // 🔹 Ajouter l'événement de double-clic pour modifier une catégorie
        categoryTableview.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Categorie selectedCategory = categoryTableview.getSelectionModel().getSelectedItem();
                if (selectedCategory != null) {
                    handleEditCategory(selectedCategory);
                }
            }
        });
    }

    /** ✅ Ajouter une catégorie */
    private void handleAddCategory(ActionEvent event) {
        String service = TXTservice.getText().trim();

        if (service.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le champ de service ne peut pas être vide.");
            return;
        }

        // Vérifier si la catégorie existe déjà
        if (CategorieService.categorieExiste(service)) {
            showAlert(Alert.AlertType.WARNING, "Duplication", "Cette catégorie existe déjà !");
            return; // Empêcher l'ajout de la catégorie en double
        }

        Categorie newCategory = new Categorie(service);
        categorieService.Ajouter(newCategory);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie ajoutée avec succès !");
        TXTservice.clear();
        loadCategories(); // Rafraîchir la liste
    }


    /** ✅ Modifier une catégorie sur double-clic */
    private void handleEditCategory(Categorie category) {
        TextInputDialog dialog = new TextInputDialog(category.getService());
        dialog.setTitle("Edit");
        dialog.setHeaderText("Edit Category");
        dialog.setContentText("New service name:");

        dialog.showAndWait().ifPresent(newService -> {
            if (!newService.trim().isEmpty()) {
                category.setService(newService.trim());
                categorieService.Modifier(category);
                showAlert(Alert.AlertType.INFORMATION, "Edit", "Category added successfully!");
                loadCategories();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "The service field cannot be empty.");
            }
        });
    }

    /** ✅ Supprimer une catégorie */
    private void deleteCategory(Categorie category) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Do you really want to delete this category");
        alert.setContentText("Service : " + category.getService());

        if (alert.showAndWait().get() == ButtonType.OK) {
            categorieService.Supprimer(category.getIdCategorie());
            loadCategories(); // Rafraîchir la liste après suppression
        }
    }

    /** ✅ Charger la liste des catégories */
    private void loadCategories() {
        categoriesList.clear();
        categoriesList.addAll(categorieService.Recuperer());
        categoryTableview.setItems(categoriesList);
        setupDeleteButton(); // 🔥 Recharger la colonne action
    }

    /** ✅ Rechercher une catégorie */
    private void searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            categoryTableview.setItems(categoriesList); // Réafficher toutes les catégories si le champ est vide
        } else {
            ObservableList<Categorie> filteredList = FXCollections.observableArrayList();
            for (Categorie categorie : categoriesList) {
                if (categorie.getService().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(categorie);
                }
            }
            categoryTableview.setItems(filteredList);
        }

        setupDeleteButton(); // 🔥 Recharger la colonne action après la recherche
    }

    /** ✅ Ajout du bouton "Supprimer" */
    private void setupDeleteButton() {
        columnaction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("Delete");

            {
                btnDelete.setOnAction(event -> {
                    Categorie category = getTableView().getItems().get(getIndex());
                    deleteCategory(category);
                });
                btnDelete.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                }
            }
        });
    }

    /** ✅ Afficher une alerte */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void goBackToMaterials() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/materiel/Materiel-view.fxml"));
            Parent root = loader.load();

            // ✅ Récupérer le contrôleur après le chargement de la scène
            MaterialController materialController = loader.getController();

            // ✅ Recharger le rôle depuis la base de données et réappliquer la configuration
            materialController.setUser(this.userId);

            Stage stage = (Stage) categoryTableview.getScene().getWindow();
            stage.setScene(new Scene(root)); // Changer la scène
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Impossible de retourner à la gestion des matériels !");
        }
    }

    @FXML
    private void handleSortCategories() {
        if (!categoriesList.isEmpty()) {
            // Trier la liste directement
            categoriesList.sort(Comparator.comparing(Categorie::getService, String.CASE_INSENSITIVE_ORDER));

            // Remettre la liste triée dans la TableView
            categoryTableview.setItems(FXCollections.observableArrayList(categoriesList));

            // 🔄 Forcer le rafraîchissement de la table
            categoryTableview.refresh();

            System.out.println("✅ Catégories triées par ordre alphabétique !");
        } else {
            System.out.println("⚠️ Aucune catégorie à trier !");
        }
    }


}
