package services;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import Util.MyDB;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ExcelExporter {

    private final Connection connection;

    public ExcelExporter() {
        this.connection = MyDB.getInstance().getConnection();
    }

    public void exportToExcel(Stage stage) {
        // 🔹 1. Choisir l'emplacement du fichier Excel
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer sous");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("Materiels.xlsx");

        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            System.out.println("⚠ Annulation de l'export.");
            return;
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Matériels");
            Row headerRow = sheet.createRow(0);

            // 🔹 2. Définir les en-têtes de colonnes (sans ID, avec Service)
            String[] columns = {"Nom", "Description", "Prix", "Image", "Quantité", "Service"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // 🔹 3. Récupérer les matériels depuis la base de données (avec le nom du service au lieu de idcategorie)
            String query = """
                SELECT m.nom, m.description, m.prix, m.image, m.quantite, c.service 
                FROM materiel m 
                JOIN categorie c ON m.idcategorie = c.idcategorie
            """;

            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                int rowNum = 1;
                while (rs.next()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(rs.getString("nom"));
                    row.createCell(1).setCellValue(rs.getString("description"));
                    row.createCell(2).setCellValue(rs.getDouble("prix"));
                    row.createCell(3).setCellValue(rs.getString("image"));
                    row.createCell(4).setCellValue(rs.getInt("quantite"));
                    row.createCell(5).setCellValue(rs.getString("service")); // ✅ Remplace idcategorie par le service
                }
            }

            // 🔹 4. Sauvegarder le fichier Excel
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                System.out.println("✅ Fichier Excel exporté avec succès : " + file.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
