package controllers;

import entities.Role;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.UserService;

public class AddUserController{

    @FXML
    private TextField nomField, prenomField, emailField, telephoneField, motDePasseField;
    @FXML
    private ComboBox<Role> roleComboBox;
    @FXML
    private Button saveButton;

    private UserService userService;

    @FXML
    public void initialize() {
        userService = new UserService();
        roleComboBox.getItems().setAll(Role.values()); // Remplir la ComboBox avec les rôles
    }

    @FXML
    private void handleSaveChanges() {
        // Récupérer les valeurs des champs
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String motDePasse = motDePasseField.getText().trim();
        Role role = roleComboBox.getValue();

        // Validation des champs
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || motDePasse.isEmpty() || role == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs obligatoires doivent être remplis.");
            return;
        }

        // Validation de l'email
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir une adresse email valide.");
            return;
        }

        // Validation du téléphone
        if (!isValidTelephone(telephone)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le téléphone doit contenir uniquement des chiffres et avoir 8 à 10 caractères.");
            return;
        }

        // Validation du mot de passe
        if (motDePasse.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        // Création du nouvel utilisateur
        User newUser = new User(nom, prenom, email, motDePasse, telephone, role);

        // Appel du service pour ajouter l'utilisateur
        boolean success = userService.Ajouter(newUser);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur ajouté avec succès.");
            clearFields(); // Vider les champs après l'ajout
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout de l'utilisateur.");
        }
    }

    // Méthode pour valider l'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    // Méthode pour valider le téléphone
    private boolean isValidTelephone(String telephone) {
        String telephoneRegex = "\\d{8,10}"; // 8 à 10 chiffres
        return telephone.matches(telephoneRegex);
    }

    // Méthode pour afficher une alerte
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour vider les champs après l'ajout
    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        motDePasseField.clear();
        roleComboBox.getSelectionModel().clearSelection();
    }
}