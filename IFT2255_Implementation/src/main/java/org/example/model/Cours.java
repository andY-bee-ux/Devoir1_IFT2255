package org.example.model;
import java.util.List;
import java.util.Map;

// alt insert : getters and setters
public class Cours {
    private String id;     //Id du cours
    private String description;     //Description du cours
    private String name;
    private String scheduledSemester;   //Trimestres où le cours est offert
    //private boolean includeSchedule;//
    //private String[] schedules;

    private String[] prerequisite_courses;
    private String[] equivalent_courses;
    private String[] concomitant_courses;
    private String udemWebsite;         //Site Web attache au cours.
    private float credits;
    private String requirement_text;
    private Map<String, Boolean> available_terms;
    private Map<String, Boolean> available_periods;
    private List<Schedule> schedules;

    public static class Schedule {
        private String _id;
        private String sigle;
        private String name;
        private String semester;
        private List<Section> sections;
        private String fetch_date;
        private int semester_int;
        public Schedule() {
        }

        public int getSemester_int() {
            return semester_int;
        }

        public void setSemester_int(int semester_int) {
            this.semester_int = semester_int;
        }

        public void setSections(List<Section> sections) {
            this.sections = sections;
        }

        public String getFetch_date() {
            return fetch_date;
        }

        public void setFetch_date(String fetch_date) {
            this.fetch_date = fetch_date;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getSigle() {
            return sigle;
        }

        public void setSigle(String sigle) {
            this.sigle = sigle;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSemester() {
            return semester;
        }

        public void setSemester(String semester) {
            this.semester = semester;
        }

        public List<Section> getSections() {
            return sections;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(semester).append("\n");

            if (sections != null) {
                for (Section sec : sections) {
                    sb.append("Section ").append(sec.getName()).append("\n");

                    if (sec.getVolets() != null) {
                        for (Volet v : sec.getVolets()) {

                            if (v.getActivities() != null) {
                                for (Activity a : v.getActivities()) {
                                    sb.append(String.join("/", a.getDays()))
                                            .append(" ")
                                            .append(a.getStart_time()).append("–").append(a.getEnd_time())
                                            .append(" (").append(a.getMode()).append(")")
                                            .append("\n");
                                }
                            }
                        }
                    }
                }
            }

            return sb.toString().trim();
        }

        public String toStringPourSemester(String semesterRecherche) {
            StringBuilder sb = new StringBuilder();

            if (!this.semester.equalsIgnoreCase(semesterRecherche)) {
                return "Aucun schedule pour le semester " + semesterRecherche;
            }

            sb.append("Semester ").append(this.semester).append("\n");

            if (sections != null) {
                for (Section sec : sections) {
                    sb.append("Section ").append(sec.getName()).append("\n");

                    if (sec.getVolets() != null) {
                        for (Volet v : sec.getVolets()) {

                            if (v.getActivities() != null) {
                                for (Activity a : v.getActivities()) {
                                    sb.append(String.join("/", a.getDays()))
                                            .append(" ")
                                            .append(a.getStart_time()).append("–").append(a.getEnd_time())
                                            .append(" (").append(a.getMode()).append(")")
                                            .append("\n");
                                }
                            }
                        }
                    }
                }
            }

            return sb.toString().trim();
        }


    }

    public static class Section {
        private String number_inscription;
        private List<String> teachers;
        private String capacity;
        private List<Volet> volets;
        private String name;

        public Section() {

        }

        public String getNumber_inscription() {
            return number_inscription;
        }

        public void setNumber_inscription(String number_inscription) {
            this.number_inscription = number_inscription;
        }

        public List<String> getTeachers() {
            return teachers;
        }

        public void setTeachers(List<String> teachers) {
            this.teachers = teachers;
        }

        public String getCapacity() {
            return capacity;
        }

        public void setCapacity(String capacity) {
            this.capacity = capacity;
        }

        public List<Volet> getVolets() {
            return volets;
        }

        public void setVolets(List<Volet> volets) {
            this.volets = volets;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    public static class Volet {
        private String name;
        private List<Activity> activities;

        public Volet() {

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Activity> getActivities() {
            return activities;
        }

        public void setActivities(List<Activity> activities) {
            this.activities = activities;
        }
    }
    public static class Activity {
        private List<String> days;
        private String start_time;
        private String end_time;
        private String start_date;
        private String end_date;
        private String campus;
        private String place;
        private String pavillon_name;
        private String room;
        private String mode;

        public Activity(){
        }

        public List<String> getDays() {
            return days;
        }

        public void setDays(List<String> days) {
            this.days = days;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public String getCampus() {
            return campus;
        }

        public void setCampus(String campus) {
            this.campus = campus;
        }

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getPavillon_name() {
            return pavillon_name;
        }

        public void setPavillon_name(String pavillon_name) {
            this.pavillon_name = pavillon_name;
        }

        public String getRoom() {
            return room;
        }

        public void setRoom(String room) {
            this.room = room;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }
    }
    //Constructeur général.

    // jackson a besoin d'un constructeur vide pour faire le mapping
    public Cours(){}
    public Cours(Map<String, Boolean> available_terms, String id, String description, String name, String scheduledSemester, List<Schedule> schedules, String[] prerequisite_courses, String[] equivalent_courses, String[] concomitant_courses, String udemWebsite, float credits, String requirement_text, Map<String, Boolean> available_periods) {
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

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
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

