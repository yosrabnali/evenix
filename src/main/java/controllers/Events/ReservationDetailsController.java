package controllers.Events;

import Entity.Events.Event;
import Entity.Events.Reservation;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import services.EventsServices.ServiceReservation;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

public class ReservationDetailsController {
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

    // Current ticket count
    private int ticketCount = 0;
    private static final int MAX_TICKETS = 5;
    private static final int MIN_TICKETS = 0;

    // Price per ticket
    private double priceperticket = 20;
    private Event event;
    @FXML
    public void initialize() {
        comboEtat.getItems().addAll("paypal", "mastercard", "visa");
        updateTicketCountLabel();
        updatePriceLabel();
    }
    /**
     * Cette méthode permet de recevoir l'événement à afficher.
     */
    public void setEvent(Event event) {
        this.event = event;
        lblTitle.setText("Titre : "+ event.getTitre());
        lblDescription.setText("Description: "+event.getDescription());
        lblDate.setText("Date: "+event.getDate().toString());
        lblLieu.setText(event.getLieu());
        lblprix.setText("Prix: "+event.getPrix()+" dt");
        priceperticket = event.getPrix();
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
        lblPrice.setText("Total Price: " + totalPrice + " dt");
    }
    /**
     * Action de confirmation de la réservation.
     */
    @FXML
    private void handleConfirmReservation(ActionEvent actionEvent) {
        // Ici, vous pouvez ajouter la logique de réservation (ex : appel à un service)
        System.out.println("Réservation confirmée pour l'événement : " + event.getTitre());
        String etat = comboEtat.getValue();
        int nbPlaces = Integer.parseInt(lblTicketCount.getText());
        Double prix = event.getPrix() * nbPlaces;
        // Par exemple, rediriger l'utilisateur vers la page client (EventClient.fxml)
        try {
            ServiceReservation serviceReservation = new ServiceReservation();
            Reservation r = new Reservation(new Date(), nbPlaces, new BigDecimal(prix), etat, event.getIdevent());
            serviceReservation.ajouterReservation(r);
            Parent root = FXMLLoader.load(getClass().getResource("/Main/UserMainLayout.fxml"));
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
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
            Parent root = FXMLLoader.load(getClass().getResource("/Main/UserMainLayout.fxml"));
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}