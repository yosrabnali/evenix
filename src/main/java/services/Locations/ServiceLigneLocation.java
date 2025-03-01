package services.Locations;

import Entity.Locations.LigneLocation;
import Util.MyDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceLigneLocation {
    private Connection cnx;//Gère la connexion à la base de données
    //→ Permet de journaliser les messages d'information et les erreurs.
    private static final Logger LOGGER = Logger.getLogger(ServiceLigneLocation.class.getName());

    public ServiceLigneLocation() {
        MyDB.getInstance();
        this.cnx = MyDB.getConnection();
        if (this.cnx == null) {
            LOGGER.severe("La connexion à la base de données est null.");
        }
    }

    //getAllByLocationId(int idLocation)
    //Récupère toutes les lignes de location pour un idlocation donné.
    //Sélectionne les champs nécessaires de lignelocation et joint la table materiel pour récupérer nomMateriel.
    //Utilise un PreparedStatement pour éviter les injections SQL.
    public List<LigneLocation> getAllByLocationId(int idLocation) {
        List<LigneLocation> lignes = new ArrayList<>();
        String sql = "SELECT ll.idLigneloca, ll.idlocation, ll.idmateriel, " +
                "m.quantite AS quantiteMateriel, " +
                "m.prix * m.quantite AS montantTotal, " + // prix * quantite (from materiel)
                "m.nom AS nomMateriel " +
                "FROM lignelocation ll " +
                "JOIN materiel m ON ll.idmateriel = m.idmateriel " +
                "WHERE ll.idlocation = ?";

        try (PreparedStatement pstmt = this.cnx.prepareStatement(sql)) {
            pstmt.setInt(1, idLocation);
            String finalSQL = pstmt.toString();
            LOGGER.info("Exécution de la requête SQL : " + finalSQL);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LigneLocation ligne = new LigneLocation(
                            rs.getInt("idLigneloca"),
                            rs.getInt("idlocation"),
                            rs.getInt("idmateriel"),
                            rs.getInt("quantiteMateriel"),
                            rs.getDouble("montantTotal")
                    );

                    ligne.setNomMateriel(rs.getString("nomMateriel"));
                    lignes.add(ligne);

                    LOGGER.info("Ligne de location récupérée : " + ligne);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la récupération des lignes de location : ", e);
        }

        return lignes;
    }

    //Exécute la requête et crée une liste d'objets LigneLocation.
    public List<LigneLocation> getAll() {
        List<LigneLocation> lignes = new ArrayList<>();
        String sql = "SELECT ll.idLigneloca, ll.idlocation, ll.idmateriel, " +
                "m.quantite AS quantiteMateriel, " +
                "m.prix * m.quantite AS montantTotal, " + // prix * quantite (from materiel)
                "m.nom AS nomMateriel " +
                "FROM lignelocation ll " +
                "JOIN materiel m ON ll.idmateriel = m.idmateriel";

        try (PreparedStatement pstmt = this.cnx.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            LOGGER.info("Exécution de la requête SQL : " + sql);

            while (rs.next()) {
                LigneLocation ligne = new LigneLocation(
                        rs.getInt("idLigneloca"),
                        rs.getInt("idlocation"),
                        rs.getInt("idmateriel"),
                        rs.getInt("quantiteMateriel"),
                        rs.getDouble("montantTotal")
                );

                ligne.setNomMateriel(rs.getString("nomMateriel"));
                lignes.add(ligne);

                LOGGER.info("Ligne de location récupérée : " + ligne);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la récupération des lignes de location : ", e);
        }

        return lignes;
    }

    public boolean add(LigneLocation ligne) {
        String sqlCheckQuantity = "SELECT quantite, prix FROM materiel WHERE idmateriel = ?"; // Get prix and existing quantite
        String sqlInsert = "INSERT INTO lignelocation (idlocation, idmateriel, quantite, montantTotal) VALUES (?, ?, ?, ?)";
        String sqlUpdateQuantity = "UPDATE materiel SET quantite = quantite - ? WHERE idmateriel = ?";

        try (PreparedStatement pstmtCheck = this.cnx.prepareStatement(sqlCheckQuantity);
             PreparedStatement pstmtInsert = this.cnx.prepareStatement(sqlInsert);
             PreparedStatement pstmtUpdate = this.cnx.prepareStatement(sqlUpdateQuantity)) {

            pstmtCheck.setInt(1, ligne.getIdmateriel());
            ResultSet rs = pstmtCheck.executeQuery();

            if (rs.next()) {
                int availableQuantity = rs.getInt("quantite");
                double prix = rs.getDouble("prix");

                if (availableQuantity < ligne.getQuantite()) {  // check the LigneLocation `quantite`, not the material
                    LOGGER.warning("Quantité insuffisante de matériel. Disponible : " + availableQuantity + ", Demandé : " + ligne.getQuantite());
                    return false;
                }

                double montantTotal = prix * ligne.getQuantite(); //prix in Material *   ligne.getQuantite(); quantity in this LigneLocation.
                pstmtInsert.setInt(1, ligne.getIdlocation());
                pstmtInsert.setInt(2, ligne.getIdmateriel());
                pstmtInsert.setInt(3, ligne.getQuantite()); // the LigneLocation quantity
                pstmtInsert.setDouble(4, montantTotal);

                int rowsAffectedInsert = pstmtInsert.executeUpdate();

                if (rowsAffectedInsert > 0) {
                    pstmtUpdate.setInt(1, ligne.getQuantite());
                    pstmtUpdate.setInt(2, ligne.getIdmateriel());
                    int rowsAffectedUpdate = pstmtUpdate.executeUpdate();

                    if (rowsAffectedUpdate > 0) {
                        LOGGER.info("Ligne de location ajoutée avec succès et quantité de matériel mise à jour : " + ligne);
                        return true;
                    } else {
                        LOGGER.warning("La ligne de location a été ajoutée, mais la quantité de matériel n'a pas été mise à jour.");
                        return false;
                    }
                } else {
                    LOGGER.warning("Aucune ligne de location n'a été ajoutée.");
                    return false;
                }
            } else {
                LOGGER.warning("Matériel non trouvé avec l'ID : " + ligne.getIdmateriel());
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de l'ajout de la ligne de location ou de la mise à jour de la quantité de matériel : ", e);
            return false;
        }
    }

    public boolean update(LigneLocation ligne) {
        String sqlCheckInitialQuantity = "SELECT quantite FROM lignelocation WHERE idLigneloca = ?";
        String sqlCheckMaterialQuantity = "SELECT quantite, prix FROM materiel WHERE idmateriel = ?";
        String sqlUpdateQuantity = "UPDATE materiel SET quantite = quantite + ? - ? WHERE idmateriel = ?"; //Adds initial - new quant

        String sql = "UPDATE lignelocation SET idlocation = ?, idmateriel = ?, quantite = ?, montantTotal = ? WHERE idLigneloca = ?";

        try (PreparedStatement pstmtCheckInit = this.cnx.prepareStatement(sqlCheckInitialQuantity);
             PreparedStatement pstmtCheckMat = this.cnx.prepareStatement(sqlCheckMaterialQuantity);
             PreparedStatement pstmtUpdateMat = this.cnx.prepareStatement(sqlUpdateQuantity);
             PreparedStatement pstmt = this.cnx.prepareStatement(sql)) {

            pstmtCheckInit.setInt(1, ligne.getIdLigneloca());
            ResultSet rsInit = pstmtCheckInit.executeQuery();

            if(!rsInit.next()){
                LOGGER.warning("LigneLocation not found with ID: " + ligne.getIdLigneloca());
                return false;
            }
            int initialQuantity = rsInit.getInt("quantite");

            pstmtCheckMat.setInt(1, ligne.getIdmateriel());
            ResultSet rsMat = pstmtCheckMat.executeQuery();

            if(!rsMat.next()){
                LOGGER.warning("Materiel not found with ID: " + ligne.getIdmateriel());
                return false;
            }

            int availableQuantity = rsMat.getInt("quantite");
            double prix = rsMat.getDouble("prix"); // Retrieve the price from the 'materiel' table
            int quantityChange = initialQuantity-ligne.getQuantite();

            if (availableQuantity < -quantityChange) {
                LOGGER.warning("Not enough material to update. Available: " + availableQuantity + ", Required change: " + -quantityChange);
                return false;
            }
            pstmtUpdateMat.setInt(1, initialQuantity);
            pstmtUpdateMat.setInt(2, ligne.getQuantite());
            pstmtUpdateMat.setInt(3, ligne.getIdmateriel());
            pstmtUpdateMat.executeUpdate();

            double montantTotal = prix * ligne.getQuantite(); //prix from Material *   ligne.getQuantite(); quantity in this LigneLocation.
            pstmt.setInt(1, ligne.getIdlocation());
            pstmt.setInt(2, ligne.getIdmateriel());
            pstmt.setInt(3, ligne.getQuantite()); // the LigneLocation quantity, the right one
            pstmt.setDouble(4, montantTotal);
            pstmt.setInt(5, ligne.getIdLigneloca());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Ligne de location mise à jour avec succès : " + ligne);
                return true;
            } else {
                LOGGER.warning("Aucune ligne de location n'a été mise à jour.");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la mise à jour de la ligne de location : ", e);
            return false;
        }
    }

    public boolean delete(int idLigneloca) {
        String sqlGetQuantity = "SELECT idmateriel, quantite FROM lignelocation WHERE idLigneloca = ?";
        String sqlUpdateQuantity = "UPDATE materiel SET quantite = quantite + ? WHERE idmateriel = ?";
        String sqlDelete = "DELETE FROM lignelocation WHERE idLigneloca = ?";

        try (PreparedStatement pstmtGet = this.cnx.prepareStatement(sqlGetQuantity);
             PreparedStatement pstmtUpdate = this.cnx.prepareStatement(sqlUpdateQuantity);
             PreparedStatement pstmtDelete = this.cnx.prepareStatement(sqlDelete)) {

            pstmtGet.setInt(1, idLigneloca);
            ResultSet rs = pstmtGet.executeQuery();

            if (rs.next()) {
                int idmateriel = rs.getInt("idmateriel");
                int quantite = rs.getInt("quantite");

                // Update material quantity first
                pstmtUpdate.setInt(1, quantite);
                pstmtUpdate.setInt(2, idmateriel);
                pstmtUpdate.executeUpdate();

                // Then delete the LigneLocation
                pstmtDelete.setInt(1, idLigneloca);
                int rowsAffected = pstmtDelete.executeUpdate();

                if (rowsAffected > 0) {
                    LOGGER.info("Ligne de location supprimée avec succès. ID : " + idLigneloca);
                    return true;
                } else {
                    LOGGER.warning("Aucune ligne de location n'a été supprimée.");
                    return false;
                }
            } else {
                LOGGER.warning("Ligne de location non trouvée avec l'ID : " + idLigneloca);
                return false;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la suppression de la ligne de location : ", e);
            return false;
        }
    }
}