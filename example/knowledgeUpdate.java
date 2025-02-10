package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class knowledgeUpdate {
    public static void update(String path){
        List<String> chunk = new ArrayList<>();
        int chunkSize=15;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            int lineCount = 0;

            while ((line = br.readLine()) != null) {
                chunk.add(line);
                lineCount++;
                if (lineCount % chunkSize == 0) {
                  //processChunk(chunk);
                    String text="";
                    for(String s: chunk){
                        text = text + s;
                    }
                    List<Double> vector =EmbeddingModel.getEmbeddingVector("nomic-embed-text",text);
                    if(vector!=null){
                        WeaviateClient.insertObject(text,vector);
                    }

                    chunk.clear();

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void webKnowledgeInsertDatabase(String text){
        List<String> chunks = splitIntoChunks(text, 250);
        for(String chunk : chunks){
            List<Double> vector = EmbeddingModel.getEmbeddingVector("nomic-embed-text" , chunk);
            if(vector!=null) {
                    WeaviateClient.insertObject(chunk,vector);
                }
            }
        }
    public static void webKnowledgeDeleteDatabase(String text){
        List<String> chunks = splitIntoChunks(text, 250);
        for(String chunk : chunks){
            List<Double> vector = EmbeddingModel.getEmbeddingVector("nomic-embed-text" , chunk);
            if(vector!=null) {
                WeaviateClient.deleteObjectByText(chunk);
            }
        }
    }
    private static List<String> splitIntoChunks(String text, int chunkSize) {
        String[] words = text.split("\\s+"); // Split by whitespace
        List<String> chunks = new ArrayList<>();

        StringBuilder chunk = new StringBuilder();
        int wordCount = 0;

        for (String word : words) {
            chunk.append(word).append(" ");
            wordCount++;

            if (wordCount == chunkSize) {
                chunks.add(chunk.toString().trim());
                chunk.setLength(0); // Clear the StringBuilder
                wordCount = 0;
            }
        }

        // Add remaining words (if any)
        if (chunk.length() > 0) {
            chunks.add(chunk.toString().trim());
        }

        return chunks;
    }
}


