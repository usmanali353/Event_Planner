package com.example.event_planner.Model;

import java.io.Serializable;

public class Halls  {
    String name,hall_image_url;
    int price;
    Boolean Available;

    public Boolean getAvailable() {
        return Available;
    }

    public void setAvailable(Boolean available) {
        Available = available;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHall_image_url() {
        return hall_image_url;
    }

    public void setHall_image_url(String hall_image_url) {
        this.hall_image_url = hall_image_url;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Halls(String name, String hall_image_url, int price,boolean Available) {
        this.name = name;
        this.hall_image_url = hall_image_url;
        this.price = price;
        this.Available=Available;
    }

    public Halls() {

    }
}
