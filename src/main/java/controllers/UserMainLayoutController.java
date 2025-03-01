package controllers;

import Entity.Users.Role;
import Entity.Users.User;
import Entity.Users.UserSingleton;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import java.io.IOException;

public class UserMainLayoutController {

    @FXML
    private BorderPane mainContent;

    @FXML
    private VBox centerContent;

    // ImageView for the animated logo
    @FXML
    private ImageView evenix;

    // Sidebar buttons
    @FXML
    private ImageView dashboardIcon;
    @FXML
    private ImageView eventIcon;
    @FXML
    private ImageView publicationIcon;
    @FXML
    private ImageView materielIcon;
    @FXML
    private ImageView feedbackIcon;
    @FXML
    private ImageView logoutIcon;

    @FXML
    public void initialize() {
        // Example: initialize user (this would usually come from a login screen)
        UserSingleton.getInstance().initializeUser(new User(1234, "Doe", "John", "john.doe@example.com", "password", "1234567890", Role.CLIENT));

        // Update UI based on user state
        updateMenuVisibility();

        // Launch the logo animation
        startRotationEvery2Seconds();

        // Setup tooltips
        Tooltip.install(dashboardIcon, new Tooltip("Dashboard"));
        Tooltip.install(eventIcon, new Tooltip("Event"));
        Tooltip.install(publicationIcon, new Tooltip("Publications"));
        Tooltip.install(feedbackIcon, new Tooltip("Feedback"));
        Tooltip.install(materielIcon, new Tooltip("Material"));
        Tooltip.install(logoutIcon, new Tooltip("Logout"));
    }

    /**
     * Update the visibility of menu icons based on the current user state.
     */
    private void updateMenuVisibility() {
        if (UserSingleton.getInstance().getUser() == null) {
            // nwariw sign in w signup screen
            dashboardIcon.setVisible(false);
            eventIcon.setVisible(false);
            publicationIcon.setVisible(false);
            materielIcon.setVisible(false);
            feedbackIcon.setVisible(false);
            logoutIcon.setVisible(false);
        } else {
            // User is logged in, set default visibility
            dashboardIcon.setVisible(false);
            eventIcon.setVisible(false);
            publicationIcon.setVisible(false);
            materielIcon.setVisible(false);
            feedbackIcon.setVisible(false);
            logoutIcon.setVisible(true);  // Always show logout when logged in

            // Update visibility based on role
            Role currentRole = UserSingleton.getInstance().getUser().getRole();
            if (currentRole == Role.CLIENT) {
                eventIcon.setVisible(true);
                publicationIcon.setVisible(true);
                showevent();
            } else if (currentRole == Role.ORGANISATEUR) {
                eventIcon.setVisible(true);
                dashboardIcon.setVisible(true);
                materielIcon.setVisible(true);
                feedbackIcon.setVisible(true);
                showdashboard();
            } else if (currentRole == Role.PRESTATAIRE) {
                materielIcon.setVisible(true);
                // Add any additional logic if needed
            }
        }
    }

    private void showevent() {
        try {
            FXMLLoader loader;
            // Load different views based on the user's role
            if (UserSingleton.getInstance().getUser().getRole() == Role.CLIENT) {
                loader = new FXMLLoader(getClass().getResource("/Events/EventContent.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/Events/EventAdmin.fxml"));
            }
            Node eventContent = loader.load();
            centerContent.getChildren().setAll(eventContent);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showdashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/dashboard.fxml"));
            Node dashboardContent = loader.load();
            centerContent.getChildren().setAll(dashboardContent);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void startRotationEvery2Seconds() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> rotateImage())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void rotateImage() {
        RotateTransition rotate = new RotateTransition(Duration.seconds(0.5), evenix);
        rotate.setByAngle(360);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();
    }

    @FXML
    private void handleDashboard(MouseEvent event) {
        showdashboard();
    }

    @FXML
    private void handleEvent(MouseEvent event) {
        System.out.println("Event icon clicked");
        showevent();
    }

    @FXML
    private void handlePublication(MouseEvent event) {
        System.out.println("Publication icon clicked");
        VBox publicationContent = new VBox(10);
        publicationContent.getChildren().add(new Label("Publication Content"));
        centerContent.getChildren().setAll(publicationContent);
    }

    @FXML
    private void handleFeedback(MouseEvent event) {
        System.out.println("Feedback icon clicked");
        VBox feedbackContent = new VBox(10);
        feedbackContent.getChildren().add(new Label("Feedback Content"));
        centerContent.getChildren().setAll(feedbackContent);
    }

    @FXML
    private void handleMateriel(MouseEvent event) {
        System.out.println("Materiel icon clicked");
        VBox materielContent = new VBox(10);
        materielContent.getChildren().add(new Label("Material Content"));
        centerContent.getChildren().setAll(materielContent);
    }

    @FXML
    private void handleLogout(MouseEvent event) {
        System.out.println("Logout icon clicked");
        // Clear the user session
        UserSingleton.getInstance().logout();

        // Optionally, show a logout message in the center area
        VBox logoutContent = new VBox(10);
        logoutContent.getChildren().add(new Label("Vous avez été déconnecté."));
        centerContent.getChildren().setAll(logoutContent);

        // Update the menu to reflect that no user is logged in
        updateMenuVisibility();
    }
}
