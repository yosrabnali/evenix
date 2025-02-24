package chatbot;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.*;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Lists;

public class DialogflowAPI {

    private static final Logger logger = Logger.getLogger(DialogflowAPI.class.getName());

    private static final String PROJECT_ID = "evenix-451521"; // 🔥 Remplace par ton Project ID
    private static final String JSON_PATH = "C:\\Users\\victo\\OneDrive\\Documents\\google_credentials.json"; // 🔥 Mets ton chemin correct

    // 🔥 Hugging Face API
    private static final String HUGGINGFACE_API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2";
    private static final String HUGGINGFACE_API_KEY = "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";

    public static String getDialogflowResponse(String query) {
        logger.info("🔹 Début du traitement...");

        // 🔹 Chargement sécurisé des credentials
        GoogleCredentials credentials;
        try (InputStream jsonStream = new FileInputStream(JSON_PATH)) {
            credentials = GoogleCredentials.fromStream(jsonStream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            logger.info("✅ Credentials chargés avec succès");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Erreur lors du chargement des credentials", e);
            return "❌ Problème avec la connexion à Dialogflow.";
        }
        String g="";

        // 🔹 Génération d'un Session ID unique
        String sessionId = "session-" + UUID.randomUUID().toString();
        SessionName session = SessionName.of(PROJECT_ID, sessionId);
        logger.info("🔹 Session ID: " + session.getSession());

        // 🔹 Création du client Dialogflow
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            logger.info("✅ Connexion à Dialogflow réussie !");

            TextInput.Builder textInput = TextInput.newBuilder()
                    .setText(query)
                    .setLanguageCode("fr");

            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
            QueryResult queryResult = response.getQueryResult();
            String botResponse = queryResult.getFulfillmentText();

            if (botResponse == null || botResponse.isEmpty()) {
                logger.info("🤖 Dialogflow n'a pas trouvé de réponse. Envoi vers Hugging Face...");
                botResponse = callHuggingFace(query);
            }

            logger.info("✅ Réponse finale : " + botResponse);
            return botResponse;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Erreur avec Dialogflow", e);
            return "❌ Problème avec Dialogflow.";
        }
    }

    public static String callHuggingFace(String question) {
        if (HUGGINGFACE_API_KEY == null || HUGGINGFACE_API_KEY.isEmpty()) {
            logger.severe("❌ Clé API Hugging Face manquante.");
            return "😕 Désolé, je ne peux pas répondre pour le moment.";
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(HUGGINGFACE_API_URL);
            request.addHeader("Authorization", "Bearer " + HUGGINGFACE_API_KEY);
            request.addHeader("Content-Type", "application/json");

            // 🔹 Paramètres avancés pour une réponse fluide
            String jsonPayload = "{"
                    + "\"inputs\": \"" + question + "\","
                    + "\"parameters\": {\"temperature\": 0.7, \"max_length\": 200, \"top_p\": 0.9}"
                    + "}";

            request.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {

                StringBuilder responseString = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseString.append(line);
                }

                logger.info("🔍 Réponse brute de Hugging Face : " + responseString.toString());

                // Vérifier si la réponse est une page HTML (erreur 503 ou autre)
                if (responseString.toString().startsWith("<!DOCTYPE html>")) {
                    logger.severe("❌ Hugging Face a renvoyé une page d'erreur HTML.");
                    return "😕 Le service IA est temporairement indisponible. Réessaie plus tard.";
                }

                // Vérifier si la réponse contient une erreur JSON
                if (responseString.toString().contains("\"error\"")) {
                    JSONObject errorResponse = new JSONObject(responseString.toString());
                    logger.severe("❌ Erreur Hugging Face : " + errorResponse.getString("error"));
                    return "😕 Désolé, l'IA ne peut pas répondre pour le moment.";
                }

                // 🔹 Extraction et formatage de la réponse
                try {
                    JSONArray jsonArray = new JSONArray(responseString.toString());
                    if (jsonArray.length() > 0) {
                        JSONObject firstObject = jsonArray.getJSONObject(0);
                        if (firstObject.has("generated_text")) {
                            return firstObject.getString("generated_text").trim();
                        }
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "❌ Erreur de parsing JSON", e);
                    return "❌ Erreur de traitement de la réponse IA.";
                }

                return "😕 Désolé, aucune réponse valide trouvée.";
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Erreur de connexion à Hugging Face", e);
            return "❌ Erreur avec l'API Hugging Face.";
        }
    }
}
