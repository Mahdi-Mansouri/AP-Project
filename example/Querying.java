package org.example;

import java.util.List;

public class Querying {
    String answer;
    public Querying(UserrPreferences preferences) {
        String Q;
        Q="I want to go to internship in another country. Currently I'm living in Iran.";
        Q+=" My major and field of study is " + preferences.getMajor() + " and the best for me is to go to " + preferences.getDesiredArea();
        if(preferences.getLanguageScore() == -1){
            Q+=" I don't have any specific license in any langauges";
        }else{
            Q+= " I have a language license in " + preferences.getLanguage() + " with score of " + preferences.getLanguageScore();
        }
        Q+=" My GPA is " + preferences.getGPA();
        Q+= " And also it is important for me to " + preferences.getExtraDiscription();
        List<String> knowledge = WeaviateClient.searchByVector(EmbeddingModel.getEmbeddingVector("nomic-embed-text",Q));
        String askFromAI="I have this knowledge and information about where is the best country to apply for internship";
        for(String s:knowledge){
            askFromAI+=" " + s;
        }
        askFromAI += " Answer this Question:" + Q + " ANWER IN ENGLISH. DO NOT ANSWER FROM YOURSELF! ANSWER FROM THE KNOWLEDGE PROVIDED TO YOU!";
        System.out.println(askFromAI);
        OllamaClient.askQuestion("llama3.2", askFromAI);
    }
}
