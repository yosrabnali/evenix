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

           Parent root = FXMLLoader.load(getClass().getResource("/Main/UserMainLayout.fxml"));


            // Vous pouvez aussi choisir une autre vue, comme EventAdmin.fxml, selon votre cas.
            //Parent root = FXMLLoader.load(getClass().getResource("/Events/EventAdmin.fxml"));

            Scene scene = new Scene(root, 1300, 700);


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
