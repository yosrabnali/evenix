package controllers.Events;

import Entity.Events.Event;
import Entity.Events.Reservation;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import services.EventsServices.ServiceEvent;
import services.EventsServices.ServiceReservation;

public class ReservationListController {

    @FXML
    private ListView<Reservation> reservationListView;

    private ServiceReservation reservationService;
    private ServiceEvent eventService;

    public ReservationListController() {
        // Initialize your services (using your singleton MyDB connection)
        reservationService = new ServiceReservation();
        eventService = new ServiceEvent();
    }

    @FXML
    public void initialize() {
        loadReservations();
        // Set a custom cell factory to load ReservationCell.fxml for each cell.
        reservationListView.setCellFactory(new Callback<ListView<Reservation>, ListCell<Reservation>>() {
            @Override
            public ListCell<Reservation> call(ListView<Reservation> listView) {
                return new ReservationListCell();
            }
        });
    }

    private void loadReservations() {
        try {
            List<Reservation> reservations = reservationService.afficherReservations();
            System.out.println("affichage reservation");
            System.out.println(reservations);
            reservationListView.getItems().setAll(reservations);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Custom ListCell that loads the ReservationCell.fxml for each reservation.
    class ReservationListCell extends ListCell<Reservation> {
        private HBox cellContainer;
        private ReservationCellController cellController;

        public ReservationListCell() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events/ReservationCell.fxml"));
                cellContainer = loader.load();  // Now this works, because the FXML root is an HBox
                cellController = loader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void updateItem(Reservation reservation, boolean empty) {
            super.updateItem(reservation, empty);
            if (empty || reservation == null) {
                setText(null);
                setGraphic(null);
            } else {
                // Retrieve the related event details using reservation.getIdEvent()
                Event event = eventService.getEventById(reservation.getIdEvent());
                // Pass the reservation and event to the cell controller
                cellController.setReservationData(reservation, event);
                setGraphic(cellContainer);
            }
        }
    }
}
