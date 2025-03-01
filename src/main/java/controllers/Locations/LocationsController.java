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

private static final long ALERT_THRESHOLD_DAYS = 7; // Number of days before start for the alert
private static final String ALERT_STYLE_CLASS = "location-proche"; // CSS class for the alert

private static final String EMAIL_USERNAME = "evenixgroup@gmail.com";
private static final String EMAIL_PASSWORD = "gbhc nnrx hvzr ekrb"; // Replace

private Set<Integer> alertedLocationIds = new HashSet<>();  // Track alerted locations
private static final int DELAY_BETWEEN_EMAILS_MS = 1000; // 1 second (Rate limiting) //Delay between sending two emails to avoid spam
private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);  //  Thread Pool

@FXML
public void initialize() {
    colId.setCellValueFactory(cellData -> cellData.getValue().idlocationProperty().asObject());
    colDateDebut.setCellValueFactory(cellData -> cellData.getValue().datedebutProperty());
    colDateFin.setCellValueFactory(cellData -> cellData.getValue().datefinProperty());
    colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty()); // Use the property directly

    // Configures the cell to display the status and manage alerts
    colStatus.setCellFactory(column -> new TableCell<Location, String>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            TableRow<Location> row = getTableRow(); // Gets the row

            if (empty || row == null) {
                setText(null);
                if (row != null) {
                    row.getStyleClass().remove(ALERT_STYLE_CLASS); // Removes the style if the cell is empty or the row is null
                }
            } else {
                Location location = getTableView().getItems().get(getIndex()); // Gets the Location object associated with this cell
                if (location == null) {
                    setText(null);
                    if (row != null) {
                        row.getStyleClass().remove(ALERT_STYLE_CLASS); // Removes the style if location is null
                    }
                    return;
                }
                setText(location.getStatus());
                // Checks if the start date is near and adds or removes the style
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
    statusFilterComboBox.getItems().addAll("All", "Upcoming", "In Progress", "Completed");
    statusFilterComboBox.setValue("All"); // Set default value
    statusFilterComboBox.setOnAction(event -> filterLocations()); // Attach filter action

    // Initialize the date sort ComboBox
    dateSortComboBox.getItems().addAll("None", "Date Ascending", "Date Descending");
    dateSortComboBox.setValue("None"); // Set default value
    dateSortComboBox.setOnAction(event -> sortLocationsByDate()); // Attach sort action

    // Enable sorting on date columns
    colDateDebut.setSortable(true);
    colDateFin.setSortable(true);

    // Set the comparator for the start date column
    colDateDebut.setComparator(Comparator.nullsLast(Comparator.naturalOrder()));

    // Set the comparator for the end date column
    colDateFin.setComparator(Comparator.nullsLast(Comparator.naturalOrder()));

    loadLocations();

    // Starts the alert system (every 60 seconds)
    startAlertSystem();
}

private void loadLocations() {
    locationList.clear();
    List<Location> locations = serviceLocation.getAll();
    locationList.addAll(locations);
    filteredLocationList.clear(); // Clear the filtered list when loading
    filteredLocationList.addAll(locationList); // Initially, filtered list contains all locations
    applyFiltersAndSort(); // Apply initial filters and sort
}

// Method to filter locations based on the status
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

    if (!"All".equals(selectedStatus)) {
        filteredList.removeIf(location -> !selectedStatus.equals(location.getStatus()));
    }

    // 2. Apply Sorting
    Comparator<Location> dateComparator = null;

    switch (selectedSort) {
        case "Date Ascending":
            dateComparator = Comparator.comparing(Location::getDatedebut, Comparator.nullsLast(Comparator.naturalOrder()));
            break;
        case "Date Descending":
            dateComparator = Comparator.comparing(Location::getDatedebut, Comparator.nullsLast(Comparator.reverseOrder()));
            break;
        case "None":
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
private void addLocation() {
    try {
        // Validate fields
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (dateDebut == null || dateFin == null) {
            showAlert("Error", "Please fill in all the fields.");
            return;
        }

        // Verify that the start date is before the end date
        if (dateDebut.isAfter(dateFin)) {
            showAlert("Error", "The start date must be before the end date.");
            return;
        }

        // Retrieve the user ID (e.g., from the session)
        int idUser = retrieveUserId();

        // Create a new location
        Location nouvelleLocation = new Location(0, idUser, dateDebut, dateFin);

        // Add the location via the service
        boolean success = serviceLocation.add(nouvelleLocation);

        if (success) {
            showInformation("Success", "Location added successfully!");
            loadLocations(); // Reload the locations in the TableView
            clearFields(); // Clear the fields of the form

            // **RESTART the alert system after adding a new location**
            alertedLocationIds.clear(); // IMPORTANT: Clear the alerted IDs so that the alert will be sent.
            startAlertSystem();

        } else {
            showAlert("Error", "Failed to add the location.");
        }
    } catch (Exception e) {
        showAlert("Error", "Error adding the location: " + e.getMessage());
    }
}

// Modify an existing location
@FXML
private void modifyLocation() {
    Location selectedLocation = tableView.getSelectionModel().getSelectedItem();
    if (selectedLocation == null) {
        showAlert("Error", "Please select a location to modify.");
        return;
    }

    try {
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (dateDebut == null || dateFin == null) {
            showAlert("Error", "Please fill in all the fields.");
            return;
        }

        // Verify that the start date is before the end date
        if (dateDebut.isAfter(dateFin)) {
            showAlert("Error", "The start date must be before the end date.");
            return;
        }

        selectedLocation.setDatedebut(dateDebut);
        selectedLocation.setDatefin(dateFin);
        selectedLocation.updateStatus();

        boolean success = serviceLocation.update(selectedLocation);

        if (success) {
            showInformation("Success", "Location modified successfully!");
            loadLocations();
            clearFields();

            // **RESTART the alert system after modifying a location**
            alertedLocationIds.clear(); // IMPORTANT: Clear the alerted IDs so that the alert will be sent again if needed.
            startAlertSystem();

        } else {
            showAlert("Error", "Failed to modify the location.");
        }
    } catch (Exception e) {
        showAlert("Error", "Error modifying the location: " + e.getMessage());
    }
}

// Delete a location
@FXML
private void deleteLocation() {
    Location selectedLocation = tableView.getSelectionModel().getSelectedItem();
    if (selectedLocation == null) {
        showAlert("Error", "Please select a location to delete.");
        return;
    }

    // Confirmation of deletion
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Deletion Confirmation");
    confirmation.setHeaderText(null);
    confirmation.setContentText("Are you sure you want to delete this location?");

    ButtonType result = confirmation.showAndWait().orElse(ButtonType.CANCEL);
    if (result == ButtonType.OK) {
        boolean success = serviceLocation.delete(selectedLocation);
        if (success) {
            showInformation("Success", "Location deleted successfully!");
            loadLocations();
        } else {
            showAlert("Error", "Failed to delete the location.");
        }
    }
}

// Show the details of the selected location
@FXML
private void showDetails() {
    // Retrieve the selected location
    Location selectedLocation = tableView.getSelectionModel().getSelectedItem();

    if (selectedLocation != null) {
        // Open the new interface to display the location lines
        openLigneLocationView(selectedLocation.getIdlocation());
    } else {
        showAlert("Error", "Please select a location to show the details.");
    }
}

// Open the new interface to display the location lines
private void openLigneLocationView(int idLocation) {
    try {
        System.out.println("Loading LigneLocationView.fxml..."); // Debug
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Location/LigneLocationView.fxml")); //Ensure it's the right path
        Parent root = loader.load();
        System.out.println("FXML loaded successfully."); // Debug

        // Retrieve the controller of the new interface
        LigneLocationController controller = loader.getController();
        controller.setIdLocation(idLocation); // Pass the ID of the selected location

        // Create a new scene
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Location lines details");
        stage.show();
    } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Error loading the FXML file: ", e);  // Detailed logging
        showAlert("Error", "Error loading the FXML file: " + e.getMessage());
    } catch (NullPointerException e) {
        LOGGER.log(Level.SEVERE, "The controller or the FXML file could not be found: ", e); // Detailed logging
        showAlert("Error", "The controller or the FXML file could not be found: " + e.getMessage());
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Unexpected error: ", e); // Detailed logging
        showAlert("Error", "Unexpected error: " + e.getMessage());
    }
}

// Clear the form fields
private void clearFields() {
    dateDebutPicker.setValue(null);
    dateFinPicker.setValue(null);
}

// Method to retrieve the user ID (to be adapted according to your logic)
private int retrieveUserId() {
    // Example: retrieve the user ID from the session
    return 1; // Replace with the appropriate logic
}

// Show an error alert
private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

// Show an information alert
private void showInformation(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
//Event button


// Check if the status is "Terminée"
//Checks if a location is finished and sends an email.
private void checkStatusAndSendEmail(Location selectedLocation) {
    if ("Completed".equals(selectedLocation.getStatus())) {
        // Retrieve the dateFin from the selectedLocation
        LocalDate dateFin = selectedLocation.getDatefin();

        // Format the date using DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDateFin = dateFin.format(formatter);

        // Construct the email content with the formatted dateFin
        String emailContent = String.format("We inform you that your location %d ended on %s.\n\nThank you for using our service. We hope to see you soon!",
                selectedLocation.getIdlocation(), formattedDateFin);

        showInformation("Email Sent", "An email has been sent to confirm that the location was completed successfully!");

        //Add Alert for Email being sent.
        //Schedule the email, now with throttling and handled by multi threading
        scheduleEmailTask("hdmminiar@gmail.com", "Location Completed", emailContent);

        // Optional: Update status in UI

    } else {
        showAlert("Alert", "This location is not yet completed.");
    }
}
//Sending an email via SMTP.
public static void sendEmail(String to, String subject, String content) {
    // Sender's email credentials
    final String username = "evenixgroup@gmail.com"; // Replace
    final String password = "gbhc nnrx hvzr ekrb"; // Replace

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
//Periodically triggers the verification of upcoming locations.
//Calls checkUpcomingLocationsAndSendAlerts to send email alerts.
//Starting the alert system to check upcoming locations.
private void startAlertSystem() {
    Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(5), event -> {  // Checks every 5 seconds
                checkUpcomingLocationsAndSendAlerts(); // Call the new method

            })
    );
    timeline.setCycleCount(1); // Run only once
    timeline.play();
}


//NEW METHOD: Check for upcoming locations and send email alerts
//Verification of upcoming locations and sending email alerts if needed.
private void checkUpcomingLocationsAndSendAlerts() {
    LocalDate now = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //Format date for email

    for (Location location : locationList) { // Check ALL locations, not just filtered ones.
        if ("Upcoming".equals(location.getStatus()) && !alertedLocationIds.contains(location.getIdlocation())) {  //Only check 'Upcoming' locations and not already alerted
            LocalDate startDate = location.getDatedebut();
            if (startDate != null) {
                long daysUntilStart = ChronoUnit.DAYS.between(now, startDate);

                if (daysUntilStart >= 0 && daysUntilStart <= ALERT_THRESHOLD_DAYS) {
                    //  Check if the location has already been alerted


                    // Capture current state in final variables for use within the task
                    final int locationId = location.getIdlocation();
                    final LocalDate dateDebut = location.getDatedebut();

                    //Schedule the email, now with throttling and handled by multi threading
                    scheduleEmailTask("hdmminiar@gmail.com", "Your location starts soon!", String.format("Your location %d starts on %s.  Remember to get ready!",
                            locationId, dateDebut.format(formatter)));

                    // Mark alert already sent after scheduling the email
                    alertedLocationIds.add(locationId); //mark alerted

                    LOGGER.log(Level.INFO, "Start soon alert already sent for Location ID: " + location.getIdlocation());

                }
            }
        }
    }
}
//Scheduling the sending of an email with a delay to avoid spam.
private void scheduleEmailTask(String to, String subject, String content) {
    scheduler.schedule(() -> {

        try {
            sendEmail(to, subject, content);
            LOGGER.log(Level.INFO, "Email sent to : " + to);


        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();

        }

    }, DELAY_BETWEEN_EMAILS_MS, TimeUnit.MILLISECONDS);
}}