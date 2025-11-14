package com.example.campusexpensemanagerforstudent.models;

public class Expense {
    private int id;
    private int userId;
    private int categoryId;
    private double amount;
    private String description;
    private String date;
    private String time;
    private String receiptPhoto;
    private String categoryName; // For joining with Category table

    public Expense() {
    }

    public Expense(int id, int userId, int categoryId, double amount, String description,
                   String date, String time, String receiptPhoto) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.time = time;
        this.receiptPhoto = receiptPhoto;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getReceiptPhoto() {
        return receiptPhoto;
    }

    public String getCategoryName() {
        return categoryName;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setReceiptPhoto(String receiptPhoto) {
        this.receiptPhoto = receiptPhoto;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
