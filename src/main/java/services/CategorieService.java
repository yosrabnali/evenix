package services;

import entities.Categorie;
import util.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieService implements IService<Categorie> {
    private Connection cnx;

    public CategorieService() {
        cnx = MyDB.getInstance().getConnection();
    }

    private boolean categorieExiste(String service) {
        try {
            String req = "SELECT COUNT(*) FROM categorie WHERE service = ?";
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, service);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la vérification de la catégorie : " + ex.getMessage());
        }
        return false;
    }

    @Override
    public void Ajouter(Categorie c) {
        if (categorieExiste(c.getService())) {
            System.out.println("⚠️ La catégorie '" + c.getService() + "' existe déjà ! Ajout annulé.");
            return;
        }
        try {
            String req = "INSERT INTO categorie (service) VALUES (?)";
            PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, c.getService());
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                c.setIdCategorie(generatedKeys.getInt(1));
            }
            System.out.println("✅ Catégorie ajoutée avec succès ! ID: " + c.getIdCategorie());
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de l'ajout de la catégorie : " + ex.getMessage());
        }
    }

    @Override
    public void Modifier(Categorie c) {
        try {
            String req = "UPDATE categorie SET service=? WHERE idcategorie=?";
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, c.getService());
            ps.setInt(2, c.getIdCategorie());
            ps.executeUpdate();
            System.out.println("✅ Catégorie modifiée avec succès !");
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la modification de la catégorie : " + ex.getMessage());
        }
    }

    @Override
    public boolean Supprimer(int id) {
        try {
            String req = "DELETE FROM categorie WHERE idcategorie=?";
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            int rowsDeleted = ps.executeUpdate(); // Vérifier combien de lignes ont été supprimées

            if (rowsDeleted > 0) {
                System.out.println("✅ Catégorie supprimée avec succès !");
                return true; // Succès
            } else {
                System.out.println("⚠️ Aucune catégorie trouvée avec cet ID.");
                return false; // Aucun enregistrement supprimé
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la suppression de la catégorie : " + ex.getMessage());
            return false; // Erreur SQL
        }
    }


    @Override
    public List<Categorie> Recuperer() {
        List<Categorie> categories = new ArrayList<>();
        try {
            String req = "SELECT idcategorie, service FROM categorie";
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Categorie c = new Categorie(
                        rs.getInt("idcategorie"),
                        rs.getString("service")
                );
                categories.add(c);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la récupération des catégories : " + ex.getMessage());
        }
        return categories;
    }
    // Méthode pour récupérer l'ID d'une catégorie en fonction de son nom
    public int getIdCategorieByName(String nomCategorie) {
        int idCategorie = -1; // Valeur par défaut si la catégorie n'existe pas
        String query = "SELECT idcategorie FROM categorie WHERE service = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, nomCategorie);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                idCategorie = rs.getInt("idcategorie");
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération de l'ID de la catégorie : " + ex.getMessage());
        }

        return idCategorie;
    }
    /**
     * Récupérer le nom d'une catégorie en fonction de son ID.
     */
    public String getNomCategorieById(int idCategorie) {
        String nomCategorie = null;
        String query = "SELECT service FROM categorie WHERE idcategorie = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, idCategorie);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    nomCategorie = rs.getString("service");
                }
            }
        } catch (SQLException ex) {
            System.out.println("❌ Erreur lors de la récupération du nom de la catégorie : " + ex.getMessage());
        }
        return nomCategorie;
    }






}
