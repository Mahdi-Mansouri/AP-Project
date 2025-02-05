package org.example;

public class UserrPreferences {
    private double GPA;
    private String major;
    private String language;
    private double languageScore = -1;
    private String desiredArea;
    private String extraDiscription;


    public String getExtraDiscription() {
        return extraDiscription;
    }

    public void setExtraDiscription(String extraDiscription) {
        this.extraDiscription = extraDiscription;
    }

    public double getGPA() {
        return GPA;
    }

    public void setGPA(double GPA) {
        this.GPA = GPA;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getLanguageScore() {
        return languageScore;
    }

    public void setLanguageScore(double languageScore) {
        this.languageScore = languageScore;
    }

    public String getDesiredArea() {
        return desiredArea;
    }

    public void setDesiredArea(String desiredArea) {
        this.desiredArea = desiredArea;
    }
}
