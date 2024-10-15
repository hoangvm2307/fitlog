package com.example.fitlog.model;

import java.time.LocalDateTime;

public class Template {
    private int id;
    private String title;
    private int userId;
    private String description;
    private String lastUsed;
    private String visibility;
    private LocalDateTime createAt;

    public Template() {
    }

    public Template(int id, int userId, String title, String description, String visibility, LocalDateTime createAt) {
        this.id = id;
        this.userId = userId;
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
        return this.lastUsed.toString();
    }

    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed.toString();
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}

