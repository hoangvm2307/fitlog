package com.example.fitlog.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;

public class Template implements Parcelable  {

    private int id;
    private int userId;
    private String name;
    private String description;
    private String visibility;
    private LocalDateTime createAt;
    private LocalDateTime lastUsed;

    public Template(int id, int userId, String name, String description, String visibility, LocalDateTime createAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.createAt = createAt;
    }

    public Template(int id, int userId, String name, String description, String visibility, LocalDateTime createAt, LocalDateTime lastUsed) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.createAt = createAt;
        this.lastUsed = lastUsed;
    }

    protected Template(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        name = in.readString();
        description = in.readString();
        visibility = in.readString();
    }

    public static final Creator<Template> CREATOR = new Creator<Template>() {
        @Override
        public Template createFromParcel(Parcel in) {
            return new Template(in);
        }

        @Override
        public Template[] newArray(int size) {
            return new Template[size];
        }
    };

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

    public String getCreateAt() {
        return createAt.toString();
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(visibility);
    }
}
