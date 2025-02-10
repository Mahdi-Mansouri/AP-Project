package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //WeaviateClient.createSchema();
        System.out.println("Welcome to my AP Project");
        System.out.println("Interactive Shell - Available commands: update PATH, ask , professors, exit");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            // Split input into command and arguments
            String[] parts = input.split("\\s+", 2); // Splitting at first space
            String command = parts[0];

            switch (command) {
                case "exit":
                    System.out.println("Exiting shell...");
                    scanner.close();
                    return;

                case "update":
                    knowledgeUpdate.update(parts[1]);
                    break;

                case "ask":
                    askInternship(scanner);
                    break;
                case "professors":
                    askProfessors(scanner);
                    break;

                default:
                    System.out.println("Unknown command: " + command);
            }
        }

    }
    static void askInternship(Scanner scanner){
        System.out.println("I will ask you some Questions to help you.\nWhat is you major or field of study?");
        UserrPreferences preferences = new UserrPreferences();
        preferences.setMajor(scanner.nextLine());
        System.out.println("What is your GPA?");
        preferences.setGPA(scanner.nextDouble());
        scanner.nextLine();
        System.out.println("What language do you have any license in it? Please enter the exam name. If none, please leave it blank");
        preferences.setLanguage(scanner.nextLine());
        if(preferences.getLanguage() != ""){
            System.out.println("Please enter the grade of your language license");
            preferences.setLanguageScore(scanner.nextDouble());
            scanner.nextLine();
        }
        System.out.println("What other details do you want to add?");
        preferences.setExtraDiscription(scanner.nextLine());
        System.out.println("What area (for example, continent) do you like the most?");
        preferences.setDesiredArea(scanner.nextLine());
        try{
            Querying.InternQuery(preferences);
        } catch (Exception e) {
            return;
        }

    }
    static void askProfessors(Scanner scanner){
        System.out.println("I will help you to find professors in your field that helps you to make connections with them\nWhat is your filed of study?");
        UserrPreferences preferences = new UserrPreferences();
        preferences.setMajor(scanner.nextLine());
        System.out.println("What Universities or Institutes do you have in mind?");
        preferences.setUniversity(scanner.nextLine());
        try{
            Querying.ProfessorQuery(preferences);
        } catch (Exception e) {
            return;
        }
    }

}