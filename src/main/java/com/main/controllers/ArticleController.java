package com.main.controllers;

import com.main.Entity.Article;
import com.main.services.PublicationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ArticleController implements Initializable {

    private long articleId;
    PublicationService ps = new PublicationService();

    @FXML
    private Label contenu;

    @FXML
    private ImageView imgarticle;

    @FXML
    private ImageView profileImg;

    @FXML
    private ImageView menutoggle;

    @FXML
    private Label username;

    @FXML
    private VBox sidemenu;

    private Popup sidemenuPopup = new Popup();

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sidemenuPopup.getContent().add(sidemenu);
        sidemenuPopup.setAutoHide(true);
        setData(articleId);
    }
}
