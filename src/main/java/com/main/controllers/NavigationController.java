package com.main.controllers;

import com.main.Entity.Article;
import com.main.services.ArticleService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class NavigationController {

    public ImageView centerLogoImage;
    public ImageView logoImage;
    @FXML private BorderPane mainContainer;
    @FXML private ToggleButton darkModeToggle;
    @FXML private StackPane contentArea;

    private int currentIndex = 0;
    private final String[] views = {
            "/hello-view.fxml",
            "/articles-view.fxml",
            "/articles-list-view.fxml"
    };

    private final String[] titles = {
            "Ajouter Article",
            "Blog",
            "Liste des articles",
    };

    private ArticlesController ArticlesController;
    private ArticlesListController ArticlesListController;
    private ArticleService articleService = new ArticleService();
    private List<Article> sharedArticles;

    @FXML
    private void initialize() {
        try {
            // Initialiser la liste partagée
            sharedArticles = articleService.rechercher();
            mainContainer.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    mainContainer.setCenter(null);
                }
            });
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation : " + e.getMessage());
            showErrorAlert("Erreur d'initialisation", "Impossible de charger les articles");
        }
    }

    @FXML
    public void navigateTo(javafx.event.ActionEvent event) {
        try {
            String fxmlFile;
            switch (((Hyperlink) event.getSource()).getUserData().toString()) {
                case "0" -> fxmlFile = "/hello-view.fxml";
                case "1" -> fxmlFile = "/articles-view.fxml";
                case "2" -> fxmlFile = "/articles-list-view.fxml";
                default -> {
                    System.out.println("Vue non reconnue");
                    return;
                }
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            mainContainer.setCenter(view);

            // Rafraîchir la liste partagée
            try {
                sharedArticles = articleService.rechercher();

                if (fxmlFile.equals("/articles-view.fxml")) {
                    ArticlesController = loader.getController();
                    ArticlesController.setNavigationController(this);
                    ArticlesController.setArticles(sharedArticles);
                } else if (fxmlFile.equals("/articles-list-view.fxml")) {
                    ArticlesListController = loader.getController();
                    ArticlesListController.setNavigationController(this);
                    ArticlesListController.setArticles(sharedArticles);
                    ArticlesListController.setBlogController(ArticlesController);
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors du chargement des articles : " + e.getMessage());
                showErrorAlert("Erreur de chargement", "Impossible de charger les articles");
            }

            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setTitle(titles[currentIndex]);

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : " + e.getMessage());
            showErrorAlert("Erreur de navigation", "Impossible de charger la page demandée");
        }
    }

    @FXML
    public void toggleDarkMode() {
        boolean isDarkMode = darkModeToggle.isSelected();
        String theme = isDarkMode ? "dark" : "light";
        mainContainer.getStyleClass().removeAll("light-mode", "dark-mode");
        mainContainer.getStyleClass().add(theme + "-mode");
        darkModeToggle.setText(isDarkMode ? "☀️" : "🌙");
    }

    @FXML
    private void onBlogButtonClick() throws IOException {
        loadView("/articles-view.fxml");
    }

    private void loadCurrentView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(views[currentIndex]));
            Parent view = loader.load();
            mainContainer.setCenter(view);

            try {
                sharedArticles = articleService.rechercher();  // Rafraîchir la liste partagée

                if (currentIndex == 1) { // Blog
                    ArticlesController = loader.getController();
                    ArticlesController.setNavigationController(this);
                    ArticlesController.setArticles(sharedArticles);
                } else if (currentIndex == 2) { // Liste des articles avec suppression
                    ArticlesListController = loader.getController();
                    ArticlesListController.setNavigationController(this);
                    ArticlesListController.setArticles(sharedArticles);
                    ArticlesListController.setBlogController(ArticlesController);
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors du chargement des articles : " + e.getMessage());
                showErrorAlert("Erreur de chargement", "Impossible de charger les articles");
            }

            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setTitle(titles[currentIndex]);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : " + e.getMessage());
            showErrorAlert("Erreur de navigation", "Impossible de charger la page demandée");
        }
    }

    private void loadView(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent view = fxmlLoader.load();
        contentArea.getChildren().setAll(view);
    }

    public void rafraichirToutesLesVues() {
        try {
            sharedArticles = articleService.rechercher();
            if (ArticlesController != null) {
                ArticlesController.setArticles(sharedArticles);
                ArticlesController.rafraichirArticles();
            }
            if (ArticlesListController != null) {
                ArticlesListController.setArticles(sharedArticles);
                ArticlesListController.rafraichirArticles();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du rafraîchissement des vues : " + e.getMessage());
            showErrorAlert("Erreur de rafraîchissement", "Impossible de rafraîchir les articles");
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}