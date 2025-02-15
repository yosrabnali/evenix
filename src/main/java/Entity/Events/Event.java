package Entity.Events;

import java.sql.Date;

public class Event {
    private int idevent;        // PK auto-increment
    private Date date;          // Correspond au champ 'date' (java.sql.Date)
    private String titre;
    private String description;
    private int NBplaces;
    private double prix;
    private String etat;
    private String type;
    private String image;
    private String lieu;
    private int iduser;

    // --- Constructeurs ---

    // Constructeur vide
    public Event() {
    }

    // Constructeur complet (avec idevent)
    public Event(int idevent, Date date, String titre, String description, int NBplaces,
                 double prix, String etat, String type, String image, String lieu, int iduser) {
        this.idevent = idevent;
        this.date = date;
        this.titre = titre;
        this.description = description;
        this.NBplaces = NBplaces;
        this.prix = prix;
        this.etat = etat;
        this.type = type;
        this.image = image;
        this.lieu = lieu;
        this.iduser = iduser;
    }

    // Constructeur sans idevent (pour l'insertion)
    public Event(Date date, String titre, String description, int NBplaces,
                 double prix, String etat, String type, String image, String lieu, int iduser) {
        this.date = date;
        this.titre = titre;
        this.description = description;
        this.NBplaces = NBplaces;
        this.prix = prix;
        this.etat = etat;
        this.type = type;
        this.image = image;
        this.lieu = lieu;
        this.iduser = iduser;
    }

    // --- Getters et Setters ---

    public int getIdevent() {
        return idevent;
    }

    public void setIdevent(int idevent) {
        this.idevent = idevent;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public int getNBplaces() {
        return NBplaces;
    }

    public void setNBplaces(int NBplaces) {
        this.NBplaces = NBplaces;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
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

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public int getIduser() {
        return iduser;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
    }

    @Override
    public String toString() {
        return "Event{" +
                "idevent=" + idevent +
                ", date=" + date +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", NBplaces=" + NBplaces +
                ", prix=" + prix +
                ", etat='" + etat + '\'' +
                ", type='" + type + '\'' +
                ", image='" + image + '\'' +
                ", lieu='" + lieu + '\'' +
                ", iduser=" + iduser +
                '}';
    }
}
