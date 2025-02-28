package controllers.Locations;

import Entity.Locations.LigneLocation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.Locations.ServiceLigneLocation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class LigneLocationController {

    private static final Logger LOGGER = Logger.getLogger(LigneLocationController.class.getName());

    @FXML private TableView<LigneLocation> ligneLocationTableView;
    @FXML private TableColumn<LigneLocation, Integer> colLigneId, colQuantite;
    @FXML private TableColumn<LigneLocation, Double> colMontantTotal;
    @FXML private TableColumn<LigneLocation, String> colNomMateriel;
    @FXML private WebView webView;  // Ajout du WebView

    private final ServiceLigneLocation serviceLigneLocation = new ServiceLigneLocation();
    private final ObservableList<LigneLocation> ligneLocationList = FXCollections.observableArrayList();

    private int idLocation;

    @FXML
    public void initialize() {
        colLigneId.setCellValueFactory(cellData -> cellData.getValue().idLignelocaProperty().asObject());
        colQuantite.setCellValueFactory(cellData -> cellData.getValue().quantiteProperty().asObject());
        colNomMateriel.setCellValueFactory(cellData -> {
            String nomMateriel = cellData.getValue().getNomMateriel();
            return new SimpleStringProperty(nomMateriel);
        });
        colMontantTotal.setCellValueFactory(cellData -> cellData.getValue().montantTotalProperty().asObject());

        // Don't load data here.
    }

    public void setIdLocation(int idLocation) {
        this.idLocation = idLocation;
        System.out.println("ID de la location reçu dans LigneLocationController : " + idLocation);
        chargerLignesLocation();
    }

    private void chargerLignesLocation() {
        ligneLocationList.clear();
        List<LigneLocation> lignes = serviceLigneLocation.getAllByLocationId(idLocation);
        ligneLocationList.addAll(lignes);
        ligneLocationTableView.setItems(ligneLocationList);

        System.out.println("Number of lignes to: "+ligneLocationList.size());
    }

    @FXML
    private void retour() {
        ligneLocationTableView.getScene().getWindow().hide();
    }

    @FXML
    private void telechargerPDF() {
        try {
            String apiUrl = "https://api.html2pdfrocket.com/pdf";  // Replace
            String apiKey = "8a2dd8ca-ff20-4c22-9f6d-91fd8ea5d544"; // Replace with your actual API key

            Map<String, String> parameters = new HashMap<>();
            parameters.put("apiKey", apiKey);
            parameters.put("value", createHtmlForPdf()); // Your HTML content
            parameters.put("margin", "10");
            parameters.put("filename", "LigneLocation.pdf");

            String formData = getFormDataString(parameters);
            System.out.println("Form Data: " + formData);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            System.out.println("Response code:" + response.statusCode());

            if (response.statusCode() == 200) {
                // Show download choice to user
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save PDF File");
                fileChooser.setInitialFileName("LigneLocation.pdf");
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
                fileChooser.getExtensionFilters().add(extFilter);

                Stage stage = new Stage();
                File file = fileChooser.showSaveDialog(stage);

                if (file != null) {
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(response.body());
                        System.out.println("PDF written to file: " + file.getAbsolutePath());
                        createAlertBox("Success", "PDF was generated");
                    } catch (IOException ioException) {
                        createAlertBox("Error","Error writing PDF");
                    }
                } else {
                    createAlertBox("Info", "Save dialogue canceled");
                }
            } else {
                createAlertBox("API Error","API returned error code"+response.statusCode());
                System.err.println("API error: " + response.statusCode());
            }
        } catch (Exception e) {
            createAlertBox("System error","Unexpected error");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getFormDataString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            result.append("&");
        }
        String string = result.toString();
        return string.length() > 0
                ? string.substring(0, string.length() - 1)
                : string;
    }

    //Create
    private void createAlertBox(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(header);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String createHtmlForPdf() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("<head><title>Lignes de Location</title>");
        sb.append("<style>\n" +
                "table {\n" +
                "    width: 100%;\n" +
                "    border-collapse: collapse;\n" +
                "}\n" +
                "th, td {\n" +
                "    border: 1px solid black;\n" +
                "    padding: 8px;\n" +
                "    text-align: left;\n" +
                "}\n" +
                "</style>");
        sb.append("</head>");
        sb.append("<body>");

        // Embed the logo as Base64
        String imagePath = "C:\\Users\\MSI\\Documents\\projectAPI6 - Copie\\projectAPI\\src\\main\\resources\\images\\logo.jpg";  // Your logo's path
        String base64Image = "";
        try {
            byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
            base64Image = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            System.err.println("Error reading image: " + e.getMessage());
            // Handle the error appropriately.  Perhaps use a default image.
            base64Image = getDefaultBase64Image(); // Use a fallback
        }

        sb.append("<img src=\"data:image/png;base64,"); // or data:image/jpeg;base64,  etc.
        sb.append(base64Image);
        sb.append("\" alt=\"Evenix Logo\" style=\"width:200px;height:150px;\">");

        sb.append("<h1>Lignes de Location</h1>");

        sb.append("<table>");
        sb.append("<thead><tr><th>ID Ligne</th><th>Nom Matériel</th><th>Quantité</th><th>Montant Total</th></tr></thead>");
        sb.append("<tbody>");
        for (LigneLocation ligne : ligneLocationList) {
            sb.append("<tr>");
            sb.append("<td>").append(ligne.getIdLigneloca()).append("</td>");
            sb.append("<td>").append(ligne.getNomMateriel()).append("</td>");
            sb.append("<td>").append(ligne.getQuantite()).append("</td>");
            sb.append("<td>").append(ligne.getMontantTotal()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }

    private String getDefaultBase64Image() {
        // A very small, simple, transparent GIF or PNG as a placeholder.
        // You can replace this with a more visually appealing default.
        return "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=";
    }
}