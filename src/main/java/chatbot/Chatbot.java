package chatbot;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Chatbot {

    private Stage chatbotStage;
    private VBox chatBox;
    private TextField userInput;

    public Chatbot() {
        chatbotStage = new Stage();
        chatbotStage.initModality(Modality.APPLICATION_MODAL);
        chatbotStage.setTitle("Chatbot IA");

        // Layout principal
        VBox chatLayout = new VBox(10);
        chatLayout.setPadding(new Insets(15));
        chatLayout.setStyle("-fx-background-color: #F4F4F4;");

        // Zone de chat
        chatBox = new VBox(10);
        chatBox.setPadding(new Insets(10));
        chatBox.setStyle("-fx-background-color: white; -fx-border-color: #ccc;");

        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        // Zone de saisie utilisateur
        userInput = new TextField();
        userInput.setPromptText("Tapez votre message...");
        userInput.setStyle("-fx-font-size: 14px;");
        userInput.setPrefWidth(300);

        // Bouton d'envoi
        Button sendButton = new Button("Envoyer");
        sendButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        sendButton.setOnAction(e -> sendMessage());

        // Conteneur d'entrée
        HBox inputContainer = new HBox(10, userInput, sendButton);
        inputContainer.setAlignment(Pos.CENTER);

        chatLayout.getChildren().addAll(scrollPane, inputContainer);

        // Scene et fermeture avec ESC
        Scene chatbotScene = new Scene(chatLayout, 450, 400);
        chatbotStage.setScene(chatbotScene);
        chatbotScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                chatbotStage.close();
            }
        });
    }

    // Envoi du message et animation de réponse
    private void sendMessage() {
        String userMessage = userInput.getText().trim();
        if (!userMessage.isEmpty()) {
            Label userLabel = new Label("👤 Vous : " + userMessage);
            userLabel.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 5px; -fx-border-radius: 5px;");
            HBox userContainer = new HBox(userLabel);
            userContainer.setAlignment(Pos.BASELINE_RIGHT);
            chatBox.getChildren().add(userContainer);
            userInput.clear();

            // Animation de réponse
            Label botLabel = new Label("🤖 Chatbot : ...");
            botLabel.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 5px; -fx-border-radius: 5px;");
            HBox botContainer = new HBox(botLabel);
            botContainer.setAlignment(Pos.BASELINE_LEFT);
            chatBox.getChildren().add(botContainer);

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> {
                try {
                    String botResponse = DialogflowAPI.getDialogflowResponse(userMessage);
                    botLabel.setText("🤖 Chatbot : " + botResponse);
                } catch (Exception ex) {
                    botLabel.setText("❌ Chatbot : Une erreur est survenue.");
                    ex.printStackTrace();
                }
            });
            pause.play();
        }
    }

    // Affichage du chatbot
    public void showChatbot() {
        chatbotStage.showAndWait();
    }
}
