package com.example.fitlog.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Workout {
    private int id;
    private int userId;
    private int templateId;
    private Date startTime;
    private Date endTime;
    public List<ExerciseSet> exerciseSets; // Corrected this line

    // Constructor
    public Workout(int id, int userId, int templateId, Date startTime, Date endTime) {
        this.id = id;
        this.userId = userId;
        this.templateId = templateId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getTemplateId() {
        return templateId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public List<ExerciseSet> getExerciseSets() {
        return exerciseSets;
    }

    public void setExerciseSets(List<ExerciseSet> exerciseSets) {
        this.exerciseSets = exerciseSets;
    }

    public void addExerciseSet(ExerciseSet set) {
        if (this.exerciseSets == null) {
            this.exerciseSets = new ArrayList<>();
        }
        this.exerciseSets.add(set);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
