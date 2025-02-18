package controllers.Events;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import Entity.Events.Event;
import services.EventsServices.ServiceEvent;
import java.util.List;

public class EventListController {

    @FXML private ListView<VBox> eventListView;

    private final ServiceEvent serviceEvent = new ServiceEvent();

    /* @FXML
     public void initialize() {
         loadEvents();
     }

      ... private void loadEvents() {
      //   List<Event> events = serviceEvent.getAllEvents();

         for (Entity.Events.Event event : events) {
             VBox eventCard = new VBox(10);
             HBox hbox = new HBox(10);

             ImageView eventImage = new ImageView(new Image(event.getImage()));
             eventImage.setFitWidth(100);
             eventImage.setFitHeight(100);

             VBox detailsBox = new VBox(5);
             Label title = new Label(event.getTitre());
             title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
             Label date = new Label("Date: " + event.getDate());
             Label lieu = new Label("Lieu: " + event.getLieu());
             Label places = new Label("Places: " + event.getNBplaces());
             Label price = new Label("Prix: " + event.getPrix() + "€");

             Button reserveButton = new Button("Réserver");
             reserveButton.setOnAction(e -> openReservationPage(event));

             detailsBox.getChildren().addAll(title, date, lieu, places, price, reserveButton);
             hbox.getChildren().addAll(eventImage, detailsBox);
             eventCard.getChildren().add(hbox);

             eventListView.getItems().add(eventCard);
         }
     }
  */
    private void openReservationPage(Event event) {
        System.out.println("Redirection vers la page de réservation pour: " + event.getTitre());
        // Implémenter la navigation vers Reservation.fxml ici
    }
}
