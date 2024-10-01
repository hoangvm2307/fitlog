package com.example.fitlog.model;

public class ExerciseSet {
    private int id;
    private int sessionId;
    private int exerciseId;
    private int setNumber;
    private float weight;
    private int reps;

    // Constructor
    public ExerciseSet(int id, int sessionId, int exerciseId, int setNumber, float weight, int reps) {
        this.id = id;
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public float getWeight() {
        return weight;
    }

    public int getReps() {
        return reps;
    }
}