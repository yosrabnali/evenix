package com.main.controllers;

import com.main.Entity.Article;
import com.main.services.ArticleService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Label;

public class ArticlesListController implements Initializable {
    @FXML
    private ListView<Article> articlesListView;
    
    private ArticleService articleService = new ArticleService();
    private NavigationController navigationController;
    private ArticlesController ArticlesController;
    private List<Article> articles;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            setupListView();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la liste des articles : " + e.getMessage());
            // Afficher une alerte à l'utilisateur
            showErrorAlert("Erreur d'initialisation", "Impossible de charger la liste des articles");
        }
    }

    private void setupListView() throws SQLException {
        articlesListView.setCellFactory(param -> {
            ArticleListCellWithDelete cell = new ArticleListCellWithDelete();
            cell.setListController(this);
            return cell;
        });
        
        rafraichirArticles();
    }

    public void setNavigationController(NavigationController controller) {
        this.navigationController = controller;
    }

    public NavigationController getNavigationController() {
        return navigationController;
    }

    public void setBlogController(ArticlesController controller) {
        this.ArticlesController = controller;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
        try {
            rafraichirArticles();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour des articles : " + e.getMessage());
            showErrorAlert("Erreur de mise à jour", "Impossible de mettre à jour la liste des articles");
        }
    }

    public void rafraichirArticles() throws SQLException {
        if (articlesListView != null && articles != null) {
            articlesListView.getItems().setAll(articles);
        }
    }

    public void modifierArticle(Article article) {
        try {
            // Créer une boîte de dialogue personnalisée
            Dialog<Article> dialog = new Dialog<>();
            dialog.setTitle("Modifier l'article");
            dialog.setHeaderText(null);

            // Créer les champs de saisie
            TextField titreField = new TextField(article.getTitre());
            titreField.setPromptText("Titre");
            TextArea contenuArea = new TextArea(article.getContenu());
            contenuArea.setPromptText("Contenu");
            contenuArea.setPrefRowCount(5);
            contenuArea.setWrapText(true);

            // Créer la mise en page
            VBox content = new VBox(10);
            content.getChildren().addAll(
                new Label("Titre:"), titreField,
                new Label("Contenu:"), contenuArea
            );
            content.setPadding(new Insets(20));

            // Configurer la boîte de dialogue
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Gérer la validation et la sauvegarde
            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    try {
                        article.setTitre(titreField.getText());
                        article.setContenu(contenuArea.getText());
                        articleService.modifier(article);
                        
                        // Rafraîchir la liste
                        rafraichirArticles();
                        
                        // Afficher un message de succès
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Succès");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("L'article a été modifié avec succès!");
                        successAlert.showAndWait();
                        
                        return article;
                    } catch (Exception e) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Erreur");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Une erreur est survenue lors de la modification de l'article.");
                        errorAlert.showAndWait();
                        e.printStackTrace();
                    }
                }
                return null;
            });

            // Ajouter les imports nécessaires en haut du fichier
            dialog.showAndWait();

        } catch (Exception e) {
            System.out.println("Erreur lors de la modification de l'article : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 