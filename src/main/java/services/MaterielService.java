package services;


import Entity.Materiel;
import Util.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterielService implements IService<Materiel> {
    private Connection cnx;

    public MaterielService() {
        cnx = MyDB.getInstance().getConnection();
    }

    @Override
    public void Ajouter(Materiel m) {
        try {
            String req = "INSERT INTO materiel (nom, description, prix, image, quantite, idcategorie, iduser) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);

            System.out.println("📸 Image reçue avant insertion : " + m.getImage());

            ps.setString(1, m.getNom());
            ps.setString(2, m.getDescription());
            ps.setDouble(3, m.getPrix());

            if (m.getImage() == null || m.getImage().isEmpty()) {
                System.out.println("❌ Aucune image reçue ! Utilisation d'une image par défaut.");
                ps.setString(4, "src/images/default.png"); // Image par défaut
            } else {
                ps.setString(4, m.getImage());
            }

            ps.setInt(5, m.getQuantite());
            ps.setInt(6, m.getIdCategorie());
            ps.setInt(7, m.getIdUser());

            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                m.setIdMateriel(generatedKeys.getInt(1));
            }
            System.out.println("✅ Matériel ajouté avec succès ! ID: " + m.getIdMateriel());

            // 🚀 Envoi du SMS après ajout
            String numeroDestinataire = "+21626654742"; // Remplace par le numéro de l'utilisateur ou un admin
            String message = "✅ Nouveau matériel ajouté : " + m.getNom() + " (Quantité: " + m.getQuantite() + ")";

            SmsService.sendSms(numeroDestinataire, message);
            System.out.println("📩 SMS envoyé avec succès !");

        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de l'ajout du matériel : " + ex.getMessage());
        }
    }


    @Override
    public void Modifier(Materiel m) {
        try {
            String req = "UPDATE materiel SET nom=?, description=?, prix=?, image=?, quantite=?, idcategorie=?, iduser=? WHERE idmateriel=?";
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, m.getNom());
            ps.setString(2, m.getDescription());
            ps.setDouble(3, m.getPrix());
            ps.setString(4, m.getImage());
            ps.setInt(5, m.getQuantite());
            ps.setInt(6, m.getIdCategorie());
            ps.setInt(7, m.getIdUser());
            ps.setInt(8, m.getIdMateriel());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Matériel modifié avec succès !");
            } else {
                System.out.println("⚠️ Aucune modification effectuée, vérifiez l'ID du matériel.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la modification du matériel : " + ex.getMessage());
        }
    }

    @Override
    public boolean Supprimer(int id) {
        try {
            // Vérification et réouverture de la connexion si nécessaire
            if (cnx == null || cnx.isClosed()) {
                System.out.println("🔄 Réouverture de la connexion pour suppression...");
                cnx = MyDB.getInstance().getConnection();
            }

            String req = "DELETE FROM materiel WHERE idmateriel=?";
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            int rowsDeleted = ps.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("✅ Matériel supprimé avec succès !");
                return true;
            } else {
                System.out.println("⚠️ Aucun matériel trouvé avec cet ID.");
                return false;
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la suppression du matériel : " + ex.getMessage());
            return false;
        }
    }



    @Override
    public List<Materiel> Recuperer() {
        List<Materiel> materiels = new ArrayList<>();

        // Vérifier et rétablir la connexion si nécessaire
        if (!MyDB.getInstance().isConnected()) {
            System.out.println("🔄 Réouverture de la connexion à la base de données...");
            MyDB.getInstance().getConnection();
        }

        String req = "SELECT * FROM materiel";

        try (Connection conn = MyDB.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                materiels.add(new Materiel(
                        rs.getInt("idmateriel"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getString("image"),
                        rs.getInt("quantite"),
                        rs.getInt("idcategorie"),
                        rs.getInt("iduser")
                ));
            }

        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la récupération des matériels : " + ex.getMessage());
        }

        return materiels;
    }

    public List<Materiel> searchByName(String searchText) {
        List<Materiel> materiels = new ArrayList<>();

        // Vérifier et rétablir la connexion si nécessaire
        if (!MyDB.getInstance().isConnected()) {
            System.out.println("🔄 Réouverture de la connexion à la base de données...");
            MyDB.getInstance().getConnection();
        }

        String query = "SELECT * FROM materiel WHERE LOWER(nom) LIKE ?";

        try (Connection conn = MyDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + searchText.toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                materiels.add(new Materiel(
                        rs.getInt("idmateriel"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getString("image"),
                        rs.getInt("quantite"),
                        rs.getInt("idcategorie"),
                        rs.getInt("iduser")
                ));
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la recherche du matériel : " + e.getMessage());
        }

        return materiels;
    }
    public String getUserRoleFromDB(int userId) {
        String role = "";
        String query = "SELECT role FROM utilisateur WHERE iduser = ?"; // ⚠️ Adapter le nom de la table si nécessaire

        try (Connection cnx = MyDB.getInstance().getConnection();
             PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                role = rs.getString("role"); // Récupérer le rôle de l'utilisateur
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la récupération du rôle : " + ex.getMessage());
        }

        return role;
    }
    /** ✅ Récupérer le nom de la catégorie à partir de son ID */
    public String getCategoryName(int categoryId) {
        String categoryName = "";
        String query = "SELECT service FROM categorie WHERE idcategorie = ?";

        try (Connection conn = MyDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                categoryName = rs.getString("service"); // Récupération du nom de la catégorie
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération du nom de la catégorie : " + e.getMessage());
        }

        return categoryName;
    }

    public void ajouterLivraison(int idMateriel, int qte) {
        try {
            Connection connection = null;
            if (connection == null || connection.isClosed()) {
                connection = MyDB.getInstance().getConnection(); // Récupérer la connexion si elle est fermée
            }

            String query = "INSERT INTO livraison (reponselivraison, qte, idmateriel) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, "Validée");
            stmt.setInt(2, qte);
            stmt.setInt(3, idMateriel);

            stmt.executeUpdate();
            stmt.close();

            System.out.println("✅ Livraison ajoutée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void diminuerQuantiteMateriel(int idMateriel, int qte) {
        try {
            Connection connection = null;
            if (connection == null || connection.isClosed()) {
                connection = MyDB.getInstance().getConnection(); // Récupérer la connexion si elle est fermée
            }
            String sql = "UPDATE materiel SET quantite = quantite - ? WHERE idmateriel = ?";
            try {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, qte);
                ps.setInt(2, idMateriel);
                ps.executeUpdate();
                System.out.println("✅ Quantité mise à jour avec succès !");
            } catch (SQLException e) {
                System.out.println("❌ Erreur lors de la mise à jour de la quantité : " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}











