package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Vérification des ressources avant de les utiliser

            // Charger le fichier FXML du SignUp
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/fxml/Dashboard.fxml"));
            Pane root = loader.load();

            // Créer la scène
            Scene scene = new Scene(root);
            // Ajouter un style CSS (optionnel)
            scene.getStylesheets().add(getClass().getResource("/css/style1.css").toExternalForm());

            // Configurer la fenêtre principale
            primaryStage.setTitle("Inscription - Evenix");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);

            // Ajouter une icône (optionnel)
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));

            // Afficher la fenêtre
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du fichier SignUp.fxml");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
