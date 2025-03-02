package Entity.Reclamations;

import java.sql.Date;

public class Reclamation {
    private int idreclamation;
    private int iduser;
    private String description;
    private String titre;
    private Date date;
    private String fichier;
    private int idevent; // Nouveau champ pour la clé étrangère

    // Constructeur avec fichier et idevent
    public Reclamation(int idreclamation, int iduser, String description, String titre, Date date, String fichier, int idevent) {
        this.idreclamation = idreclamation;
        this.iduser = iduser;
        this.description = description;
        this.titre = titre;
        this.date = date;
        this.fichier = fichier;
        this.idevent = idevent;
    }

    // Constructeur sans fichier mais avec idevent
    public Reclamation(int idreclamation, int iduser, String description, String titre, Date date, int idevent) {
        this(idreclamation, iduser, description, titre, date, null, idevent);
    }

    // Constructeur pour l'ajout d'une nouvelle réclamation (sans ID) avec idevent
    public Reclamation(int iduser, String description, String titre, Date date, String fichier, int idevent) {
        this.iduser = iduser;
        this.description = description;
        this.titre = titre;
        this.date = date;
        this.fichier = fichier;
        this.idevent = idevent;
    }

    // Getters et Setters
    public int getIdreclamation() { return idreclamation; }
    public void setIdreclamation(int idreclamation) { this.idreclamation = idreclamation; }

    public int getIduser() { return iduser; }
    public void setIduser(int iduser) { this.iduser = iduser; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getFichier() { return fichier; }
    public void setFichier(String fichier) { this.fichier = fichier; }

    public int getIdevent() { return idevent; }
    public void setIdevent(int idevent) { this.idevent = idevent; }

    @Override
    public String toString() {
        return "Reclamation{" +
                "idreclamation=" + idreclamation +
                ", iduser=" + iduser +
                ", description='" + description + '\'' +
                ", titre='" + titre + '\'' +
                ", date=" + date +
                ", fichier='" + fichier + '\'' +
                ", idevent=" + idevent +
                '}';
    }
}