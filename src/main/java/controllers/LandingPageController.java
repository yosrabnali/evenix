package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class LandingPageController {

    @FXML
    private Button signInButton, signUpButton;

    @FXML
    private void goToSignIn(ActionEvent event) {
        System.out.println("🔵 Navigating to Sign In Page...");
        switchScene(event, "/user/fxml/signin.fxml");
    }

    @FXML
    private void goToSignUp(ActionEvent event) {
        System.out.println("🟢 Navigating to Sign Up Page...");
        switchScene(event, "/user/fxml/signup.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error loading " + fxmlFile);
        }
    }
}
