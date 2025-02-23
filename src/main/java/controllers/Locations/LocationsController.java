package controllers.Locations;

import Entity.Locations.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import services.Locations.ServiceLocation;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocationsController {

    private static final Logger LOGGER = Logger.getLogger(LocationsController.class.getName());

    @FXML
    private TableView<Location> tableView;
    @FXML private TableColumn<Location, Integer> colId;
    @FXML private TableColumn<Location, LocalDate> colDateDebut, colDateFin;
    @FXML private TableColumn<Location, String> colStatus;
    @FXML private DatePicker dateDebutPicker, dateFinPicker;
    @FXML private ComboBox<String> statusFilterComboBox; // Add ComboBox for filtering

    private final ServiceLocation serviceLocation = new ServiceLocation();
    private final ObservableList<Location> locationList = FXCollections.observableArrayList();
    private final ObservableList<Location> filteredLocationList = FXCollections.observableArrayList(); // Separate list for filtered data

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idlocationProperty().asObject());
        colDateDebut.setCellValueFactory(cellData -> cellData.getValue().datedebutProperty());
        colDateFin.setCellValueFactory(cellData -> cellData.getValue().datefinProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colStatus.setCellFactory(column -> new TableCell<Location, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Location location = getTableView().getItems().get(getIndex());
                    LocalDate now = LocalDate.now();
                    LocalDate dateDebut = location.getDatedebut();
                    LocalDate dateFin = location.getDatefin();
                    String status;

                    if (now.isBefore(dateDebut)) {
                        status = "À venir";
                    } else if (now.isAfter(dateDebut) && now.isBefore(dateFin)) {
                        status = "En cours";
                    } else {
                        status = "Terminée";
                    }

                    setText(status);
                    location.setStatus(status); // Update the status in the Location object
                    serviceLocation.update(location); // Persist the status update to the database
                }
            }
        });

        tableView.setRowFactory(tv -> {
            TableRow<Location> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty() && event.getButton().equals(MouseButton.PRIMARY)
                        && event.getClickCount() == 2) {

                    Location clickedRow = row.getItem();
                    checkStatusAndSendEmail(clickedRow);
                }
            });
            return row ;
        });

        // Initialize the filter ComboBox
        statusFilterComboBox.getItems().addAll("Tous", "À venir", "En cours", "Terminée");
        statusFilterComboBox.setValue("Tous"); // Set default value
        statusFilterComboBox.setOnAction(event -> filterLocations()); // Attach filter action

        chargerLocations();
    }

    private void chargerLocations() {
        locationList.clear();
        List<Location> locations = serviceLocation.getAll();
        locationList.addAll(locations);
        filteredLocationList.clear(); // Clear the filtered list when loading
        filteredLocationList.addAll(locationList); // Initially, filtered list contains all locations
        tableView.setItems(filteredLocationList);
    }

    // Method to filter locations based on status
    @FXML
    private void filterLocations() {
        String selectedStatus = statusFilterComboBox.getValue();
        filteredLocationList.clear();

        if ("Tous".equals(selectedStatus)) {
            filteredLocationList.addAll(locationList); // Show all locations
        } else {
            for (Location location : locationList) {
                if (selectedStatus.equals(location.getStatus())) {
                    filteredLocationList.add(location); // Add locations matching the selected status
                }
            }
        }

        tableView.setItems(filteredLocationList); // Update the TableView with the filtered list
    }

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

            // Déterminer le statut initial en fonction des dates
            LocalDate now = LocalDate.now();
            String status;
            if (now.isBefore(dateDebut)) {
                status = "À venir";
            } else if (now.isAfter(dateDebut) && now.isBefore(dateFin)) {
                status = "En cours";
            } else {
                status = "Terminée";
            }

            // Créer une nouvelle location
            Location nouvelleLocation = new Location(0, idUser, dateDebut, dateFin);
            nouvelleLocation.setStatus(status);

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

            // Déterminer le nouveau statut en fonction des dates
            LocalDate now = LocalDate.now();
            String status;
            if (now.isBefore(dateDebut)) {
                status = "À venir";
            } else if (now.isAfter(dateDebut) && now.isBefore(dateFin)) {
                status = "En cours";
            } else {
                status = "Terminée";
            }

            // Mettre à jour la location
            selectedLocation.setDatedebut(dateDebut);
            selectedLocation.setDatefin(dateFin);
            selectedLocation.setStatus(status);

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

        ButtonType result = confirmation.showAndWait().orElse(ButtonType.CANCEL);
        if (result == ButtonType.OK) {
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
            LOGGER.log(Level.SEVERE, "Erreur inattendue : ", e); // Detailed logging
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
    //Event button


    // Check if the status is "Terminée"
    private void checkStatusAndSendEmail(Location selectedLocation) {
        if ("Terminée".equals(selectedLocation.getStatus())) {
            // Retrieve the dateFin from the selectedLocation
            LocalDate dateFin = selectedLocation.getDatefin();

            // Format the date using DateTimeFormatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDateFin = dateFin.format(formatter);

            // Construct the email content with the formatted dateFin
            String emailContent = String.format("Nous vous informons que votre location %d s'est terminée le %s.\n\nMerci d'avoir utilisé notre service. Nous espérons vous revoir bientôt !",
                    selectedLocation.getIdlocation(), formattedDateFin);

            afficherInformation("Email envoyé", "Un email a été envoyé pour confirmer que la location à été effectuée avec succes!");

            //Add Alert for Email being sent.
            sendEmail("hdmminiar@mail.com", "Location terminée", emailContent);

            // Optional: Update status in UI

        } else {
            afficherAlerte("Alerte", "Cette location n'est pas encore terminée.");
        }
    }
    public static void sendEmail(String to, String subject, String content) {
        // Sender's email credentials
        final String username = "miniar.hemden@esprit.tn"; // Replace
        final String password = "zbdg gcrs taho onuk\n"; // Replace

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);

            System.out.println("Email sent successfully");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


}