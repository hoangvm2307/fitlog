package com.example.fitlog.model;

public class Exercise {
    private int id;
    private int userId;
    private String name;
    private String instruction;
    private String bodypart;
    private String category;
    private String visibility;

    // Constructor
    public Exercise(int id, int userId, String name, String instruction, String bodypart, String category, String visibility) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.instruction = instruction;
        this.bodypart = bodypart;
        this.category = category;
        this.visibility = visibility;
    }

    // Getters and setters
    // ... (implement getters and setters for all fields)
}