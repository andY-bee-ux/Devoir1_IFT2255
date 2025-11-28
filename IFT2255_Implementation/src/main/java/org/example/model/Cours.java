package org.example.model;
import java.util.Map;

// alt insert : getters and setters
public class Cours {
    private String id;     //Id du cours
    private String description;     //Description du cours
    private String name;
    private String scheduledSemester;   //Trimestres où le cours est offert
    //private boolean includeSchedule;//
    private String[] schedules;

    private String[] prerequisite_courses;
    private String[] equivalent_courses;
    private String[] concomitant_courses;
    private String udemWebsite;         //Site Web attache au cours.
    private float credits;
    private String requirement_text;
    private Map<String, Boolean> available_terms;
    private Map<String, Boolean> available_periods;
    //Constructeur général.

    // jackson a besoin d'un constructeur vide pour faire le mapping
    public Cours(){}
    public Cours(Map<String, Boolean> available_terms, String id, String description, String name, String scheduledSemester, String[] schedules, String[] prerequisite_courses, String[] equivalent_courses, String[] concomitant_courses, String udemWebsite, float credits, String requirement_text, Map<String, Boolean> available_periods) {
        this.available_terms = available_terms;
        this.id = id;
        this.description = description;
        this.name = name;
        this.scheduledSemester = scheduledSemester;
        this.schedules = schedules;
        this.prerequisite_courses = prerequisite_courses;
        this.equivalent_courses = equivalent_courses;
        this.concomitant_courses = concomitant_courses;
        this.udemWebsite = udemWebsite;
        this.credits = credits;
        this.requirement_text = requirement_text;
        this.available_periods = available_periods;
    }

    //Getters et Setters.
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }





    public String getDescription() {
        return description;
    }

    public String getScheduledSemester() {
        return scheduledSemester;
    }

    public String getUdemWebsite() {
        return udemWebsite;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public void setScheduledSemester(String scheduledSemester) {
        this.scheduledSemester = scheduledSemester;
    }

    public void setUdemWebsite(String udemWebsite) {
        this.udemWebsite = udemWebsite;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getSchedules() {
        return schedules;
    }

    public void setSchedules(String[] schedules) {
        this.schedules = schedules;
    }

    public String[] getPrerequisite_courses() {
        return prerequisite_courses;
    }

    public void setPrerequisite_courses(String[] prerequisite_courses) {
        this.prerequisite_courses = prerequisite_courses;
    }

    public String[] getEquivalent_courses() {
        return equivalent_courses;
    }

    public void setEquivalent_courses(String[] equivalent_courses) {
        this.equivalent_courses = equivalent_courses;
    }

    public String[] getConcomitant_courses() {
        return concomitant_courses;
    }

    public void setConcomitant_courses(String[] concomitant_courses) {
        this.concomitant_courses = concomitant_courses;
    }

    public float getCredits(){
        return credits;
    }

    public void setCredits(float credits) {
        this.credits = credits;
    }

    public String getRequirement_text() {
        return requirement_text;
    }

    public void setRequirement_text(String requirement_text) {
        this.requirement_text = requirement_text;
    }

    public Map<String, Boolean> getAvailable_terms() {
        return available_terms;
    }

    public void setAvailable_terms(Map<String, Boolean> available_terms) {
        this.available_terms = available_terms;
    }

    public Map<String, Boolean> getAvailable_periods() {
        return available_periods;
    }

    public void setAvailable_periods(Map<String, Boolean> available_periods) {
        this.available_periods = available_periods;
    }
}

