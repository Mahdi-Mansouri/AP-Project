package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.util.*;

public class WeaviateClient {
    private static final String WEAVIATE_URL = "http://localhost:8080/v1";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void createSchema() {
        String schemaJson = """
        {
            "class": "TextEmbeddings",
            "vectorIndexType": "hnsw",
            "properties": [
                {"name": "text", "dataType": ["string"]}
            ]
        }
        """;

        sendRequest("/schema", "POST", schemaJson);
    }

    public static void insertObject(String text, List<Double> vector) {
        if (objectExists(text)) {
            //System.out.println("⚠️ Object already exists in Weaviate. Skipping insert.");
            return;
        }

        String jsonInput = """
        {
            "class": "TextEmbeddings",
            "properties": {"text": "%s"},
            "vector": %s
        }
        """.formatted(text.replace("\"", "\\\""), vector.toString());

        sendRequest("/objects", "POST", jsonInput);
    }
    private static boolean objectExists(String text) {
        String jsonQuery = """
        {
            "query": "{
                Get {
                    TextEmbeddings(
                        where: {
                            path: [\\"text\\"],
                            operator: Equal,
                            valueText: \\"%s\\"
                        }
                    ) {
                        text
                    }
                }
            }"
        }
        """.formatted(text.replace("\"", "\\\""));

        String response = sendRequest("/graphql", "POST", jsonQuery);

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode texts = jsonNode.at("/data/Get/TextEmbeddings");

            return texts.isArray() && texts.size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static List<String> searchByVector(List<Double> vector) {
        try {
            // Convert the vector list to a valid JSON array format
            String vectorJsonArray = objectMapper.writeValueAsString(vector);

            // Construct the GraphQL query with proper formatting
            String jsonInput = """
        {
            "query": "{ Get { TextEmbeddings( nearVector: { vector: %s, certainty: 0 }, limit: 5 ) { text } } }"
        }
        """.formatted(vectorJsonArray);


            String response = sendRequest("/graphql", "POST", jsonInput);

            // Print the response for debugging
            System.out.println("Weaviate Response: " + response);

            List<String> results = new ArrayList<>();

            // Parse response
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode texts = jsonNode.at("/data/Get/TextEmbeddings");

            if (texts.isArray()) {
                for (JsonNode node : texts) {
                    results.add(node.get("text").asText());
                }
            }

            return results;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }





    private static String sendRequest(String endpoint, String method, String jsonInput) {
        try {
            URL url = new URL(WEAVIATE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            if (jsonInput != null) {
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonInput.getBytes());
                }
            }

            Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";

        } catch (Exception e) {
            return "";
        }
    }
}
