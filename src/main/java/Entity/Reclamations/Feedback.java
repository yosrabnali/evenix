package Entity.Reclamations;

import java.sql.Date;

public class Feedback {
    private int idfeedback;
    private String description;
    private String etat;
    private Date date;
    private int idreclamation;

    public Feedback(int idfeedback, String description, String etat, Date date, int idreclamation) {
        this.idfeedback = idfeedback;
        this.description = description;
        this.etat = etat;
        this.date = date;
        this.idreclamation = idreclamation;
    }

    public int getIdfeedback() { return idfeedback; }
    public void setIdfeedback(int idfeedback) { this.idfeedback = idfeedback; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getIdreclamation() { return idreclamation; }
    public void setIdreclamation(int idreclamation) { this.idreclamation = idreclamation; }
}