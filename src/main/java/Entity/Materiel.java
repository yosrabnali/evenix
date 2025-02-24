package Entity;

public class Materiel {
    private int idMateriel;
    private String nom;
    private String description;
    private double prix;
    private String image;
    private int quantite;
    private int idCategorie;
    private int idUser;

    // Constructeur par défaut
    public Materiel() {}

    // Constructeur avec paramètres
    public Materiel(int idMateriel, String nom, String description, double prix, String image, int quantite, int idCategorie, int idUser) {
        this.idMateriel = idMateriel;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.image = image;
        this.quantite = quantite;
        this.idCategorie = idCategorie;
        this.idUser = idUser;
    }

    public Materiel(int idMateriel, String nom, String description, double prix, String image, int quantite, int idCategorie) {
        this.idMateriel = idMateriel;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.image = image;
        this.quantite = quantite;
        this.idCategorie = idCategorie;
    }

    public Materiel(String nom, String description, double prix, String image, int quantite, int idCategorie, int idUser) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.image = image;
        this.quantite = quantite;
        this.idCategorie = idCategorie;
        this.idUser = idUser;
    }

    public Materiel(String nom, String description, double prix, String image, int quantite, int idCategorie) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.image = image;
        this.quantite = quantite;
        this.idCategorie = idCategorie;
    }

    // Getters et Setters
    public int getIdMateriel() {
        return idMateriel;
    }

    public void setIdMateriel(int idMateriel) {
        this.idMateriel = idMateriel;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        return "Materiel{" +
                "idMateriel=" + idMateriel +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", image='" + image + '\'' +
                ", quantite=" + quantite +
                ", idCategorie=" + idCategorie +
                ", idUser=" + idUser +
                '}';
    }
}
