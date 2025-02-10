package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OllamaClient {
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static void askQuestion(String model, String prompt) {
        try {
            URL url = new URL("http://localhost:11434/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = "{\"model\":\"" + model + "\", \"prompt\":\"" + prompt + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes());
            }

            // Read the streamed response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                boolean lastWasSpace = false;
                while ((line = br.readLine()) != null) {
                    JsonNode responseData = objectMapper.readTree(line);
                    String word = responseData.get("response").asText();

                    if (!word.isBlank()) {
                        if (lastWasSpace && word.startsWith(" ")) {
                            word = word.trim();  // Remove extra leading spaces
                        }
                        System.out.print(word + " ");
                        lastWasSpace = word.endsWith(" ");
                    }

                    if (responseData.get("done").asBoolean()) {
                        break;
                    }
                }
            }
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
