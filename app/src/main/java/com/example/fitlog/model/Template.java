package com.example.fitlog.model;

import java.time.LocalDateTime;

public class Template {
    private int id;
    private int userId;
    private String name;
    private String description;
    private String visibility;
    private LocalDateTime createAt;

    public Template() {
    }

    public Template(int id, int userId, String name, String description, String visibility, LocalDateTime createAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.createAt = createAt;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
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
