package com.example.event_planner.Model;

public class Food {
    String food_name,food_image_url;
    int price;
    Boolean Available;

    public Boolean getAvailable() {
        return Available;
    }

    public void setAvailable(Boolean available) {
        Available = available;
    }

    public Food() {

    }
    public Food(String food_name, String food_image_url, int price,boolean Available) {
        this.food_name = food_name;
        this.food_image_url = food_image_url;
        this.price = price;
        this.Available=Available;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_image_url() {
        return food_image_url;
    }

    public void setFood_image_url(String food_image_url) {
        this.food_image_url = food_image_url;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
