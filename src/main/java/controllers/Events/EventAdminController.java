package controllers.Events;

import Entity.Events.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import services.EventsServices.ServiceEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EventAdminController implements Initializable {

    @FXML
    private FlowPane flowPaneEvents; // Container to display events

    @FXML
    private Button btnAddEvent;

    private ServiceEvent serviceEvent = new ServiceEvent();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadEvents();
    }

    private void loadEvents() {
        // Retrieve the list of events from the service
        List<Event> events = serviceEvent.getAllEvents();
        // Clear the current content of the FlowPane
        flowPaneEvents.getChildren().clear();

        // For each event, create a "card" (using a VBox)
        for (Event e : events) {
            VBox card = new VBox(5); // 5px spacing between elements
            card.setStyle("-fx-border-color: gray; -fx-padding: 5; -fx-alignment: center;");

            // Create a label for the event title
            Label lblTitle = new Label(e.getTitre());

            // Get the image path from the event (ensure it returns a valid path)
            String imagePath = e.getImage();
            // Create an Image object using the path (prepend "file:" for local files)
            Image image = new Image("file:" + imagePath, 100, 100, true, true);
            ImageView imageView = new ImageView(image);

            // Create the Edit button
            Button btnEdit = new Button("Edit");
            btnEdit.setOnAction(evn -> {
                // Navigate to the EditEventController and pass the event
                navigateToEditEventController(e);
            });

            // Create the Delete button with confirmation
            Button btnDelete = new Button("Delete");
            btnDelete.setOnAction(evn -> {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Delete Confirmation");
                confirmAlert.setHeaderText("Delete Event");
                confirmAlert.setContentText("Are you sure you want to delete this event?");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Delete the event (make sure your Event model has getIdevent() method or adjust accordingly)
                    serviceEvent.deleteEvent(e.getIdevent());

                    // Show a confirmation message
                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setTitle("Deleted");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Event deleted successfully!");
                    infoAlert.showAndWait();

                    // Reload the events to reflect deletion
                    loadEvents();
                }
            });

            // Add the ImageView, title label, and buttons to the card
            card.getChildren().addAll(imageView, lblTitle, btnEdit, btnDelete);
            // Add the card to the FlowPane
            flowPaneEvents.getChildren().add(card);
        }
    }

    /**
     * Navigates to the EditEventController, passing the event to be edited.
     * Ensure that /Events/EditEvent.fxml is correctly placed in your resources.
     */
    private void navigateToEditEventController(Event event) {
        try {
            // Load the FXML for the Edit Event screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/EditEvent.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the event object
            EditEventController controller = loader.getController();
            controller.setEvent(event);

            // Get the current stage and switch the scene
            Stage stage = (Stage) flowPaneEvents.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Navigation Error");
            errorAlert.setHeaderText("Could not load the edit event screen.");
            errorAlert.setContentText(ex.getMessage());
            errorAlert.showAndWait();
        }
    }

    /**
     * Called when the "+" button is clicked to add a new event.
     */
    @FXML
    private void handleAddEvent(ActionEvent event) {
        try {
            // Load the FXML for the Add Event screen (ensure /Events/AddEvent.fxml exists)
            Parent root = FXMLLoader.load(getClass().getResource("/Events/AddEvent.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Navigation Error");
            errorAlert.setHeaderText("Could not load the add event screen.");
            errorAlert.setContentText(ex.getMessage());
            errorAlert.showAndWait();
        }
    }
}