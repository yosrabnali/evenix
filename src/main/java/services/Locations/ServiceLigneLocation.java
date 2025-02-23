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
        String sql = "SELECT ll.idLigneloca, ll.idlocation, ll.idmateriel, ll.quantite, ll.montantTotal, m.nom AS nomMateriel " +
                "FROM lignelocation ll " +
                "JOIN materiel m ON ll.idmateriel = m.idmateriel " +
                "WHERE ll.idlocation = ?"; // Filter by idLocation

        try (PreparedStatement pstmt = this.cnx.prepareStatement(sql)) {
            pstmt.setInt(1, idLocation); // Set the parameter
            String finalSQL = pstmt.toString(); // Get the SQL after setting parameter
            LOGGER.info("Exécution de la requête SQL : " + finalSQL); //Log the query after setting the param

            try (ResultSet rs = pstmt.executeQuery()) {


                while (rs.next()) {
                    LigneLocation ligne = new LigneLocation(
                            rs.getInt("idLigneloca"),
                            rs.getInt("idlocation"),
                            rs.getInt("idmateriel"),
                            rs.getInt("quantite"),
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
        String sql = "SELECT ll.idLigneloca, ll.idlocation, ll.idmateriel, ll.quantite, ll.montantTotal, m.nom AS nomMateriel " +
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
                        rs.getInt("quantite"),
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
        String sql = "INSERT INTO lignelocation (idlocation, idmateriel, quantite, montantTotal) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = this.cnx.prepareStatement(sql)) {
            pstmt.setInt(1, ligne.getIdlocation());
            pstmt.setInt(2, ligne.getIdmateriel());
            pstmt.setInt(3, ligne.getQuantite());
            pstmt.setDouble(4, ligne.getMontantTotal());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {  //Vérifie si l’insertion a réussi (rowsAffected > 0).
                LOGGER.info("Ligne de location ajoutée avec succès : " + ligne);
                return true;
            } else {
                LOGGER.warning("Aucune ligne de location n'a été ajoutée.");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de l'ajout de la ligne de location : ", e);
            return false;
        }
    }

    public boolean update(LigneLocation ligne) {
        String sql = "UPDATE lignelocation SET idlocation = ?, idmateriel = ?, quantite = ?, montantTotal = ? WHERE idLigneloca = ?";

        try (PreparedStatement pstmt = this.cnx.prepareStatement(sql)) {
            pstmt.setInt(1, ligne.getIdlocation());
            pstmt.setInt(2, ligne.getIdmateriel());
            pstmt.setInt(3, ligne.getQuantite());
            pstmt.setDouble(4, ligne.getMontantTotal());
            pstmt.setInt(5, ligne.getIdLigneloca());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) { //Met à jour une ligne existante via idLigneloca.
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
        String sql = "DELETE FROM lignelocation WHERE idLigneloca = ?";

        try (PreparedStatement pstmt = this.cnx.prepareStatement(sql)) {
            pstmt.setInt(1, idLigneloca);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Ligne de location supprimée avec succès. ID : " + idLigneloca);
                return true;
            } else {
                LOGGER.warning("Aucune ligne de location n'a été supprimée.");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la suppression de la ligne de location : ", e);
            return false;
        }
    }
}