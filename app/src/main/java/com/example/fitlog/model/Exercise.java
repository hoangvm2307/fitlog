package com.example.fitlog.model;

public class Exercise {
    private int id;
    private int userId;
    private String name;
    private String instruction;
    private String bodypart;
    private String category;
    private String visibility;
    private String imageName; // Thêm trường mới này

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

    // Cập nhật constructor
    public Exercise(int id, int userId, String name, String instruction, String bodypart, String category, String visibility, String imageName) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.instruction = instruction;
        this.bodypart = bodypart;
        this.category = category;
        this.visibility = visibility;
        this.imageName = imageName;
    }

    // Thêm getter và setter cho imageName
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    // Getters and setters
    // ... (implement getters and setters for all fields)

//    public int getImageResourceId() {
//        // Giả sử tên file hình ảnh trùng với tên bài tập (chuyển thành chữ thường và thay khoảng trắng bằng gạch dưới)
//        String imageName = name.toLowerCase().replace(" ", "_");
//        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
//    }
}