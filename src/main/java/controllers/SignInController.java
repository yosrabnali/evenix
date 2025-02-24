package controllers;

import entities.User;
import services.UserService;
import services.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignInController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signInButton;

    private final UserService userService = new UserService();

    @FXML
    void handleSignIn(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        User user = userService.authenticate(email, password);

        if (user != null) {
            // Stocker l'utilisateur dans la session
            SessionManager.getInstance().setUser(user);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Connexion réussie");
            alert.setHeaderText(null);
            alert.setContentText("Bienvenue, " + user.getNom() + " !");
            alert.showAndWait();

            // Fermer la fenêtre de connexion après succès
            Stage stage = (Stage) signInButton.getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Échec de connexion");
            alert.setHeaderText(null);
            alert.setContentText("Email ou mot de passe incorrect !");
            alert.showAndWait();
        }
    }

    public void loginButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) signInButton.getScene().getWindow();
    }

    public void rememberMeAction(ActionEvent actionEvent) {

    }

    public void forgetPassword(ActionEvent actionEvent) {
        
    }
}
