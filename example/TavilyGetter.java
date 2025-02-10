package org.example;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TavilyGetter {
    private static final String TAVILY_API_KEY = "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";
    private static final String TAVILY_ENDPOINT = "https://api.tavily.com/search";

    public static List<String> fetchTopInternshipUrlsContent(String query) throws Exception{
        List<String> urls = new ArrayList<>();

        // Create the JSON request payload
        String jsonInputString = "{ \"query\": \"" + query + "\", \"api_key\": \"" + TAVILY_API_KEY + "\", \"num_results\": 3 }";

        // Setup HTTP connection
        URL url = new URL(TAVILY_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // Parse JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.toString());

        // Extract URLs from response
        if (jsonResponse.has("results")) {
            for (JsonNode result : jsonResponse.get("results")) {
                if (result.has("url")) {
                    urls.add(result.get("url").asText());
                }
                if (urls.size() == 5) break; // Only fetch top 3 results
            }
        }
        List<String> content = new ArrayList<>();
        for (String s : urls) {
            content.add(fetchPageContent(s));
        }
        return content;
    }


    private static String fetchPageContent(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.body().text();
        } catch (Exception e) {
            return "";
        }
    }
}
