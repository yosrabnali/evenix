package chatbot;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class Chatbot {

    private Stage chatbotStage;

    public Chatbot() {
        chatbotStage = new Stage();
        chatbotStage.initModality(Modality.APPLICATION_MODAL);
        chatbotStage.setTitle("Chatbot IA");

        // Créer l'interface de chat
        VBox chatLayout = new VBox();
        chatLayout.setSpacing(10);
        chatLayout.setPadding(new javafx.geometry.Insets(10));

        // Zone de texte pour afficher la conversation
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        // Zone de saisie utilisateur
        TextField userInput = new TextField();
        userInput.setPromptText("Tapez votre message...");

        // Bouton d'envoi
        Button sendButton = new Button("Envoyer");
        sendButton.setOnAction(e -> {
            String userMessage = userInput.getText().trim();
            if (!userMessage.isEmpty()) {
                chatArea.appendText("Vous : " + userMessage + "\n");
                userInput.clear();

                // Appeler l'API Dialogflow pour obtenir une réponse
                try {
                    String botResponse = DialogflowAPI.getDialogflowResponse(userMessage);
                    chatArea.appendText("Chatbot : " + botResponse + "\n");
                } catch (Exception ex) {
                    chatArea.appendText("Chatbot : Désolé, une erreur est survenue.\n");
                    ex.printStackTrace(); // Debugging
                }
            }
        });

        // Ajouter les éléments à l'interface
        HBox inputContainer = new HBox(10, userInput, sendButton);
        inputContainer.setSpacing(10);
        chatLayout.getChildren().addAll(chatArea, inputContainer);

        Scene chatbotScene = new Scene(chatLayout, 400, 400);
        chatbotStage.setScene(chatbotScene);
    }

    // Méthode pour afficher la fenêtre du chatbot
    public void showChatbot() {
        chatbotStage.showAndWait();
    }
}
