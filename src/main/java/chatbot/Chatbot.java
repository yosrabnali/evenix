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
        chatBox.setPrefWidth(400);

        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Ajout d'une barre de défilement horizontale

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
            addMessage(userMessage, true);
            userInput.clear();

            // Animation de réponse avec "..."
            Label botLabel = createMessageLabel("🤖 Chatbot : ...", false);
            chatBox.getChildren().add(botLabel);

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

    // Ajout des messages avec prise en charge des longs textes
    private void addMessage(String message, boolean isUser) {
        Label messageLabel = createMessageLabel((isUser ? "👤 Vous : " : "🤖 Chatbot : ") + message, isUser);

        ScrollPane messageScrollPane = new ScrollPane(messageLabel);
        messageScrollPane.setFitToWidth(true);
        messageScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Ajoute le défilement horizontal si nécessaire

        HBox messageContainer = new HBox(messageScrollPane);
        messageContainer.setAlignment(isUser ? Pos.BASELINE_RIGHT : Pos.BASELINE_LEFT);

        chatBox.getChildren().add(messageContainer);
    }

    // Création des labels de messages
    private Label createMessageLabel(String text, boolean isUser) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(300); // Permet un affichage optimal du texte
        label.setPadding(new Insets(5));
        label.setStyle("-fx-background-color: " + (isUser ? "#DCF8C6" : "#E0E0E0") + "; -fx-border-radius: 5px; -fx-padding: 5px;");
        return label;
    }

    // Affichage du chatbot
    public void showChatbot() {
        chatbotStage.showAndWait();
    }
}
