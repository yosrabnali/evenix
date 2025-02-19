package controllers;

import entities.Role;
import entities.User;
import services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class UserDashboardController {
    private UserService userService = new UserService();

    @FXML private TextField nomField, prenomField, emailField, telephoneField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private Button editUserButton;

    @FXML private TextField searchEmailField, newNomField, newPrenomField, newTelephoneField;
    @FXML private ComboBox<Role> newRoleComboBox;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> nomColumn, prenomColumn, emailColumn, telephoneColumn, roleColumn;

    @FXML
    public void initialize() {
        // Bind columns to User properties
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Load user list
        loadUserList();

        // Enable the edit button only when a user is selected
        editUserButton.setDisable(true);
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editUserButton.setDisable(newSelection == null);
            if (newSelection != null) {
                System.out.println("ℹ Selected User: " + newSelection.getNom());
            }
        });

        // Populate role selection dropdowns
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
        newRoleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
    }

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab ModifierUtilisateur;

    @FXML
    private Tab AjouterUtilisateur;

    @FXML
    private void switchToModifierUtilisateur() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            // Load user details before switching to modification tab
            loadSelectedUserDetails(selectedUser);
            tabPane.getSelectionModel().select(ModifierUtilisateur);
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun utilisateur sélectionné", "Veuillez sélectionner un utilisateur à modifier.");
        }
    }

    private void loadSelectedUserDetails(User selectedUser) {
        if (selectedUser != null) {
            System.out.println("✅ Loading User: " + selectedUser.getNom() + " " + selectedUser.getPrenom());

            // Populate fields with selected user data
            searchEmailField.setText(selectedUser.getEmail());
            newNomField.setText(selectedUser.getNom());
            newPrenomField.setText(selectedUser.getPrenom());
            newTelephoneField.setText(selectedUser.getTelephone());
            newRoleComboBox.setValue(selectedUser.getRole());

            // Debugging logs
            System.out.println("📌 New Nom Field: " + newNomField.getText());
            System.out.println("📌 New Prénom Field: " + newPrenomField.getText());
            System.out.println("📌 New Téléphone Field: " + newTelephoneField.getText());
            System.out.println("📌 New Role Field: " + newRoleComboBox.getValue());
        }
    }



    @FXML
    private void switchToAjouterUtilisateur() {
        tabPane.getSelectionModel().select(AjouterUtilisateur);
    }


    @FXML
    public void handleAddUser() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String telephone = telephoneField.getText().trim();
        Role role = roleComboBox.getValue();

        // Vérification des champs vides
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || telephone.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Champs obligatoires", "Veuillez remplir tous les champs.");
            return;
        }

        // Vérification du format de l'email
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Email invalide", "Veuillez entrer une adresse email valide.");
            return;
        }

        // Vérification de la force du mot de passe
        if (!isValidPassword(password)) {
            showAlert(Alert.AlertType.WARNING, "Mot de passe faible", "Le mot de passe doit contenir au moins 6 caractères et un chiffre.");
            return;
        }

        // Vérification du format du téléphone
        if (!isValidPhoneNumber(telephone)) {
            showAlert(Alert.AlertType.WARNING, "Téléphone invalide", "Veuillez entrer un numéro de téléphone valide.");
            return;
        }

        // Création et ajout de l'utilisateur
        User user = new User(nom, prenom, email, password, telephone, role);
        userService.Ajouter(user);

        // Confirmation et mise à jour de l'interface
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur ajouté avec succès !");
        clearFields(nomField, prenomField, emailField, passwordField, telephoneField);
        roleComboBox.setValue(null);
        loadUserList();
    }


    // Vérification du format de l'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

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
    @FXML
    public void handleEditUser() {
        // Get the selected user from the TableView
        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun utilisateur sélectionné", "Veuillez sélectionner un utilisateur à modifier.");
            return;
        }

        // Validate input fields
        if (!validateFields(newNomField, newPrenomField, newTelephoneField) || newRoleComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Champs obligatoires", "Veuillez remplir tous les champs.");
            return;
        }

        // Update the user with new values
        selectedUser.setNom(newNomField.getText());
        selectedUser.setPrenom(newPrenomField.getText());
        selectedUser.setTelephone(newTelephoneField.getText());
        selectedUser.setRole(newRoleComboBox.getValue());

        // Debug logs
        System.out.println("✏️ Modifying User:");
        System.out.println("   - Nom: " + selectedUser.getNom());
        System.out.println("   - Prénom: " + selectedUser.getPrenom());
        System.out.println("   - Téléphone: " + selectedUser.getTelephone());
        System.out.println("   - Rôle: " + selectedUser.getRole());

        // Call UserService to update the database
        userService.Modifier(selectedUser);

        // Show success message
        showAlert(Alert.AlertType.INFORMATION, "Modification Réussie", "Les informations de l'utilisateur ont été mises à jour.");

        // Clear input fields
        clearFields(newNomField, newPrenomField, newTelephoneField);
        newRoleComboBox.setValue(null);

        // Refresh user list
        loadUserList();
    }




    @FXML
    public void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            userService.Supprimer(selectedUser.getEmail());
            showAlert(Alert.AlertType.INFORMATION, "Suppression", "Utilisateur supprimé avec succès !");
            loadUserList();
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun utilisateur sélectionné", "Veuillez sélectionner un utilisateur à supprimer.");
        }
    }

    private void loadUserList() {
        List<User> users = userService.Recuperer();
        ObservableList<User> userList = FXCollections.observableArrayList(users);
        userTable.setItems(userList);

        // Debug : Afficher dans la console
        if (users.isEmpty()) {
            System.out.println("⚠ Aucun utilisateur trouvé !");
        } else {
            System.out.println("✅ Nombre d'utilisateurs récupérés : " + users.size());
        }
    }

    private boolean validateFields(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}