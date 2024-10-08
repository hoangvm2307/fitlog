package com.example.fitlog;

public class Template {
    private String title;
    private String description;
    private String lastUsed;

    public Template(String title, String description, String lastUsed) {
        this.title = title;
        this.description = description;
        this.lastUsed = lastUsed;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLastUsed() {
        return lastUsed;
    }
}