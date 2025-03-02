package Entity.Locations;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.util.Objects;

public class Location {
    private final IntegerProperty idlocation;
    private final ObjectProperty<LocalDate> startDate;
    private final ObjectProperty<LocalDate> endDate;
    private final IntegerProperty userId;
    private final StringProperty status;

    public Location(int idlocation, int userId, LocalDate startDate, LocalDate endDate) {
        this.idlocation = new SimpleIntegerProperty(idlocation);
        this.userId = new SimpleIntegerProperty(userId);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.status = new SimpleStringProperty(calculateStatus(startDate, endDate));
    }

    // Getters and Setters (with Property methods)
    public int getIdlocation() {
        return idlocation.get();
    }

    public IntegerProperty idlocationProperty() {
        return idlocation;
    }

    public void setIdlocation(int idlocation) {
        this.idlocation.set(idlocation);
    }

    public LocalDate getDatedebut() {
        return startDate.get();
    }

    public ObjectProperty<LocalDate> datedebutProperty() {
        return startDate;
    }

    public void setDatedebut(LocalDate startDate) {
        this.startDate.set(startDate);
        updateStatus(); // Recalculate status when startDate changes
    }

    public LocalDate getDatefin() {
        return endDate.get();
    }

    public ObjectProperty<LocalDate> datefinProperty() {
        return endDate;
    }

    public void setDatefin(LocalDate endDate) {
        this.endDate.set(endDate);
        updateStatus(); // Recalculate status when endDate changes
    }

    public int getUserId() {
        return userId.get();
    }

    public IntegerProperty idUserProperty() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public void updateStatus() {
        this.status.set(calculateStatus(this.getDatedebut(), this.getDatefin()));
    }

    private String calculateStatus(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return "Unknown";
        }
        LocalDate now = LocalDate.now();
        if (now.isBefore(startDate)) {
            return "Upcoming";
        } else if (now.isAfter(endDate)) {
            return "Completed";
        } else {
            return "Ongoing";
        }
    }

    @Override
    public String toString() {
        return "Location{" +
                "idlocation=" + idlocation.get() +
                ", startDate=" + startDate.get() +
                ", endDate=" + endDate.get() +
                ", status='" + status.get() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return idlocation.get() == location.idlocation.get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(idlocation.get());
    }
}