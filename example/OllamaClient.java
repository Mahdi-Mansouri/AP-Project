package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OllamaClient {

    public static void askQuestion(String model, String prompt) {
        try {
            // Prepare API URL
            URL url = new URL("http://localhost:11434/api/generate");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configure request
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create JSON request body with "keep_alive": 0
            String jsonRequest = "{ \"model\": \"" + model + "\", \"prompt\": \"" + prompt + "\", \"keep_alive\": 0 }";

            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response in streaming mode
            ObjectMapper objectMapper = new ObjectMapper();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    JsonNode jsonResponse = objectMapper.readTree(responseLine.trim());
                    if (jsonResponse.has("response")) {
                        // Print each word as soon as it's generated
                        System.out.print(jsonResponse.get("response").asText());
                        System.out.flush(); // Ensures immediate output
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\nError: Unable to get a response.");
        }
    }
}

