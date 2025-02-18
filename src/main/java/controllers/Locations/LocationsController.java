package controllers.Locations;

import Entity.Locations.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.Locations.ServiceLocation;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;  // Import Logger

public class LocationsController {

    // Logger for error handling
    private static final Logger LOGGER = Logger.getLogger(LocationsController.class.getName());

    // Composants FXML
    @FXML private TableView<Location> tableView;
    @FXML private TableColumn<Location, Integer> colId;
    @FXML private TableColumn<Location, LocalDate> colDateDebut, colDateFin;

    @FXML private DatePicker dateDebutPicker, dateFinPicker;

    // Service et données
    private final ServiceLocation serviceLocation = new ServiceLocation();
    private final ObservableList<Location> locationList = FXCollections.observableArrayList();

    // Méthode d'initialisation
    @FXML
    public void initialize() {
        // Lier les colonnes du TableView aux propriétés de l'entité Location
        colId.setCellValueFactory(cellData -> cellData.getValue().idlocationProperty().asObject());
        colDateDebut.setCellValueFactory(cellData -> cellData.getValue().datedebutProperty());
        colDateFin.setCellValueFactory(cellData -> cellData.getValue().datefinProperty());

        // Charger les locations au démarrage
        chargerLocations();
    }

    // Charger les locations depuis le service
    private void chargerLocations() {
        locationList.clear();
        locationList.addAll(serviceLocation.getAll());
        tableView.setItems(locationList);
    }

    // Ajouter une nouvelle location
    @FXML
    private void ajouterLocation() {
        try {
            // Valider les champs
            LocalDate dateDebut = dateDebutPicker.getValue();
            LocalDate dateFin = dateFinPicker.getValue();

            if (dateDebut == null || dateFin == null) {
                afficherAlerte("Erreur", "Veuillez remplir tous les champs.");
                return;
            }

            // Vérifier que la date de début est avant la date de fin
            if (dateDebut.isAfter(dateFin)) {
                afficherAlerte("Erreur", "La date de début doit être avant la date de fin.");
                return;
            }

            // Récupérer l'ID de l'utilisateur (par exemple, à partir de la session)
            int idUser = recupererIdUser();

            // Créer une nouvelle location
            Location nouvelleLocation = new Location(0, idUser, dateDebut, dateFin);

            // Ajouter la location via le service
            boolean success = serviceLocation.add(nouvelleLocation);

            if (success) {
                afficherInformation("Succès", "Location ajoutée avec succès !");
                chargerLocations(); // Recharger les locations dans le TableView
                clearFields(); // Vider les champs du formulaire
            } else {
                afficherAlerte("Erreur", "Échec de l'ajout de la location.");
            }
        } catch (Exception e) {
            afficherAlerte("Erreur", "Erreur lors de l'ajout de la location : " + e.getMessage());
        }
    }

    // Modifier une location existante
    @FXML
    private void modifierLocation() {
        Location selectedLocation = tableView.getSelectionModel().getSelectedItem();
        if (selectedLocation == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner une location à modifier.");
            return;
        }

        try {
            LocalDate dateDebut = dateDebutPicker.getValue();
            LocalDate dateFin = dateFinPicker.getValue();

            if (dateDebut == null || dateFin == null) {
                afficherAlerte("Erreur", "Veuillez remplir tous les champs.");
                return;
            }

            // Vérifier que la date de début est avant la date de fin
            if (dateDebut.isAfter(dateFin)) {
                afficherAlerte("Erreur", "La date de début doit être avant la date de fin.");
                return;
            }

            // Mettre à jour la location
            selectedLocation.setDatedebut(dateDebut);
            selectedLocation.setDatefin(dateFin);

            boolean success = serviceLocation.update(selectedLocation);

            if (success) {
                afficherInformation("Succès", "Location modifiée avec succès !");
                chargerLocations();
                clearFields();
            } else {
                afficherAlerte("Erreur", "Échec de la modification de la location.");
            }
        } catch (Exception e) {
            afficherAlerte("Erreur", "Erreur lors de la modification de la location : " + e.getMessage());
        }
    }

    // Supprimer une location
    @FXML
    private void supprimerLocation() {
        Location selectedLocation = tableView.getSelectionModel().getSelectedItem();
        if (selectedLocation == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner une location à supprimer.");
            return;
        }

        // Confirmation de suppression
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette location ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = serviceLocation.delete(selectedLocation);
            if (success) {
                afficherInformation("Succès", "Location supprimée avec succès !");
                chargerLocations();
            } else {
                afficherAlerte("Erreur", "Échec de la suppression de la location.");
            }
        }
    }

    // Afficher les détails de la location sélectionnée
    @FXML
    private void afficherDetails() {
        // Récupérer la location sélectionnée
        Location selectedLocation = tableView.getSelectionModel().getSelectedItem();

        if (selectedLocation != null) {
            // Ouvrir la nouvelle interface pour afficher les lignes de location
            ouvrirLigneLocationView(selectedLocation.getIdlocation());
        } else {
            // Afficher une alerte si aucune location n'est sélectionnée
            afficherAlerte("Erreur", "Veuillez sélectionner une location pour afficher les détails.");
        }
    }

    // Ouvrir la nouvelle interface pour afficher les lignes de location
    private void ouvrirLigneLocationView(int idLocation) {
        try {
            System.out.println("Chargement de LigneLocationView.fxml..."); // Debug
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Location/LigneLocationView.fxml")); //Ensure it's the right path
            Parent root = loader.load();
            System.out.println("FXML chargé avec succès."); // Debug

            // Récupérer le contrôleur de la nouvelle interface
            LigneLocationController controller = loader.getController();
            controller.setIdLocation(idLocation); // Passer l'ID de la location sélectionnée

            // Créer une nouvelle scène
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Détails des lignes de location");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement du fichier FXML : ", e);  // Detailed logging
            afficherAlerte("Erreur", "Erreur lors du chargement du fichier FXML : " + e.getMessage());
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Le contrôleur ou le fichier FXML est introuvable : ", e); // Detailed logging
            afficherAlerte("Erreur", "Le contrôleur ou le fichier FXML est introuvable : " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue : ", e); // Catch all exceptions
            afficherAlerte("Erreur", "Erreur inattendue : " + e.getMessage());
        }
    }

    // Vider les champs du formulaire
    private void clearFields() {
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
    }

    // Méthode pour récupérer l'ID de l'utilisateur (à adapter selon votre logique)
    private int recupererIdUser() {
        // Exemple : récupérer l'ID de l'utilisateur à partir de la session
        return 1; // Remplacez par la logique appropriée
    }

    // Afficher une alerte d'erreur
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Afficher une alerte d'information
    private void afficherInformation(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}