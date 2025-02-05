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

            String jsonInput = """
            {
                "model": "%s",
                "prompt": "%s",
                "stream": true,
                "keep_alive": 0
            }
            """.formatted(model, prompt);

            // Open connection
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(jsonInput);
                writer.flush();
            }


            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        JsonNode jsonNode = objectMapper.readTree(line);
                        String word = jsonNode.get("response").asText();
                        System.out.print(word + " ");
                        System.out.flush();
                    }
                }
            }

            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
