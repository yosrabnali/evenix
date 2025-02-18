package controllers.Events;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class EventView {

    @FXML
    private Button submitButton;

    @FXML
    private TextField inputTextField;

    // Initialisation du contrôleur (optionnel si nécessaire)
    @FXML
    public void initialize() {
        System.out.println("Controller initialized!");
    }

    // Méthode déclenchée par le bouton
    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        String userInput = inputTextField.getText();

        if (userInput.isEmpty()) {
            showAlert("Erreur", "Veuillez saisir une valeur avant de soumettre !");
        } else {
            showAlert("Succès", "Vous avez saisi : " + userInput);
        }
    }

    // Méthode pour afficher une alerte simple
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

