package controllers.Locations;

import Entity.Locations.Location;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.Locations.ServiceLocation;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
    @FXML private ComboBox<String> dateSortComboBox; // Add ComboBox for date sorting

    private final ServiceLocation serviceLocation = new ServiceLocation();
    private final ObservableList<Location> locationList = FXCollections.observableArrayList();
    private final ObservableList<Location> filteredLocationList = FXCollections.observableArrayList(); // Separate list for filtered data

    private static final long ALERT_THRESHOLD_DAYS = 7; // Nombre de jours avant le début pour l'alerte
    private static final String ALERT_STYLE_CLASS = "location-proche"; // Classe CSS pour l'alerte

    private static final String EMAIL_USERNAME = "hemdenminou@gmail.com";
    private static final String EMAIL_PASSWORD = "szex gsyo jnyu lvaq\n"; // Replace

    private Set<Integer> alertedLocationIds = new HashSet<>();  // Track alerted locations
    private static final int DELAY_BETWEEN_EMAILS_MS = 1000; // 1 second (Rate limiting)
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);  //  Thread Pool

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idlocationProperty().asObject());
        colDateDebut.setCellValueFactory(cellData -> cellData.getValue().datedebutProperty());
        colDateFin.setCellValueFactory(cellData -> cellData.getValue().datefinProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty()); // Use the property directly

        // Configure la cellule pour afficher le status et gérer les alertes
        colStatus.setCellFactory(column -> new TableCell<Location, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                TableRow<Location> row = getTableRow(); // Récupère la ligne

                if (empty || row == null) {
                    setText(null);
                    if (row != null) {
                        row.getStyleClass().remove(ALERT_STYLE_CLASS); // Enlève le style si la cellule est vide ou la ligne est nulle
                    }
                } else {
                    Location location = getTableView().getItems().get(getIndex()); // Récupère l'objet Location associé à cette cellule
                    if (location == null) {
                        setText(null);
                        if (row != null) {
                            row.getStyleClass().remove(ALERT_STYLE_CLASS); // Enlève le style si location est null
                        }
                        return;
                    }
                    setText(location.getStatus());
                    // Vérifie si la date de début est proche et ajoute ou supprime le style
                    LocalDate now = LocalDate.now();
                    LocalDate dateDebut = location.getDatedebut();
                    if(dateDebut != null){
                        long daysUntilStart = ChronoUnit.DAYS.between(now, dateDebut);
                        if (daysUntilStart >= 0 && daysUntilStart <= ALERT_THRESHOLD_DAYS) {
                            if (!row.getStyleClass().contains(ALERT_STYLE_CLASS)) {
                                row.getStyleClass().add(ALERT_STYLE_CLASS);
                            }
                        } else {
                            row.getStyleClass().remove(ALERT_STYLE_CLASS);
                        }
                    }else {
                        row.getStyleClass().remove(ALERT_STYLE_CLASS);
                    }


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

        // Initialize the date sort ComboBox
        dateSortComboBox.getItems().addAll("Aucun", "Date croissant", "Date décroissant");
        dateSortComboBox.setValue("Aucun"); // Set default value
        dateSortComboBox.setOnAction(event -> sortLocationsByDate()); // Attach sort action

        // Enable sorting on date columns
        colDateDebut.setSortable(true);
        colDateFin.setSortable(true);

        // Set the comparator for the start date column
        colDateDebut.setComparator(Comparator.nullsLast(Comparator.naturalOrder()));

        // Set the comparator for the end date column
        colDateFin.setComparator(Comparator.nullsLast(Comparator.naturalOrder()));

        chargerLocations();

        // Démarre le système d'alerte (toutes les 60 secondes)
        startAlertSystem();
    }

    private void chargerLocations() {
        locationList.clear();
        List<Location> locations = serviceLocation.getAll();
        locationList.addAll(locations);
        filteredLocationList.clear(); // Clear the filtered list when loading
        filteredLocationList.addAll(locationList); // Initially, filtered list contains all locations
        applyFiltersAndSort(); // Apply initial filters and sort
    }

    // Méthode pour filtrer les locations en fonction du statut
    @FXML
    private void filterLocations() {
        applyFiltersAndSort();
    }

    @FXML
    private void sortLocationsByDate() {
        applyFiltersAndSort();
    }

    private void applyFiltersAndSort() {
        String selectedStatus = statusFilterComboBox.getValue();
        String selectedSort = dateSortComboBox.getValue();

        // 1. Apply Filters
        ObservableList<Location> filteredList = FXCollections.observableArrayList(locationList);

        if (!"Tous".equals(selectedStatus)) {
            filteredList.removeIf(location -> !selectedStatus.equals(location.getStatus()));
        }

        // 2. Apply Sorting
        Comparator<Location> dateComparator = null;

        switch (selectedSort) {
            case "Date croissant":
                dateComparator = Comparator.comparing(Location::getDatedebut, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "Date décroissant":
                dateComparator = Comparator.comparing(Location::getDatedebut, Comparator.nullsLast(Comparator.reverseOrder()));
                break;
            case "Aucun":
                // Do nothing. Keep the order as is.
                break;
            default:
                LOGGER.warning("Unknown sort option: " + selectedSort);
                break;
        }

        if (dateComparator != null) {
            filteredList.sort(dateComparator);
        }

        // 3. Update TableView
        tableView.setItems(filteredList);
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

            selectedLocation.setDatedebut(dateDebut);
            selectedLocation.setDatefin(dateFin);
            selectedLocation.updateStatus();

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
            //Schedule the email, now with throttling and handled by multi threading
            scheduleEmailTask("hdmminiar@gmail.com", "Location terminée", emailContent);

            // Optional: Update status in UI

        } else {
            afficherAlerte("Alerte", "Cette location n'est pas encore terminée.");
        }
    }
    public static void sendEmail(String to, String subject, String content) {
        // Sender's email credentials
        final String username = "hemdenminou@gmail.com"; // Replace
        final String password = "szex gsyo jnyu lvaq\n"; // Replace

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
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

    private void startAlertSystem() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(60), event -> {  // Vérifie toutes les 60 secondes
                    checkUpcomingLocationsAndSendAlerts(); // Call the new method
                    updateLocationAlerts();
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateLocationAlerts() {
        //Itérer à travers chaque location dans la liste filtrée et rafraîchir son style.
        // Iterate through each location in the filtered list and refresh its style.
        for (Location location : filteredLocationList) {
            // Find the TableRow associated with the Location object.
            for (int i = 0; i < tableView.getItems().size(); i++) {
                if (tableView.getItems().get(i) == location) {
                    // Refresh the TableView to update the styles.
                    tableView.refresh();
                    break; // Exit the loop once the Location is found.
                }
            }
        }
    }

    //NEW METHOD: Check for upcoming locations and send email alerts
    private void checkUpcomingLocationsAndSendAlerts() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //Format date for email

        for (Location location : locationList) { // Check ALL locations, not just filtered ones.
            if ("À venir".equals(location.getStatus())) {  //Only check 'À venir' locations
                LocalDate startDate = location.getDatedebut();
                if (startDate != null) {
                    long daysUntilStart = ChronoUnit.DAYS.between(now, startDate);

                    if (daysUntilStart >= 0 && daysUntilStart <= ALERT_THRESHOLD_DAYS) {
                        //  Check if the location has already been alerted
                        if (!alertedLocationIds.contains(location.getIdlocation())) {  // IMPORTANT: CHECK IF WE ALREADY SENT IT

                            // Capture current state in final variables for use within the task
                            final int locationId = location.getIdlocation();
                            final LocalDate dateDebut = location.getDatedebut();

                            //Schedule the email, now with throttling and handled by multi threading
                            scheduleEmailTask("hdmminiar@gmail.com", "Votre location commence bientôt !", String.format("Votre location %d commence le %s.  Pensez à vous préparer !",
                                    locationId, dateDebut.format(formatter)));

                            // Mark alert already sent after scheduling the email

                        } else {
                            LOGGER.log(Level.INFO, "Start soon alert already sent for Location ID: " + location.getIdlocation());
                        }
                    }
                }
            }
        }
    }

    private void scheduleEmailTask(String to, String subject, String content) {
        scheduler.schedule(() -> {

            try {
                sendEmail(to, subject, content);
                LOGGER.log(Level.INFO, "Email sent to : " + to);

                // Add location ID to the set of alerted locations when send succesfully
                List<Location> locations = serviceLocation.getAll(); // Get all locations
                for (Location location : locations) {
                    List<Location> listeLoc = serviceLocation.getAll();
                    for (Location loc : listeLoc) {
                        alertedLocationIds.add(loc.getIdlocation()); // Mark as sent
                    }
                }
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();

            }

        }, DELAY_BETWEEN_EMAILS_MS, TimeUnit.MILLISECONDS);
    }
}