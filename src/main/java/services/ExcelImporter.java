package services;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Entity.Materiel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import Util.MyDB;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

public class ExcelImporter {

    private final Connection connection;
    private final CategorieService categorieService;

    public ExcelImporter() {
        this.connection = MyDB.getInstance().getConnection();
        this.categorieService = new CategorieService(); // ✅ Service pour récupérer idcategorie
    }

    public void importFromExcel(Stage stage) {
        // 🔹 1. Sélectionner le fichier Excel
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
        File file = fileChooser.showOpenDialog(stage);

        if (file == null) {
            System.out.println("⚠ Aucun fichier sélectionné !");
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // 🔹 2. Lire le fichier ligne par ligne (ignorer la première ligne si c'est un header)
            boolean firstRow = true;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (firstRow) {
                    firstRow = false;
                    continue;
                }

                try {
                    int idMateriel = (int) getCellValueAsDouble(row.getCell(0));
                    String nom = getCellValueAsString(row.getCell(1));
                    String description = getCellValueAsString(row.getCell(2));
                    double prix = getCellValueAsDouble(row.getCell(3));
                    String image = getCellValueAsString(row.getCell(4));
                    int quantite = (int) getCellValueAsDouble(row.getCell(5));
                    String nomCategorie = getCellValueAsString(row.getCell(6)); // ✅ Nom de la catégorie

                    // 🔹 1. Récupérer idCategorie depuis le service
                    int idCategorie = categorieService.getIdCategorieByName(nomCategorie);

                    if (idCategorie == -1) {
                        System.err.println("❌ Erreur : La catégorie '" + nomCategorie + "' n'existe pas.");
                        continue;
                    }

                    // 🔹 2. Insérer le matériel dans la base (sans iduser)
                    insertMateriel(new Materiel(idMateriel, nom, description, prix, image, quantite, idCategorie));

                } catch (Exception e) {
                    System.err.println("❌ Erreur lors de l'importation d'une ligne : " + e.getMessage());
                }
            }

            System.out.println("✅ Importation Excel terminée !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertMateriel(Materiel materiel) {
        // 🔹 Nouvelle requête SANS `iduser`
        String sql = "INSERT INTO materiel (idmateriel, nom, description, prix, image, quantite, idcategorie) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, materiel.getIdMateriel());
            stmt.setString(2, materiel.getNom());
            stmt.setString(3, materiel.getDescription());
            stmt.setDouble(4, materiel.getPrix());
            stmt.setString(5, materiel.getImage());
            stmt.setInt(6, materiel.getQuantite());
            stmt.setInt(7, materiel.getIdCategorie());

            stmt.executeUpdate();
            System.out.println("✅ Matériel ajouté : " + materiel.getNom());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        try {
            return cell.getStringCellValue();
        } catch (Exception e) {
            return "";
        }
    }

    private double getCellValueAsDouble(Cell cell) {
        if (cell == null) return 0.0;
        try {
            return cell.getNumericCellValue();
        } catch (Exception e) {
            return 0.0;
        }
    }
}
