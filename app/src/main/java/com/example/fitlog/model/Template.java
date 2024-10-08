package com.example.fitlog.model;

import java.time.LocalDateTime;

public class Template {
    private String title;
    private String description;
    private String lastUsed;
    private String visibility;
    private LocalDateTime createAt;
    private LocalDateTime lastUsed;

    public Template() {
    }

    public Template(int id, int userId, String name, String description, String visibility, LocalDateTime createAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
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
        this.lastUsed = lastUsed;
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

