package services;

import entities.User;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class ExcelExporter {

    private Connection connection = null;

    public ExcelExporter() {
        this.connection = connection;
    }

    public boolean exportToExcel(List<User> users, File file) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Utilisateurs");
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Nom", "Prénom", "Email", "Téléphone", "Rôle"};

            // Créer les en-têtes de colonnes
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Remplir les données
            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getNom());
                row.createCell(1).setCellValue(user.getPrenom());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(user.getTelephone());
                row.createCell(4).setCellValue(user.getRole().getRoleName());
            }

            // Auto-ajuster les colonnes
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Sauvegarder le fichier
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur d'export", "Erreur lors de l'exportation : " + e.getMessage());
            return false;
        }
    }

    public void exportUsersToExcel(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer sous");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("Utilisateurs.xlsx");

        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            showAlert(Alert.AlertType.WARNING, "Export annulé", "L'exportation a été annulée par l'utilisateur.");
            return;
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Utilisateurs");
            Row headerRow = sheet.createRow(0);

            // En-têtes de colonnes
            String[] columns = {"Nom", "Prénom", "Email", "Téléphone", "Rôle"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Récupérer les utilisateurs depuis la base de données
            String query = "SELECT nom, prenom, email, telephone, role FROM utilisateur";
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                int rowNum = 1;
                while (rs.next()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(rs.getString("nom"));
                    row.createCell(1).setCellValue(rs.getString("prenom"));
                    row.createCell(2).setCellValue(rs.getString("email"));
                    row.createCell(3).setCellValue(rs.getString("telephone"));
                    row.createCell(4).setCellValue(rs.getString("role"));
                }
            }

            // Auto-ajuster les colonnes
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Sauvegarder le fichier
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                showAlert(Alert.AlertType.INFORMATION, "Export réussi", "Le fichier Excel a été exporté avec succès : " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur d'export", "Une erreur est survenue lors de l'exportation : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}