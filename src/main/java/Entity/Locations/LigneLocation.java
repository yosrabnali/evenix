package Entity.Locations;

import javafx.beans.property.*;

public class LigneLocation {
    private final IntegerProperty idLigneloca;
    private final IntegerProperty idlocation;
    private final IntegerProperty idmateriel;
    private final IntegerProperty quantite;
    private final DoubleProperty montantTotal;
    private String nomMateriel;  // Not a Property :
    // Nom du matériel (⚠️ ce n'est pas une propriété JavaFX car il est seulement utilisé en affichage, pas pour la liaison avec l’UI).


    public LigneLocation(int idLigneloca, int idlocation, int idmateriel, int quantite, double montantTotal) {
        this.idLigneloca = new SimpleIntegerProperty(idLigneloca);
        this.idlocation = new SimpleIntegerProperty(idlocation);
        this.idmateriel = new SimpleIntegerProperty(idmateriel);
        this.quantite = new SimpleIntegerProperty(quantite);
        this.montantTotal = new SimpleDoubleProperty(montantTotal);
    }

    // Les getters permettent de récupérer une valeur, et les setters permettent de la modifier.

    public int getIdLigneloca() {
        return idLigneloca.get();
    }

    public IntegerProperty idLignelocaProperty() {
        return idLigneloca;
    }

    public void setIdLigneloca(int idLigneloca) {
        this.idLigneloca.set(idLigneloca);
    }

    public int getIdlocation() {
        return idlocation.get();
    }

    public IntegerProperty idlocationProperty() {
        return idlocation;
    }

    public void setIdlocation(int idlocation) {
        this.idlocation.set(idlocation);
    }

    public int getIdmateriel() {
        return idmateriel.get();
    }

    public IntegerProperty idmaterielProperty() {
        return idmateriel;
    }

    public void setIdmateriel(int idmateriel) {
        this.idmateriel.set(idmateriel);
    }

    public int getQuantite() {
        return quantite.get();
    }

    public IntegerProperty quantiteProperty() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite.set(quantite);
    }

    public double getMontantTotal() {
        return montantTotal.get();
    }

    public DoubleProperty montantTotalProperty() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal.set(montantTotal);
    }

    // Getter and setter for nomMateriel (NOT a Property)
    public String getNomMateriel() {
        return nomMateriel;
    }

    public void setNomMateriel(String nomMateriel) {
        this.nomMateriel = nomMateriel;
    }
// Une méthode toString() pour afficher l’objet sous forme de texte.
    @Override
    public String toString() {
        return "LigneLocation{" +
                "idLigneloca=" + idLigneloca.get() +
                ", idlocation=" + idlocation.get() +
                ", idmateriel=" + idmateriel.get() +
                ", quantite=" + quantite.get() +
                ", montantTotal=" + montantTotal.get() +
                ", nomMateriel='" + nomMateriel + '\'' +
                '}';
    }
}