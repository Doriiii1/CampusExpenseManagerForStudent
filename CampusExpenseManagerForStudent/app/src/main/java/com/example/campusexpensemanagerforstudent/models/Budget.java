package com.example.campusexpensemanagerforstudent.models;

public class Budget {
    private int id;
    private int userId;
    private int categoryId;
    private double amount;
    private String period; // "weekly" or "monthly"
    private String startDate;
    private String endDate;
    private double spent;
    private double remaining;
    private String categoryName; // For joining with Category table

    public Budget() {
    }

    public Budget(int id, int userId, int categoryId, double amount, String period,
                  String startDate, String endDate) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getPeriod() {
        return period;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public double getSpent() {
        return spent;
    }

    public double getRemaining() {
        return remaining;
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

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setSpent(double spent) {
        this.spent = spent;
        this.remaining = this.amount - spent;
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getPercentageUsed() {
        if (amount == 0) return 0;
        return (int) ((spent / amount) * 100);
    }
}
