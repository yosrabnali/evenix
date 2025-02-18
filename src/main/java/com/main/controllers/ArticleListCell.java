package com.main.controllers;

import com.main.Entity.Article;
import com.main.Entity.Commentaire;
import com.main.Entity.Like;
import com.main.services.ArticleService;
import com.main.services.CommentaireService;
import com.main.services.LikeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArticleListCell extends ListCell<Article> {
    @FXML private Label titre;
    @FXML private Label auteur;
    @FXML private Label contenu;
    @FXML private Label likesCount;
    @FXML private Button likeButton;
    @FXML protected VBox commentairesBox;
    @FXML private TextField nouveauCommentaire;
    @FXML private Button annulerButton;
    @FXML private Button envoyerButton;
    @FXML private ImageView imageView;

    private LikeService likeService = new LikeService();
    private CommentaireService commentaireService = new CommentaireService();
    private ArticleService articleService = new ArticleService();
    private VBox cellLayout;

    public ArticleListCell() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/article_list_cell.fxml"));
            loader.setController(this);
            cellLayout = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(Article article, boolean empty) {
        super.updateItem(article, empty);

        if (empty || article == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (titre != null) {
                titre.setText(article.getTitre());
                auteur.setText("Par " + article.getAuteur());
                contenu.setText(article.getContenu());
                
                // Gestion de l'image avec meilleure gestion des erreurs
                try {
                    if (article.getImage() != null && !article.getImage().isEmpty()) {
                        File imageFile = new File("src/main/resources/uploads/" + article.getImage());
                        if (imageFile.exists()) {
                            Image image = new Image(imageFile.toURI().toString());
                            imageView.setImage(image);
                        } else {
                            setDefaultImage();
                        }
                    } else {
                        setDefaultImage();
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
                    setDefaultImage();
                }
                
                likesCount.setText(String.valueOf(article.getLikes()));
                
                likeButton.setOnAction(e -> handleLike(article));
                envoyerButton.setOnAction(e -> handleComment(article));
                annulerButton.setOnAction(e -> nouveauCommentaire.clear());
                
                updateCommentaires(article);
            }
            setGraphic(cellLayout);
        }
    }

    private void handleLike(Article article) {
        if (article == null) {
            System.out.println("Article est null");
            return;
        }
        if (article.getId() == null) {
            System.out.println("L'ID de l'article est null. Article: " + article.getTitre());
            return;
        }

        Long currentUserId = 1L;
        try {
            List<Like> likes = likeService.rechercher();
            boolean hasLiked = likes.stream()
                .anyMatch(l -> l.getArticleId() != null && 
                             l.getArticleId().equals(article.getId()) && 
                             l.getUserId().equals(currentUserId));

            Like like = new Like();
            like.setArticleId(article.getId());
            like.setUserId(currentUserId);

            if (hasLiked) {
                likeService.supprimer(like);
                article.setLikes(article.getLikes() - 1);
                System.out.println("Like supprimé pour l'article ID: " + article.getId());
            } else {
                likeService.ajouter(like);
                article.setLikes(article.getLikes() + 1);
                System.out.println("Like ajouté pour l'article ID: " + article.getId());
            }
            updateLikes(article);
        } catch (Exception ex) {
            System.out.println("Erreur lors de la gestion du like : " + ex.getMessage());
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la gestion du like.");
            alert.showAndWait();
        }
    }

    private void handleComment(Article article) {
        if (article == null || article.getId() == null) {
            System.out.println("Article invalide pour le commentaire");
            return;
        }

        String commentText = nouveauCommentaire.getText().trim();
        if (!commentText.isEmpty()) {
            try {
                Commentaire commentaire = new Commentaire();
                commentaire.setContenu(commentText);
                commentaire.setArticleId(article.getId());
                commentaire.setUserId(1L);
                commentaire.setCreatedAt(LocalDateTime.now());
                commentaire.setAuteur("Utilisateur");

                commentaireService.ajouter(commentaire);
                nouveauCommentaire.clear();
                
                updateCommentaires(article);
                
            } catch (Exception e) {
                System.out.println("Erreur lors de l'ajout du commentaire : " + e.getMessage());
                e.printStackTrace();
                
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur est survenue lors de l'ajout du commentaire.");
                alert.showAndWait();
            }
        }
    }

    private void updateLikes(Article article) {
        try {
            if (article.getId() != null) {
                List<Like> likes = likeService.rechercher();
                int nbLikes = (int) likes.stream()
                    .filter(l -> l.getArticleId() != null && l.getArticleId().equals(article.getId()))
                    .count();
                likesCount.setText(String.valueOf(nbLikes));
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la mise à jour des likes : " + e.getMessage());
        }
    }

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
                    updateCommentaires(article);
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

    private void onSupprimerCommentClick(Commentaire commentaire, Article article) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce commentaire ?");
        alert.setContentText("Cette action est irréversible.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                commentaireService.supprimer(commentaire);
                updateCommentaires(article);
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

    @FXML
    private void onModifierClick(ActionEvent event) {
        Article article = getItem();
        if (article != null) {
            // Créer des TextFields pour la modification
            TextField titreField = new TextField(article.getTitre());
            TextArea contenuField = new TextArea(article.getContenu());
            
            // Appliquer les styles
            titreField.getStyleClass().add("edit-field");
            contenuField.getStyleClass().add("edit-field");
            contenuField.setWrapText(true);
            
            // Remplacer les Labels par les champs de texte
            titre.setVisible(false);
            contenu.setVisible(false);
            
            // Créer les boutons de confirmation et d'annulation
            Button confirmerBtn = new Button("Confirmer");
            Button annulerBtn = new Button("Annuler");
            
            confirmerBtn.getStyleClass().add("comment-confirm-button");
            annulerBtn.getStyleClass().add("comment-cancel-button");
            
            HBox buttonsBox = new HBox(10, annulerBtn, confirmerBtn);
            buttonsBox.setAlignment(Pos.CENTER_RIGHT);
            
            // Ajouter les champs de modification à la cellule
            VBox editContainer = (VBox) titre.getParent();
            editContainer.getChildren().addAll(titreField, contenuField, buttonsBox);
            
            // Gérer la confirmation
            confirmerBtn.setOnAction(e -> {
                String nouveauTitre = titreField.getText().trim();
                String nouveauContenu = contenuField.getText().trim();
                
                if (!nouveauTitre.isEmpty() && !nouveauContenu.isEmpty()) {
                    article.setTitre(nouveauTitre);
                    article.setContenu(nouveauContenu);
                    
                    try {
                        // Sauvegarder les modifications dans la base de données
                        articleService.modifier(article);
                        
                        // Mettre à jour l'affichage
                        titre.setText(nouveauTitre);
                        contenu.setText(nouveauContenu);
                        
                        // Restaurer l'affichage normal
                        restaurerAffichage(editContainer, titreField, contenuField, buttonsBox);
                    } catch (Exception ex) {
                        // Afficher une alerte en cas d'erreur
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur");
                        alert.setHeaderText("Erreur lors de la modification");
                        alert.setContentText("Impossible de modifier l'article : " + ex.getMessage());
                        alert.showAndWait();
                    }
                }
            });
            
            // Gérer l'annulation
            annulerBtn.setOnAction(e -> {
                restaurerAffichage(editContainer, titreField, contenuField, buttonsBox);
            });
        }
    }

    private void restaurerAffichage(VBox container, TextField titreField, TextArea contenuField, HBox buttonsBox) {
        titre.setVisible(true);
        contenu.setVisible(true);
        container.getChildren().removeAll(titreField, contenuField, buttonsBox);
    }

    @FXML
    private void onSupprimerClick(ActionEvent event) {
        Article article = getItem();
        if (article != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet article ?");
            alert.setContentText("Cette action est irréversible.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    articleService.supprimer(article);
                    getListView().getItems().remove(article);
                    System.out.println("Article supprimé avec succès");
                } catch (Exception e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText("Erreur lors de la suppression");
                    errorAlert.setContentText("Impossible de supprimer l'article : " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
        }
    }

    private void setDefaultImage() {
        try {
            // Essayer d'abord de charger depuis le système de fichiers
            File defaultFile = new File("src/main/resources/images/default.png");
            if (defaultFile.exists()) {
                Image defaultImage = new Image(defaultFile.toURI().toString());
                imageView.setImage(defaultImage);
            } else {
                // Si le fichier n'existe pas, essayer de charger depuis les ressources
                String defaultImagePath = "/images/default.png";
                InputStream is = getClass().getResourceAsStream(defaultImagePath);
                if (is != null) {
                    Image defaultImage = new Image(is);
                    imageView.setImage(defaultImage);
                    is.close();
                } else {
                    // Si tout échoue, créer une image vide ou utiliser un placeholder
                    imageView.setImage(null);
                    System.err.println("Image par défaut introuvable");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image par défaut: " + e.getMessage());
            imageView.setImage(null);
        }
    }
}