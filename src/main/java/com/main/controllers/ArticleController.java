package com.main.controllers;

import com.main.Entity.*;
import com.main.services.CommentaireService;
import com.main.services.LikeService;
import com.main.services.PublicationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import com.main.services.UserService;


public class ArticleController implements Initializable {

    Reactions currentReaction;
    private long startTime=0;
    private long articleId;
    PublicationService ps = new PublicationService();

    public long getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(long publicationId) {
        this.publicationId = publicationId;
    }

    @FXML
    private ImageView imgCare;

    @FXML
    private ImageView imgHaha;

    @FXML
    private ImageView imgLike;

    @FXML
    private ImageView imgLove;
    @FXML
    private ImageView imgSad;

    @FXML
    private ImageView imgWow;
    @FXML
    private Label nbReactions;

    private long publicationId;
    @FXML

    private HBox ReactionsContainer;
    @FXML
    private ImageView addcommentbtn;

    @FXML
    private VBox comment;

    @FXML
    private HBox commentContainer21;

    @FXML
    private ImageView profileimg;
    @FXML
    private ScrollPane commentScrolll;

    @FXML
    private HBox commentSection;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private Label contenu;

    @FXML
    private Label reactionName;

    @FXML
    private ImageView imgarticle;

    @FXML
    private ImageView profileImg;

    @FXML
    private ImageView imgReaction;

    @FXML
    private ImageView menutoggle;

    @FXML
    private Label username;
    @FXML
    private ImageView reaction1;

    @FXML
    private ImageView reaction2;

    @FXML
    private ImageView reaction3;

    @FXML
    private VBox sidemenu;
    PublicationService publicationService = new PublicationService();
    LikeService ls = new LikeService();
    private Popup sidemenuPopup = new Popup();
    CommentaireService cs = new CommentaireService();

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    @FXML
    public void showmenu(MouseEvent event) {
        sidemenuPopup.show(menutoggle.getScene().getWindow(),
                event.getScreenX(), event.getScreenY());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sidemenuPopup.getContent().add(sidemenu);
        sidemenuPopup.setAutoHide(true);
        commentSection.setManaged(false);
        commentSection.setVisible(false);
        commentScrolll.setManaged(false);
        commentScrolll.setVisible(false);
        setData(articleId);

    }


