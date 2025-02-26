package com.main.Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger le fichier FXML et obtenir le Parent
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main/UserMainLayout.fxml"));
            Parent root = loader.load();

            // Afficher la fenêtre principale
            primaryStage.setTitle("Evenix - User Main Layout");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors du démarrage de l'application : ");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
