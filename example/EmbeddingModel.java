package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class EmbeddingModel {
    private static final String EMBED_URL = "http://localhost:11434/api/embeddings";

    public static List<Double> getEmbeddingVector(String model, String text) {
        try {
            URL url = new URL(EMBED_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();


            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);


            String jsonInput = String.format("{\"model\": \"%s\", \"prompt\": \"%s\"}", model, text);

            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }


            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }


            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.toString());


            JsonNode embeddingNode = jsonResponse.get("embedding");
            if (embeddingNode != null && embeddingNode.isArray()) {
                return objectMapper.convertValue(embeddingNode, List.class);
            } else {
                System.err.println("Invalid response: " + response);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
