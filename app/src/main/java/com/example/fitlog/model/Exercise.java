package com.example.fitlog.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {
    private int id;
    private int userId;
    private String name;
    private String instruction;
    private String bodypart;
    private String category;
    private String visibility;
    private String imageName;
    private int exerciseOrder; // New field

    public Exercise(int id, int userId, String name, String instruction, String bodypart, String category, String visibility, String imageName) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.instruction = instruction;
        this.bodypart = bodypart;
        this.category = category;
        this.visibility = visibility;
        this.imageName = imageName;
        this.exerciseOrder = 0; // Default value
    }

    public Exercise(String name, String bodypart, String category){
        this.name = name;
        this.bodypart = bodypart;
        this.category = category;
    }

    protected Exercise(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        name = in.readString();
        instruction = in.readString();
        bodypart = in.readString();
        category = in.readString();
        visibility = in.readString();
        imageName = in.readString();
        exerciseOrder = in.readInt();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeString(name);
        dest.writeString(instruction);
        dest.writeString(bodypart);
        dest.writeString(category);
        dest.writeString(visibility);
        dest.writeString(imageName);
        dest.writeInt(exerciseOrder);
    }

    // Existing getters and setters...
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

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getBodypart() {
        return bodypart;
    }

    public void setBodypart(String bodypart) {
        this.bodypart = bodypart;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getExerciseOrder() {
        return exerciseOrder;
    }

    public void setExerciseOrder(int exerciseOrder) {
        this.exerciseOrder = exerciseOrder;
    }
}
