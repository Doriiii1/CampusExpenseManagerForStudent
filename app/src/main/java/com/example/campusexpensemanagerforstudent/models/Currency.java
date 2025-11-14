package com.example.campusexpensemanagerforstudent.models;

public class Currency {
    private int id;
    private String code;
    private String symbol;
    private double exchangeRate;

    public Currency() {
    }

    public Currency(int id, String code, String symbol, double exchangeRate) {
        this.id = id;
        this.code = code;
        this.symbol = symbol;
        this.exchangeRate = exchangeRate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public String toString() {
        return code + " (" + symbol + ")";
    }
}
