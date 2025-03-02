package services.ReclamationsServices;

import Entity.Reclamations.Reclamation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceReport {

    private static final Logger logger = Logger.getLogger(ServiceReport.class.getName()); // Déclaration du logger
    private final ServiceReclamation serviceReclamation = new ServiceReclamation();

    public void generatePDFReport(String filePath) throws SQLException, DocumentException, IOException {
        List<Reclamation> reclamations = serviceReclamation.afficherReclamations();
        Document document = new Document();
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            PdfWriter.getInstance(document, fos);
            document.open();
            for (Reclamation reclamation : reclamations) {
                document.add(new Paragraph("ID: " + reclamation.getIdreclamation()));
                document.add(new Paragraph("Titre: " + reclamation.getTitre()));
                document.add(new Paragraph("Description: " + reclamation.getDescription()));
                document.add(new Paragraph("Date: " + reclamation.getDate()));
                document.add(new Paragraph("--------------------------------------"));
            }
            document.close();
            logger.log(Level.INFO, "Fichier PDF généré avec succès : " + filePath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la génération du PDF", e);
            throw e; // Relancer l'exception pour la gestion dans le contrôleur
        }
    }

    public void generateExcelReport(String filePath) throws SQLException, IOException {
        List<Reclamation> reclamations = serviceReclamation.afficherReclamations();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reclamations");
            int rowNum = 0;

            // Créer une ligne d'en-tête
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Titre");
            headerRow.createCell(2).setCellValue("Description");
            headerRow.createCell(3).setCellValue("Date");

            // Remplir les données
            for (Reclamation reclamation : reclamations) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(reclamation.getIdreclamation());
                row.createCell(1).setCellValue(reclamation.getTitre());
                row.createCell(2).setCellValue(reclamation.getDescription());
                row.createCell(3).setCellValue(reclamation.getDate().toString());
            }

            // Écrire le fichier Excel
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
            logger.log(Level.INFO, "Excel file successfully generated: " + filePath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while generating the Excel file", e);
            throw e;
        }
    }
}