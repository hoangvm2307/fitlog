package com.example.fitlog.model;

import java.time.LocalDateTime;

public class Template {

    private int id;
    private int userId;
    private String title;
    private String description;
    private String visibility;
    private LocalDateTime createAt;
    private LocalDateTime lastUsed;


    public Template(int id, int userId, String title, String description, String visibility, LocalDateTime createAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.visibility = visibility;
        this.createAt = createAt;
        this.lastUsed = createAt; // Initialize lastUsed with createAt
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
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
