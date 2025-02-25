
package com.main.controllers;

import com.main.Entity.Commentaire;
import com.main.services.CommentaireService;
import com.main.services.LikeService;
import com.main.services.PublicationService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;

import static com.main.services.PublicationService.getTimeAgo;

//import com.main.services.utilisateurService;
public class CommentItemController {


//import static com.main.services.PublicationService.getTimeAgo;


        @FXML
        private VBox commentBox;
        @FXML private ImageView profileImage;
        @FXML private Label usernameLabel;
        @FXML private Label commentTextLabel;
        @FXML private Label dateLabel;

        @FXML
        private Label modifcomment;

        @FXML
        private Label supprimercomment;

        private Commentaire commentaire;

        private LikeService ls = new LikeService();
        private CommentaireService cs = new CommentaireService();
        private PublicationService ps = new PublicationService();
        //private utilisateurService us = new utilisateurService();


        @FXML
        void handleModif(MouseEvent event) {
            if(commentaire.getUserId()!=3){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Avertissement !");
                alert.setHeaderText(null);
                alert.setContentText("Tu ne peux pas modifier le commentaire de quelqu'un d'autre 😉");
                alert.getDialogPane().setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                alert.showAndWait();
            }else{
                try{
                }catch (Exception e){e.printStackTrace();}
            }
        }
//getter et setters de l'utilisateur
        //mba3ed tfasa5hon
        public int getUserId() {
            return UserId;
        }

        public void setUserId(int userId) {
            UserId = userId;
        }
        int UserId = 1;
        @FXML

        void handleSuppr(MouseEvent event) {
            if(commentaire.getUserId()!=1){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Avertissement !");
                alert.setHeaderText(null);
                alert.setContentText("Tu ne peux pas supprimer le commentaire de quelqu'un d'autre 😉");
                alert.getDialogPane().setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                alert.showAndWait();
            }else{
                try{cs.supprimer(commentaire);}catch (Exception e){e.printStackTrace();}

            }
        }

        public void setCommentData(Commentaire comment, String username, String profileImagePath) {
            commentaire = comment;
            usernameLabel.setText(username);
            commentTextLabel.setText(comment.getContenu());
                dateLabel.setText(getTimeAgo(comment.getCreatedAt()));
            if (profileImagePath != null && !profileImagePath.isEmpty()) {
                profileImage.setImage(new Image(profileImagePath));
            }
        }
    }

