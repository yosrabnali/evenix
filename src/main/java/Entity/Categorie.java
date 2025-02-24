package Entity;

public class Categorie {
    private int idCategorie;
    private String service;

    // Constructeur par défaut
    public Categorie() {}

    // Constructeur avec paramètres
    public Categorie(int idCategorie, String service) {
        this.idCategorie = idCategorie;
        this.service = service;
    }

    public Categorie(String service) {
        this.service = service;
    }

    // Getters et Setters
    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "Categorie{" +
                "idCategorie=" + idCategorie +
                ", service='" + service + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
