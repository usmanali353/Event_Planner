package com.example.event_planner.Model;

public class Themes {
    public String getTheme_name() {
        return theme_name;
    }
    Boolean Available;

    public Boolean getAvailable() {
        return Available;
    }

    public void setAvailable(Boolean available) {
        Available = available;
    }

    public Themes() {

    }

    public void setTheme_name(String theme_name) {
        this.theme_name = theme_name;
    }

    public int getTheme_price() {
        return theme_price;
    }

    public void setTheme_price(int theme_price) {
        this.theme_price = theme_price;
    }

    public String getTheme_image() {
        return theme_image;
    }

    public void setTheme_image(String theme_image) {
        this.theme_image = theme_image;
    }

    String theme_name,theme_image;
    int theme_price;
    public Themes(String theme_name, int theme_price, String theme_image,Boolean Available) {
        this.theme_name = theme_name;
        this.theme_price = theme_price;
        this.theme_image = theme_image;
        this.Available=Available;
    }
}
