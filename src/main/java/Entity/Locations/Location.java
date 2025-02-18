package Entity.Locations;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.util.Objects;

public class Location {
    private final IntegerProperty idlocation;
    private final ObjectProperty<LocalDate> datedebut;
    private final ObjectProperty<LocalDate> datefin;
    private final IntegerProperty idUser;  // Added to hold user ID
    // Les propriétés IntegerProperty et ObjectProperty sont utilisées pour JavaFX, ce qui permet de lier facilement ces valeurs à une interface graphique.

    public Location(int idlocation, int idUser, LocalDate datedebut, LocalDate datefin) {
        this.idlocation = new SimpleIntegerProperty(idlocation);
        this.idUser = new SimpleIntegerProperty(idUser); // Sets Iduser from the constructor
        this.datedebut = new SimpleObjectProperty<>(datedebut);
        this.datefin = new SimpleObjectProperty<>(datefin);
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
        return datedebut.get();
    }

    public ObjectProperty<LocalDate> datedebutProperty() {
        return datedebut;
    }

    public void setDatedebut(LocalDate datedebut) {
        this.datedebut.set(datedebut);
    }

    public LocalDate getDatefin() {
        return datefin.get();
    }

    public ObjectProperty<LocalDate> datefinProperty() {
        return datefin;
    }

    public void setDatefin(LocalDate datefin) {
        this.datefin.set(datefin);
    }

    // Getter/Setter for idUser

    public int getIdUser() {
        return idUser.get();
    }

    public IntegerProperty idUserProperty() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser.set(idUser);
    }

    @Override
    public String toString() {
        return "Location{" +
                "idlocation=" + idlocation.get() +
                ", datedebut=" + datedebut.get() +
                ", datefin=" + datefin.get() +
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