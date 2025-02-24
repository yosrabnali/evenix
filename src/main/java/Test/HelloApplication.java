package Test;

import controllers.MaterialController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // ✅ Initialiser d'abord le FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/materiel/materiel-view.fxml"));
            Parent root = loader.load(); // Charger le fichier FXML

            // ✅ Récupérer le contrôleur après avoir chargé le fichier
            MaterialController materialController = loader.getController();
            materialController.setUser(1); // Passer l'ID utilisateur connecté
            // ✅ Afficher la fenêtre principale
            primaryStage.setTitle("Material Management");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors du chargement de l'interface !");
        }
    }

    public static void HelloApplication(String[] args) {
        launch(args);
    }
}
