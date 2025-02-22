package controllers.Events;

import Entity.Events.Event;
import Entity.Events.Reservation;
import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import services.EventsServices.ServiceEvent;
import services.EventsServices.ServiceReservation;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import javafx.scene.control.ScrollPane;

public class ReservationDetailsController  {
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblLieu;
    @FXML
    private Label lblprix;
    @FXML
    private TextField txtNBplaces;
    @FXML
    private ComboBox<String> comboEtat;
    @FXML
    private Button btnConfirmReservation;
    @FXML
    private Button btnCancelReservation;
    @FXML
    private Label lblTicketCount;

    @FXML
    private Button btnDecrease;

    @FXML
    private Button btnIncrease;

    // New label to display the total price
    @FXML
    private Label lblPrice;

    @FXML
    private ImageView coverImage;

    @FXML
    private ScrollPane scrollPane;


    // Current ticket count
    private int ticketCount = 0;
    private static final int MAX_TICKETS = 5;
    private static final int MIN_TICKETS = 0;

    // Price per ticket
    private double priceperticket = 20;
    private Event event;
    @FXML
    private VBox vbox;
    private MapPoint mapCenter;
    private MapView mapView;
    private CustomMapLayer customLayer;

    private final MapPoint eiffelPoint = new MapPoint(48.8583701,2.2944813);
    public void setMapCenter(MapPoint mapCenter) {
        this.mapCenter = mapCenter;
        if (mapView != null) {
            // Update the center of the map view
            mapView.setCenter(mapCenter);
            mapView.flyTo(0, mapCenter, 0.1);

            // Clear existing layers and add a new custom marker layer.
            if (customLayer != null) {
                mapView.removeLayer(customLayer);
            }
            customLayer = new CustomMapLayer(mapCenter.getLatitude(), mapCenter.getLongitude());
            mapView.addLayer(customLayer);
        }
    }
    private MapView createMapView() {
        mapView = new MapView();

        // If mapCenter hasn't been set, use a default value.
        if (mapCenter == null) {
            mapCenter = eiffelPoint;
        }

        mapView.setCenter(mapCenter);
        mapView.setZoom(15);
        mapView.setPrefSize(300, 300);
        mapView.flyTo(0, mapCenter, 0.1);

        // Add a custom marker layer to the map.
        CustomMapLayer customLayer = new CustomMapLayer(mapCenter.getLatitude(), mapCenter.getLongitude());
        mapView.addLayer(customLayer);

        return mapView;
    }


    @FXML
    public void initialize() {
//        setMapCenter(new MapPoint(36.806389, 10.181667));

        MapView createdMapView = createMapView();
        vbox.getChildren().add(createdMapView);

        comboEtat.getItems().addAll("paypal", "mastercard", "visa");
        updateTicketCountLabel();
        updatePriceLabel();
    }
    /**
     * Cette méthode permet de recevoir l'événement à afficher.
     */
    public void setEvent(Event event) {
        this.event = event;
        lblTitle.setText("Title:    "+ event.getTitre());
        lblDate.setText("Date:     "+event.getDate().toString());
        lblDescription.setText("Address:    "+event.getDescription());
        lblLieu.setText("Location:    "+event.getLieu());
        lblprix.setText("Price:   "+event.getPrix()+" dt");
        priceperticket = event.getPrix();
        Image image = new Image("file:" + event.getImage());
        coverImage.setImage(image);
        setMapCenter(new MapPoint(event.getLatitude(), event.getLongitude()));
    }

    /**
     * Handles the action for decreasing the ticket count.
     */
    @FXML
    private void handleDecrease() {
        if (ticketCount > MIN_TICKETS) {
            ticketCount--;
            updateTicketCountLabel();
            updatePriceLabel();
        }
    }

    /**
     * Handles the action for increasing the ticket count.
     */
    @FXML
    private void handleIncrease() {
        if (ticketCount < MAX_TICKETS) {
            ticketCount++;
            updateTicketCountLabel();
            updatePriceLabel();
        }
    }

    /**
     * Updates the ticket count label with the current number of tickets.
     */
    private void updateTicketCountLabel() {
        lblTicketCount.setText(String.valueOf(ticketCount));
    }

    /**
     * Updates the price label based on the current ticket count.
     */
    private void updatePriceLabel() {
        double totalPrice = ticketCount * priceperticket;
        lblPrice.setText("Total Price:   " + totalPrice + " dt");
    }
    /**
     * Action de confirmation de la réservation.
     */
    @FXML
    private void handleConfirmReservation(ActionEvent actionEvent) {


        if (Integer.parseInt(lblTicketCount.getText()) > event.getNBplaces()) {

            showAlert(Alert.AlertType.ERROR, "Erreur", "Nombre de ticket saisie indisponible");
            return;
        }
        // Ici, vous pouvez ajouter la logique de réservation (ex : appel à un service)
        System.out.println("Réservation confirmée pour l'événement : " + event.getTitre());
        String etat = comboEtat.getValue();
        int nbPlaces = Integer.parseInt(lblTicketCount.getText());
        Double prix = event.getPrix() * nbPlaces;
        // Par exemple, rediriger l'utilisateur vers la page client (EventClient.fxml)
        try {
            ServiceReservation serviceReservation = new ServiceReservation();
            Reservation r = new Reservation(1234,new Date(), nbPlaces, new BigDecimal(prix), etat, event.getIdevent());
            serviceReservation.ajouterReservation(r);
            ServiceEvent serviceEvent = new ServiceEvent();
            int newnbplaces = event.getNBplaces() - Integer.parseInt(lblTicketCount.getText());

            if(newnbplaces == 0) {
                serviceEvent.updateEventEtat("Complet", event.getIdevent());
            }
            serviceEvent.updateEvent(newnbplaces, event.getIdevent());
            Parent parentRoot = FXMLLoader.load(getClass().getResource("/Main/UserMainLayout.fxml"));


            // Retrieve the current scene from the event's source
            Scene currentScene = ((Node) actionEvent.getSource()).getScene();

            // Set the new root to the parent view
            currentScene.setRoot(parentRoot);
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Annule la réservation et retourne à la page client.
     */
    @FXML
    private void handleCancelReservation(ActionEvent actionEvent) {
        try {
            Parent parentRoot = FXMLLoader.load(getClass().getResource("/Main/UserMainLayout.fxml"));


            // Retrieve the current scene from the event's source
            Scene currentScene = ((Node) actionEvent.getSource()).getScene();

            // Set the new root to the parent view
            currentScene.setRoot(parentRoot);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}