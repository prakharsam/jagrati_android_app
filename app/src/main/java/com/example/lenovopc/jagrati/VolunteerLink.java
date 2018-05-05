package com.example.lenovopc.jagrati;

public class VolunteerLink {
    private int id;
    private String name;
    private String discipline;
    private String displayPicURL;

    public VolunteerLink(int id, String name, String discipline, String displayPicURL) {
        this.id = id;
        this.name = name;
        this.discipline = discipline;
        this.displayPicURL = displayPicURL;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDiscipline() {
        return this.discipline;
    }

    public String getDisplayPicURL() {
        return this.displayPicURL;
    }
}