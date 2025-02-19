package controllers;

import entities.User;
import entities.Role;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {

    @FXML
    private TextField nomTextField;
    @FXML
    private TextField prenomTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private TextField telephoneTextField; // Ajout du champ téléphone
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
//        // Initialisation des rôles dans le ComboBox
//        roleComboBox.getItems().addAll("Admin", "Client", "Organisateur", "Prestataire");
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        String nom = nomTextField.getText().trim();
        String prenom = prenomTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String password = passwordTextField.getText().trim();
        String telephone = telephoneTextField.getText().trim();
        String roleString = roleComboBox.getValue();

        // Vérification des champs vides
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || telephone.isEmpty() || roleString == null) {
            errorLabel.setText("Tous les champs sont obligatoires !");
            return;
        }

        // Vérification du format de l'email
        if (!isValidEmail(email)) {
            errorLabel.setText("Adresse email invalide !");
            return;
        }

        // Vérification de la force du mot de passe
        if (!isValidPassword(password)) {
            errorLabel.setText("Le mot de passe invalide!");
            return;
        }

        // Vérification du format du téléphone
        if (!isValidPhoneNumber(telephone)) {
            errorLabel.setText("Numéro de téléphone invalide !");
            return;
        }

        // Conversion du rôle en énumération Role
        Role role;
        try {
            role = Role.fromString(roleString);
        } catch (IllegalArgumentException e) {
            errorLabel.setText("Rôle inconnu sélectionné !");
            return;
        }

        // Création et ajout de l'utilisateur
        User newUser = new User(nom, prenom, email, password, telephone, role);
        userService.Ajouter(newUser);

        // Confirmation message
        showAlert("Succès", "Inscription réussie !", Alert.AlertType.INFORMATION);

        // Redirect to Sign In page
        goToSignIn(event);
    }
    // Vérification du format de l'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    // Vérification de la force du mot de passe
    private boolean isValidPassword(String password) {
        // Minimum 6 characters, at least 1 digit
        String passwordRegex = "^(?=.*\\d).{6,}$";
        return password.matches(passwordRegex);
    }


    // Vérification du format du téléphone (exemple pour format international)
    private boolean isValidPhoneNumber(String telephone) {
        String phoneRegex = "^[0-9]{8,15}$"; // Accepte entre 8 et 15 chiffres
        return telephone.matches(phoneRegex);
    }


    private void goToSignIn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/fxml/SignIn.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de connexion.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void goBack(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nomTextField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
