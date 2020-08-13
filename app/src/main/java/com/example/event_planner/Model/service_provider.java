package com.example.event_planner.Model;

public class service_provider {
    private String category;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    private int capacity,price;
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String name="/^(?=.?[A-Z])(?=.?[a-z])(?=.?[0-9])(?=.?[^\\w\\s]).{8,}$/";
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private String email;
    private String address;
    private boolean approved;

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public service_provider(String category, String name, String city, String email, String address, String image_url, String phone, int capacity, int price, boolean approved) {
        this.category = category;
        this.name = name;
        this.city = city;
        this.email = email;
        this.address = address;
        this.image_url = image_url;
        this.phone = phone;
        this.capacity=capacity;
        this.price=price;
        this.approved=approved;
    }

    public service_provider() {

    }

    private String image_url;

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String phone;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
