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
        return getObjectIdByText(text) != null;
    }

    private static String getObjectIdByText(String text) {
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
                        _additional { id }
                    }
                }
            }"
        }
        """.formatted(text.replace("\"", "\\\""));

        String response = sendRequest("/graphql", "POST", jsonQuery);

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode texts = jsonNode.at("/data/Get/TextEmbeddings");

            if (texts.isArray() && texts.size() > 0) {
                return texts.get(0).at("/_additional/id").asText();
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    public static void deleteObjectByText(String text) {
        String objectId = getObjectIdByText(text);
        if (objectId != null) {
            sendRequest("/objects/" + objectId, "DELETE", null);
        }
    }

    public static List<String> searchByVector(List<Double> vector) {
        try {
            String vectorJsonArray = objectMapper.writeValueAsString(vector);

            String jsonInput = """
        {
            "query": "{ Get { TextEmbeddings( nearVector: { vector: %s, certainty: 0 }, limit: 15 ) { text } } }"
        }
        """.formatted(vectorJsonArray);

            String response = sendRequest("/graphql", "POST", jsonInput);

            List<String> results = new ArrayList<>();
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