    public void setData(long articleId) {
        try {
            Article a = ps.getById(articleId);
            if (a != null) {
                username.setText(a.getAuteur());
                contenu.setText(a.getContenu());
                if (a.getImage() != null && !a.getImage().isEmpty()) {
                    System.out.println(a.getImage());
                    imgarticle.setImage(new Image(a.getImage()));
                    imgarticle.setVisible(true);
                    imgarticle.setManaged(true);
                } else {
                    System.out.println("Image not available");
                    imgarticle.setImage(null);
                    imgarticle.setVisible(false);
                    imgarticle.setManaged(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void loadComments() throws Exception {
        List<Commentaire> com = null;
        System.out.println(getPublicationId());
        com = cs.rechercherByArticleID(getPublicationId());
        comment.getChildren().clear();
        for (Commentaire comment1 : com) {
            System.out.println(comment1);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Publication/CommentItem.fxml"));
                VBox commentNode = loader.load();
                CommentItemController controller = loader.getController();
                String username = publicationService.getById(getPublicationId()).getAuteur();
                //if (user.getRole().equals("Client")) {
                String profilePath = "/images/client.png";
                //}
                controller.setCommentData(comment1, username, profilePath);
                comment.getChildren().add(commentNode);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    @FXML
    void addcomment(MouseEvent event) {
        if (commentTextArea.getLength() != 0) {
            Commentaire commentaire = new Commentaire();
            commentaire.setArticleId(publicationId);
            commentaire.setUserId((long)1);
            commentaire.setContenu(commentTextArea.getText());
            commentaire.setAuteur("Yesmine");
            try {
                System.out.println(commentaire);
                cs.ajouter(commentaire);
                loadComments();
            } catch (Exception e) {
                e.printStackTrace();
            }
            commentTextArea.clear();
        }else{
            showWarningDialog("veuiller saisir commentaire :(");
        }


    }



    @FXML
    void handleComment(MouseEvent event) {
        boolean isVisible = commentSection.isVisible(); // Check current state
        if (isVisible) {
            commentSection.setManaged(false);
            commentSection.setVisible(false);
            commentScrolll.setManaged(false);
            commentScrolll.setVisible(false);
        } else {
            try {
              /*  String profileImageUrl = us.getById(ps.getById(getPublicationId()).getUserId()).getPdp();
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    profileimg.setImage(new Image(profileImageUrl));
                }*/
                loadComments();
            } catch (Exception e) {
                e.printStackTrace();
            }
            commentSection.setManaged(true);
            commentSection.setVisible(true);
            commentScrolll.setManaged(true);
            commentScrolll.setVisible(true);
        }
    }



    @FXML
    public void onLikeContainerPressed(MouseEvent event) {
        startTime = System.currentTimeMillis();
    }

    @FXML
    public void onLikeContainerMouseReleased(MouseEvent event) {
        if (System.currentTimeMillis() - startTime > 200) {
            ReactionsContainer.setVisible(true);
        } else {
            if (ReactionsContainer.isVisible()) {
                ReactionsContainer.setVisible(false);
            }
            if (currentReaction == Reactions.NON) {
                setReaction(Reactions.LIKE);
            } else {
                setReaction(Reactions.NON);
            }
        }
    }
    public void onReactionImgPressed(MouseEvent event) {
        switch (((ImageView) event.getSource()).getId()) {
            case "imgLike":
                setReaction(Reactions.LIKE);
                break;
            case "imgLove":
                setReaction(Reactions.LOVE);
                break;
            case "imgCare":
                setReaction(Reactions.CARE);
                break;
            case "imgHaha":
                setReaction(Reactions.HAHA);
                break;
            case "imgWow":
                setReaction(Reactions.WOW);
                break;
            case "imgSad":
                setReaction(Reactions.SAD);
                break;
            case "imgAngry":
                setReaction(Reactions.ANGRY);
                break;
            default:
                setReaction(Reactions.NON);
                break;
        }
        ReactionsContainer.setVisible(false);
    }



    public void initReaction(LikeService ls, int userId, int publicationId) throws Exception {
        Like reactionValue = ls.getUserReaction(publicationId, userId);
        long reactionId;
        if (reactionValue == null) {
            reactionId = 0;
        } else {
            reactionId = reactionValue.getReactiontype();
        }
        if (reactionId == 0) {
            setReaction(Reactions.NON);
        } else {
            switch ((int)reactionId) {
                case 1:
                    setReaction(Reactions.LIKE);
                    break;
                case 2:
                    setReaction(Reactions.LOVE);
                    break;
                case 3:
                    setReaction(Reactions.HAHA);
                    break;
                case 4:
                    setReaction(Reactions.WOW);
                    break;
                case 5:
                    setReaction(Reactions.SAD);
                    break;
                case 6:
                    setReaction(Reactions.ANGRY);
                    break;
            }
        }
    }


    public void setReaction(Reactions reaction) {
        int userId = 1;

        Image img = new Image(getClass().getResourceAsStream(reaction.getImgSrc()));
        imgReaction.setImage(img);
        reactionName.setText(reaction.getName());
        reactionName.setTextFill(Color.web(reaction.getColor()));
        try {
            if (currentReaction != null && currentReaction != reaction) {
                ls.toggleReaction(publicationId, userId, reaction.getId());

                nbReactions.setText(String.valueOf(ls.countReactions(publicationId) + " Réactions"));
                List<Integer> topReactions = ls.getTop3Reactions(publicationId);
                updateTopReactions(topReactions);
            }

            currentReaction = reaction;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void updateTopReactions(List<Integer> topReactions) {
        ImageView[] reactionImages = {reaction1, reaction2, reaction3};

        for (int i = 0; i < reactionImages.length; i++) {
            if (i < topReactions.size()) {
                int reactionType = topReactions.get(i);
                Reactions reaction = Reactions.fromId(reactionType);

                if (reaction != null) {
                    if (reaction == Reactions.NON) {
                        reactionImages[i].setVisible(false);
                        reactionImages[i].setManaged(false);
                    } else {
                        reactionImages[i].setImage(new Image(getClass().getResourceAsStream(reaction.getImgSrc())));
                        reactionImages[i].setVisible(true);
                        reactionImages[i].setManaged(true);
                    }
                } else {
                    reactionImages[i].setVisible(false);
                    reactionImages[i].setManaged(false);
                }
            } else {
                reactionImages[i].setVisible(false);
                reactionImages[i].setManaged(false);
            }
        }
    }


    private void showWarningDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
