package org.example;

import java.util.ArrayList;
import java.util.List;

public class Querying {
    String answer;
    public static void InternQuery(UserrPreferences preferences) throws Exception{
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

        String webQuery = "Best Summer Internship Programs in " + preferences.getDesiredArea() + " in " + preferences.getMajor();
        List<String> webText = TavilyGetter.fetchTopInternshipUrlsContent(webQuery);
        for(String s:webText){
            knowledgeUpdate.webKnowledgeInsertDatabase(s);
        }

        List<String> knowledge = WeaviateClient.searchByVector(EmbeddingModel.getEmbeddingVector("nomic-embed-text",Q));
        String askFromAI="I have this knowledge and information about where is the best country to apply for internship. Help to find the University of Institute which suits me the best.";
        for(String s:knowledge){
            askFromAI+=" " + s;
        }
        askFromAI += " " + Q + " ANWER IN ENGLISH. DO NOT ANSWER FROM YOURSELF! ANSWER FROM THE KNOWLEDGE PROVIDED TO YOU! Respond in this format:[ Institute or University] [next line] deadline: [deadline] [next line] country: [country]";
        OllamaClient.askQuestion("llama3.2", askFromAI);
        for(String s:webText){
            knowledgeUpdate.webKnowledgeDeleteDatabase(s);
        }
    }
    public static void ProfessorQuery(UserrPreferences preferences) throws Exception{
        String Q="I am looking for the a list of professors (maximum 10) in the field of " + preferences.getMajor() + " in the university of "+ preferences.getUniversity();

        String webQuery = "List of faculty members of the " + preferences.getUniversity() + " with their contact information";
        List<String> webText = TavilyGetter.fetchTopInternshipUrlsContent(webQuery);
        for(String s:webText){
            knowledgeUpdate.webKnowledgeInsertDatabase(s);
        }
        List<String> knowledge = WeaviateClient.searchByVector(EmbeddingModel.getEmbeddingVector("nomic-embed-text", Q));
        Q+=" The Knowledge: ";
        for (String s:knowledge){
            Q+=" " + s;
        }
        Q+=". Only answer using the knowledge given to you. Answer it in English and obey this format and do not say anything else. For every professor: [name of professor] [go to next line] [contact information] [go to next line] [interests of professor] ";
        OllamaClient.askQuestion("llama3.2", Q);
        for(String s:webText){
            knowledgeUpdate.webKnowledgeDeleteDatabase(s);
        }

    }
}
