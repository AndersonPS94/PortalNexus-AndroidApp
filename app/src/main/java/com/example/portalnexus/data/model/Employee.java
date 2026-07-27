package com.example.portalnexus.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Employee implements Serializable {
    private int id;
    
    @SerializedName("nome")
    private String name;
    
    @SerializedName("cargo")
    private String position;
    
    private String email;
    
    @SerializedName("salario")
    private double salary;
    
    @SerializedName("ativo")
    private boolean active;

    private transient String photo;

    public Employee(int id, String name, String position, String email, double salary, boolean active, String photo) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.email = email;
        this.salary = salary;
        this.active = active;
        this.photo = photo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
}
