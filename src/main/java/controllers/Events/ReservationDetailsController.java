package controllers.Events;

import Entity.Users.UserSingleton;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import Entity.Events.Event;
import Entity.Events.Reservation;
import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.stripe.model.PaymentIntent;
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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.Node;
import services.EventsServices.PaymentService;
import services.EventsServices.ServiceEvent;
import services.EventsServices.ServiceReservation;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import javafx.scene.control.ScrollPane;

public class ReservationDetailsController  {

    @FXML
    private WebView paymentWebView;

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
    // Bouton "Complaint" ajouté dans le contrôleur
    @FXML
    private Button btnComplaint;
    @FXML
    private Label lblTicketCount;

    @FXML
    private Button btnDecrease;

    @FXML
    private Button btnIncrease;

    // Label pour afficher le prix total
    @FXML
    private Label lblPrice;

    @FXML
    private ImageView coverImage;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox vbox;

    private PaymentService paymentService;

    // Nombre actuel de tickets
    private int ticketCount = 0;
    private static final int MAX_TICKETS = 5;
    private static final int MIN_TICKETS = 0;

    // Prix par ticket
    private double priceperticket = 20;
    private Event event;

    private MapPoint mapCenter;
    private MapView mapView;
    private CustomMapLayer customLayer;

    private final MapPoint eiffelPoint = new MapPoint(48.8583701, 2.2944813);

    public void setMapCenter(MapPoint mapCenter) {
        this.mapCenter = mapCenter;
        if (mapView != null) {
            // Met à jour le centre de la vue de la carte
            mapView.setCenter(mapCenter);
            mapView.flyTo(0, mapCenter, 0.1);

            // Supprime les calques existants et ajoute un nouveau calque personnalisé.
            if (customLayer != null) {
                mapView.removeLayer(customLayer);
            }
            customLayer = new CustomMapLayer(mapCenter.getLatitude(), mapCenter.getLongitude());
            mapView.addLayer(customLayer);
        }
    }

    private MapView createMapView() {
        mapView = new MapView();

        // Si mapCenter n'est pas défini, on utilise une valeur par défaut.
        if (mapCenter == null) {
            mapCenter = eiffelPoint;
        }

        mapView.setCenter(mapCenter);
        mapView.setZoom(15);
        mapView.setPrefSize(300, 300);
        mapView.flyTo(0, mapCenter, 0.1);

        // Ajoute un calque personnalisé à la carte.
        CustomMapLayer customLayer = new CustomMapLayer(mapCenter.getLatitude(), mapCenter.getLongitude());
        mapView.addLayer(customLayer);

        return mapView;
    }

    @FXML
    public void initialize() {
        // setMapCenter(new MapPoint(36.806389, 10.181667));
        MapView createdMapView = createMapView();
        vbox.getChildren().add(createdMapView);
        paymentService = new PaymentService();

        comboEtat.getItems().addAll("paypal", "mastercard", "visa");
        updateTicketCountLabel();
        updatePriceLabel();
    }

    /**
     * Cette méthode permet de recevoir l'événement à afficher.
     */
    public void setEvent(Event event) {
        this.event = event;
        lblTitle.setText("Title: " + event.getTitre());
        lblDate.setText("Date: " + event.getDate().toString());
        lblDescription.setText("Address: " + event.getDescription());
        lblLieu.setText("Location: " + event.getLieu());
        lblprix.setText("Price: " + event.getPrix() + " dt");
        priceperticket = event.getPrix();
        Image image = new Image("file:" + event.getImage());
        coverImage.setImage(image);
        setMapCenter(new MapPoint(event.getLatitude(), event.getLongitude()));
    }

    /**
     * Gère l'action de diminution du nombre de tickets.
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
     * Gère l'action d'augmentation du nombre de tickets.
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
     * Met à jour l'affichage du nombre de tickets.
     */
    private void updateTicketCountLabel() {
        lblTicketCount.setText(String.valueOf(ticketCount));
    }

    /**
     * Met à jour l'affichage du prix total en fonction du nombre de tickets.
     */
    private void updatePriceLabel() {
        double totalPrice = ticketCount * priceperticket;
        lblPrice.setText("Total Price: " + totalPrice + " dt");
    }

    /**
     * Action de confirmation de la réservation.
     */
    @FXML
    private void handleConfirmReservation(ActionEvent actionEvent) {
        try {
            // Création d'une session de paiement via Stripe
            int nbPlaces = Integer.parseInt(lblTicketCount.getText());
            Double prix = event.getPrix() * nbPlaces;
            Session session = paymentService.createCheckoutSession(prix * 100, "usd", event.getIdevent(), event.getTitre());
            String paymentUrl = session.getUrl();

            WebEngine webEngine = paymentWebView.getEngine();
            webEngine.load(paymentUrl);
            paymentWebView.setVisible(true);

            // Ajout d'un écouteur sur les changements d'URL
            webEngine.locationProperty().addListener((obs, oldUrl, newUrl) -> {
                if (newUrl.contains("success")) {
                    paymentWebView.setVisible(false);
                    reserveTicket();
                    showAlert(Alert.AlertType.CONFIRMATION, "Congratulations", "Payment successful");
                } else if (newUrl.contains("cancel")) {
                    paymentWebView.setVisible(false);
                    showAlert(Alert.AlertType.ERROR, "Error", "Payment failed");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            // Gestion appropriée des exceptions
        }
    }

    /**
     * Réalise la réservation du ticket.
     */
    private void reserveTicket() {
        if (Integer.parseInt(lblTicketCount.getText()) > event.getNBplaces()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Nombre de ticket saisi indisponible");
            return;
        }
        System.out.println("Réservation confirmée pour l'événement : " + event.getTitre());
        String etat = comboEtat.getValue();
        int nbPlaces = Integer.parseInt(lblTicketCount.getText());
        Double prix = event.getPrix() * nbPlaces;
        try {
            ServiceReservation serviceReservation = new ServiceReservation();
            Reservation r = new Reservation(UserSingleton.getInstance().getUser().getIduser(), new Date(), nbPlaces, new BigDecimal(prix), etat, event.getIdevent());
            serviceReservation.ajouterReservation(r);
            ServiceEvent serviceEvent = new ServiceEvent();
            int newnbplaces = event.getNBplaces() - nbPlaces;

            if (newnbplaces == 0) {
                serviceEvent.updateEventEtat("Complet", event.getIdevent());
            }
            serviceEvent.updateEvent(newnbplaces, event.getIdevent());

        } catch (SQLException ex) {
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
            Scene currentScene = ((Node) actionEvent.getSource()).getScene();
            currentScene.setRoot(parentRoot);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gère l'action du bouton "Complaint".
     */
    @FXML
    private void handleComplaint(ActionEvent actionEvent) {
        // Implémentez ici la logique de gestion des réclamations.
        // Pour l'instant, on affiche simplement une alerte.
        showAlert(Alert.AlertType.INFORMATION, "Complaint", "Fonctionnalité de réclamation non implémentée.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
