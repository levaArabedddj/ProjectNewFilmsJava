package com.example.DTO;

public class DtoStaff {

    private String name;
    private String surName;
    private int salaryPerHour;
    private Integer rating;  // Используй Integer для rating, так как он может быть null

    // Конструктор
    public DtoStaff(String name, String surName, int salaryPerHour, Integer rating) {
        this.name = name;
        this.surName = surName;
        this.salaryPerHour = salaryPerHour;
        this.rating = rating;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public int getSalaryPerHour() {
        return salaryPerHour;
    }

    public void setSalaryPerHour(int salaryPerHour) {
        this.salaryPerHour = salaryPerHour;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
