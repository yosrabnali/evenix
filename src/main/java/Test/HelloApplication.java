package Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Ici, on charge la vue de démarrage.
            // Par exemple, si vous voulez démarrer avec la vue client :
            boolean isAdmin = true;
            String fxmlPath = isAdmin ? "/Events/EventAdmin.fxml" : "/Main/UserMainLayout.fxml";

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            

            Scene scene = new Scene(root, 800, 600);


            primaryStage.setTitle("Mon Application d'Événements");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
