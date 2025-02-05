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
}
