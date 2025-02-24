package controllers;

import entities.Role;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.UserService;

public class ModifierUtilisateurController {

    @FXML
    private TextField nomField, prenomField, emailField, telephoneField, motDePasseField;
    @FXML
    private ComboBox<Role> roleComboBox;
    @FXML
    private Button saveButton;

    private UserService userService;
    private User userToModify; // Utilisateur à modifier

    @FXML
    public void initialize() {
        userService = new UserService();
        roleComboBox.getItems().setAll(Role.values()); // Remplir la ComboBox avec les rôles
    }

    public void initData(User user) {
        this.userToModify = user; // Initialiser l'utilisateur à modifier
        if (user != null) {
            nomField.setText(user.getNom());
            prenomField.setText(user.getPrenom());
            emailField.setText(user.getEmail());
            telephoneField.setText(user.getTelephone());
            roleComboBox.setValue(user.getRole()); // Définir le rôle actuel
            motDePasseField.setText(user.getMotDePasse()); // Initialiser le champ mot de passe
        }
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
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || role == null) {
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

        // Validation du mot de passe (optionnel)
        if (!motDePasse.isEmpty() && motDePasse.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        // Mettre à jour les données de l'utilisateur
        userToModify.setNom(nom);
        userToModify.setPrenom(prenom);
        userToModify.setEmail(email);
        userToModify.setTelephone(telephone);
        userToModify.setRole(role);

        // Mettre à jour le mot de passe uniquement si le champ n'est pas vide
        if (!motDePasse.isEmpty()) {
            userToModify.setMotDePasse(motDePasse);
        }

        // Appel du service pour mettre à jour l'utilisateur
        boolean success = userService.Modifier(userToModify);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur mis à jour avec succès.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour.");
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
}