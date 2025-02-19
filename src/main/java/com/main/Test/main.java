package com.main.Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application {
    @Override
    public void start(Stage stage) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Navigation.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            String cssPath = "/navigation.css";
            if (getClass().getResource(cssPath) != null) {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            } else {
                System.out.println("Attention : Le fichier CSS n'a pas été trouvé : " + cssPath);
            }
            
            stage.setTitle("Publications!");
            stage.setScene(scene);
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.show();
        } catch (Exception e) {
            System.out.println("Erreur lors du démarrage de l'application : ");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            System.out.println("Erreur fatale lors du lancement : ");
            e.printStackTrace();
        }
    }

}