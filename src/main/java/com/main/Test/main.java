package com.main.Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.main.controllers.PostController;


public class main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Publication/Post-view.fxml"));
            //Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            Parent root = loader.load(); // Charger le fichier FXML

            // ✅ Récupérer le contrôleur après avoir chargé le fichier
           PostController materialController = loader.getController();
            //PostController.setUser(1); // Passer l'ID utilisateur connecté
            // ✅ Afficher la fenêtre principale
            primaryStage.setTitle("Post Management");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors du démarrage de l'application : ");
        }
    }

    public static void main(String[] args) {launch(args);}

}