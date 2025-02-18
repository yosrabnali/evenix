package com.main.controllers;

import com.main.Entity.Article;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ArticlesController implements Initializable {
    @FXML private ListView<Article> articlesListView;
    @FXML private TextField searchField;
    @FXML private DatePicker datePicker;
    
    private NavigationController navigationController;
    private List<Article> articles;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            setupListView();
            setupSearch();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation : " + e.getMessage());
        }
    }

    private void setupListView() {
        articlesListView.setCellFactory(param -> new ArticleListCell());
        if (articles != null) {
            articlesListView.getItems().setAll(articles);
        }
    }

    private void setupSearch() {
        // Configuration de la recherche...
    }

    public void setNavigationController(NavigationController controller) {
        this.navigationController = controller;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
        rafraichirArticles();
    }

    public void rafraichirArticles() {
        if (articlesListView != null && articles != null) {
            articlesListView.getItems().setAll(articles);
        }
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        datePicker.setValue(null);
        rafraichirArticles();
    }
}