package com.example.event_planner;

public class user_info {
    String name;
    String email;
    String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    String phone;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    String role;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public user_info(String name, String email, String city, String phone,String role) {
        this.name = name;
        this.email = email;
        this.city = city;
        this.phone = phone;
        this.role=role;
    }

    public user_info() {

    }
}
