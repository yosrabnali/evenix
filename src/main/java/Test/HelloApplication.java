package Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloApplication extends Application {

    private static final Logger logger = Logger.getLogger(HelloApplication.class.getName());

    @Override
    public void start(Stage primaryStage) {
        try {
            // Debug: Print the URL
            URL location = getClass().getResource("/menu.fxml"); //Corrected Path!

            if (location == null) {
                System.err.println("FXML file not found at /menu.fxml");
                return;
            }

            System.out.println("FXML location: " + location.toString()); // Print URL for debugging

            FXMLLoader loader = new FXMLLoader(location);
            Parent root = loader.load(); // Load the FXML layout
            Scene scene = new Scene(root);

            primaryStage.setTitle("EVEINX App"); // Set the window title
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load FXML", e);
            // Optionally, show an alert dialog to the user to indicate an error
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An unexpected error occurred", e);
            //Handle Unexpected Errors (e.g., print stack trace, show an alert)
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
