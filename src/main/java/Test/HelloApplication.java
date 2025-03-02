package Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Vérification du chemin FXML
            URL fxmlLocation = getClass().getResource("/Main/UserMainLayout.fxml");
            if (fxmlLocation == null) {
                throw new RuntimeException("Fichier FXML introuvable : Vérifiez le chemin et l'emplacement du fichier.");
            }

            Parent root = FXMLLoader.load(fxmlLocation);
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("Mon Application des réclamations");
            primaryStage.setScene(scene);
            primaryStage.show();

            System.out.println("Application lancée avec succès !");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'application :");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
