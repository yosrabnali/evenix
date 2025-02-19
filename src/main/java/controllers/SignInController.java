package controllers;
import entities.User;

import entities.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.UserService;
import entities.Role;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignInController {

    @FXML
    private TextField emailTextfield;

    @FXML
    private Hyperlink forgetP;

    @FXML
    private Label invalidText;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordTextfield;

    @FXML
    private CheckBox rememberMeCheckbox;

    @FXML
    private Button backbtn;

    private Stage stage;
    private Scene scene;

    @FXML
    void forgetPassword(ActionEvent event) {
        invalidText.setText("Fonction de récupération du mot de passe en cours...");
    }

    @FXML
    void goToSignup(ActionEvent event) {
        loadPage(event, "/user/fxml/SignUp.fxml");
    }

    @FXML

    private void loginButtonOnAction(ActionEvent event) throws IOException {
        String email = emailTextfield.getText();
        String password = passwordTextfield.getText();

        if (email.isEmpty() || password.isEmpty()) {
            invalidText.setText("Veuillez remplir tous les champs.");
            return;
        }

        UserService userService = new UserService();
        User user = userService.getUserByEmail(email);

        if (user == null) {
            invalidText.setText("Utilisateur introuvable.");
            return;
        }

        if (user.getMotDePasse().equals(password)) {
            if (user.getRole() == Role.ADMIN) {
                loadPage(event, "/user/fxml/Dashboard.fxml");
            } else if (user.getRole() == Role.CLIENT) {
                loadPage(event, "/user/fxml/Dashboard.fxml");
            }
        } else {
            invalidText.setText("Mot de passe incorrect.");
        }
    }





    @FXML
    void rememberMeAction(ActionEvent event) {
        if (rememberMeCheckbox.isSelected()) {
            System.out.println("L'utilisateur souhaite se souvenir de sa connexion.");
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        // Ferme la fenêtre actuelle
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void loadPage(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            if (root == null) {
                throw new IOException("Le fichier FXML " + fxmlPath + " est introuvable.");
            }
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            invalidText.setText("Erreur de chargement : " + e.getMessage());
        }
    }
    // Vérification du format de l'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        return email.matches(emailRegex);
    }
    // Vérification du format du téléphone (exemple pour format international)
    private boolean isValidPhoneNumber(String telephone) {
        String phoneRegex = "^[0-9]{8,15}$"; // Accepte entre 8 et 15 chiffres
        return telephone.matches(phoneRegex);
    }
    // Vérification de la force du mot de passe
    private boolean isValidPassword(String password) {
        // Minimum 6 characters, at least 1 digit
        String passwordRegex = "^(?=.*\\d).{6,}$";
        return password.matches(passwordRegex);
    }



}
