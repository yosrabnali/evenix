package com.main.controllers;

import com.main.Entity.Article;
import com.main.Entity.Commentaire;
import com.main.services.ArticleService;
import com.main.services.CommentaireService;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ArticleListCellWithDelete extends ArticleListCell {
    private Button supprimerButton;
    private Button modifierButton;
    private ArticleService articleService = new ArticleService();
    private CommentaireService commentaireService = new CommentaireService();
    private ArticlesListController ArticlesListController;

    public ArticleListCellWithDelete() {
        super();
        supprimerButton = new Button("Supprimer");
        supprimerButton.getStyleClass().add("delete-button");
        
        modifierButton = new Button("Modifier");
        modifierButton.getStyleClass().add("edit-button");
    }

    public void setListController(ArticlesListController controller) {
        this.ArticlesListController = controller;
    }

    @Override
    protected void updateItem(Article article, boolean empty) {
        super.updateItem(article, empty);
        
        if (empty || article == null) {
            setText(null);
            setGraphic(null);
        } else {
            HBox actionsBox = new HBox(10);
            actionsBox.getChildren().addAll(modifierButton, supprimerButton);
            
            VBox cellContent = (VBox) getGraphic();
            if (cellContent != null) {
                cellContent.getChildren().add(actionsBox);
                
                modifierButton.setOnAction(e -> onModifierClick(article));
                supprimerButton.setOnAction(e -> onSupprimerClick(article));
            }
        }
    }

    private void onModifierClick(Article article) {
        // Logique pour modifier l'article
        if (ArticlesListController != null) {
            ArticlesListController.modifierArticle(article);
        }
    }

    private void onSupprimerClick(Article article) {
        if (article != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet article ?");
            alert.setContentText("Cette action est irréversible.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    articleService.supprimer(article);
                    
                    if (ArticlesListController != null && ArticlesListController.getNavigationController() != null) {
                        Platform.runLater(() -> {
                            ArticlesListController.getNavigationController().rafraichirToutesLesVues();
                        });
                    }
                    
                    System.out.println("Article supprimé avec succès");
                } catch (Exception ex) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText("Erreur lors de la suppression");
                    errorAlert.setContentText("Impossible de supprimer l'article : " + ex.getMessage());
                    errorAlert.showAndWait();
                }
            }
        }
    }

    @Override
    protected void updateCommentaires(Article article) {
        try {
            commentairesBox.getChildren().clear();
            
            List<Commentaire> commentaires = commentaireService.rechercher().stream()
                    .filter(c -> c.getArticleId() == article.getId())
                    .toList();

            if (commentaires.isEmpty()) {
                Label noComments = new Label("Soyez le premier à commenter !");
                noComments.getStyleClass().add("no-comments-label");
                commentairesBox.getChildren().add(noComments);
            } else {
                for (Commentaire commentaire : commentaires) {
                    VBox commentBox = new VBox(5);
                    commentBox.getStyleClass().add("comment-box");
                    
                    // En-tête du commentaire
                    HBox headerBox = new HBox(10);
                    headerBox.setAlignment(Pos.CENTER_LEFT);
                    headerBox.setSpacing(10);
                    
                    Label auteurLabel = new Label(commentaire.getAuteur());
                    auteurLabel.getStyleClass().add("comment-author");
                    
                    Label dateLabel = new Label(commentaire.getCreatedAt().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    dateLabel.getStyleClass().add("comment-date");
                    
                    // Boutons d'action
                    HBox actionButtons = new HBox(5);
                    actionButtons.setAlignment(Pos.CENTER_RIGHT);
                    HBox.setHgrow(actionButtons, Priority.ALWAYS);
                    
                    Button modifierButton = new Button("✏️");
                    modifierButton.getStyleClass().add("edit-comment-button");
                    modifierButton.setOnAction(e -> onModifierCommentClick(commentaire, article, commentBox));
                    
                    Button supprimerButton = new Button("🗑");
                    supprimerButton.getStyleClass().add("delete-comment-button");
                    supprimerButton.setOnAction(e -> onSupprimerCommentClick(commentaire, article));
                    
                    actionButtons.getChildren().addAll(modifierButton, supprimerButton);
                    
                    headerBox.getChildren().addAll(auteurLabel, dateLabel, actionButtons);
                    
                    // Contenu du commentaire
                    Label contenuLabel = new Label(commentaire.getContenu());
                    contenuLabel.getStyleClass().add("comment-content");
                    contenuLabel.setWrapText(true);
                    
                    commentBox.getChildren().addAll(headerBox, contenuLabel);
                    commentairesBox.getChildren().add(commentBox);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la mise à jour des commentaires : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void onSupprimerCommentClick(Commentaire commentaire, Article article) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce commentaire ?");
        alert.setContentText("Cette action est irréversible.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                commentaireService.supprimer(commentaire);
                updateCommentaires(article); // Rafraîchir les commentaires
                System.out.println("Commentaire supprimé avec succès");
            } catch (Exception ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText("Erreur lors de la suppression");
                errorAlert.setContentText("Impossible de supprimer le commentaire : " + ex.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    private void onModifierCommentClick(Commentaire commentaire, Article article, VBox commentBox) {
        // Créer la zone de texte pour la modification
        TextArea editArea = new TextArea(commentaire.getContenu());
        editArea.setWrapText(true);
        editArea.getStyleClass().add("edit-comment-area");
        
        // Créer les boutons de confirmation et d'annulation
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button confirmerButton = new Button("Confirmer");
        confirmerButton.getStyleClass().add("confirm-edit-button");
        
        Button annulerButton = new Button("Annuler");
        annulerButton.getStyleClass().add("cancel-edit-button");
        
        buttonBox.getChildren().addAll(annulerButton, confirmerButton);
        
        // Sauvegarder les enfants originaux du commentBox
        List<Node> originalChildren = new ArrayList<>(commentBox.getChildren());
        
        // Remplacer le contenu par la zone d'édition
        commentBox.getChildren().clear();
        commentBox.getChildren().addAll(editArea, buttonBox);
        
        // Gérer l'annulation
        annulerButton.setOnAction(e -> {
            commentBox.getChildren().clear();
            commentBox.getChildren().addAll(originalChildren);
        });
        
        // Gérer la confirmation
        confirmerButton.setOnAction(e -> {
            String nouveauContenu = editArea.getText().trim();
            if (!nouveauContenu.isEmpty()) {
                try {
                    commentaire.setContenu(nouveauContenu);
                    commentaireService.modifier(commentaire);
                    
                    // Rafraîchir toutes les vues
                    if (ArticlesListController != null && ArticlesListController.getNavigationController() != null) {
                        Platform.runLater(() -> {
                            ArticlesListController.getNavigationController().rafraichirToutesLesVues();
                        });
                    }
                    
                    System.out.println("Commentaire modifié avec succès");
                } catch (Exception ex) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText("Erreur lors de la modification");
                    errorAlert.setContentText("Impossible de modifier le commentaire : " + ex.getMessage());
                    errorAlert.showAndWait();
                }
            }
        });
    }
} 