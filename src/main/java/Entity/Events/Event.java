package Entity.Events;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class Event {
    private int idEvent;
    private String titre;
    private String description;
    private Date date;
    private String lieu;
    private int NBplaces;
    private BigDecimal prix;
    private String etat;
    private String type;
    private String image;

    public Event(int idEvent, String titre, String description, Date date, String lieu, int NBplaces, BigDecimal prix, String etat, String type, String image) {
        this.idEvent = idEvent;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.lieu = lieu;
        this.NBplaces = NBplaces;
        this.prix = prix;
        this.etat = etat;
        this.type = type;
        this.image = image;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public int getNBplaces() {
        return NBplaces;
    }

    public void setNBplaces(int NBplaces) {
        this.NBplaces = NBplaces;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
