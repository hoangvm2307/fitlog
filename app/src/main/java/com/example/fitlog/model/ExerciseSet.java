package com.example.fitlog.model;

public class ExerciseSet {
    public int id;
    public int sessionId;
    public int exerciseId;
    public int setNumber;
    public float weight;
    public int reps;

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