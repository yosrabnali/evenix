package services;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Util.MyDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PDFExporter {

    private final Connection connection;

    public PDFExporter() {
        this.connection = MyDB.getInstance().getConnection();
    }

    public void exportToPDF(Stage stage) {
        // 🔹 1. Choisir l'emplacement du fichier PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer sous");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Materiels.pdf");

        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            System.out.println("⚠ Annulation de l'export.");
            return;
        }

        try {
            // 🔹 2. Création du document PDF en format A4
            PdfWriter writer = new PdfWriter(new FileOutputStream(file));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);

            // 🔹 3. Ajout d'un titre bien visible
            document.add(new Paragraph("📄 Liste des Matériels")
                    .setBold()
                    .setFontSize(18)
                    .setMarginBottom(10));

            // 🔹 4. Définition du tableau avec colonnes optimisées
            float[] columnWidths = {80f, 150f, 60f, 100f, 50f, 100f}; // ✅ Colonnes équilibrées
            Table table = new Table(columnWidths);
            table.setAutoLayout(); // ✅ Ajuste automatiquement la largeur
            table.setWidth(UnitValue.createPercentValue(100)); // ✅ S'adapte à la largeur de la page

            // 🔹 5. Ajouter les en-têtes du tableau
            table.addCell(new Cell().add(new Paragraph("Nom").setBold()));
            table.addCell(new Cell().add(new Paragraph("Description").setBold()));
            table.addCell(new Cell().add(new Paragraph("Prix").setBold()));
            table.addCell(new Cell().add(new Paragraph("Image").setBold()));
            table.addCell(new Cell().add(new Paragraph("Quantité").setBold()));
            table.addCell(new Cell().add(new Paragraph("Service").setBold()));

            // 🔹 6. Récupérer les données depuis la base de données
            String query = """
                SELECT m.nom, m.description, m.prix, m.image, m.quantite, c.service 
                FROM materiel m 
                JOIN categorie c ON m.idcategorie = c.idcategorie
            """;

            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    // 🔹 Ajout des données avec taille de texte optimisée
                    table.addCell(new Cell().add(new Paragraph(rs.getString("nom")).setFontSize(10).setWidth(80)));
                    table.addCell(new Cell().add(new Paragraph(rs.getString("description")).setFontSize(10).setWidth(150)));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(rs.getDouble("prix"))).setFontSize(10).setWidth(60)));
                    table.addCell(new Cell().add(new Paragraph(rs.getString("image")).setFontSize(10).setWidth(100)));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(rs.getInt("quantite"))).setFontSize(10).setWidth(50)));
                    table.addCell(new Cell().add(new Paragraph(rs.getString("service")).setFontSize(10).setWidth(100)));
                }
            }

            // 🔹 7. Ajouter le tableau au document et fermer
            document.add(table);
            document.close();
            System.out.println("✅ Fichier PDF exporté avec succès : " + file.getAbsolutePath());

        } catch (FileNotFoundException e) {
            System.err.println("❌ Erreur : Impossible de créer le fichier PDF.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
