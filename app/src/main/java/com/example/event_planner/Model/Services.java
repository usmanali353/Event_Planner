package com.example.event_planner.Model;

public class Services {
    String service_name,service_image_url;
    int service_price;
    Boolean Available;

    public Boolean getAvailable() {
        return Available;
    }

    public void setAvailable(Boolean available) {
        Available = available;
    }

    public Services() {

    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_image_url() {
        return service_image_url;
    }

    public void setService_image_url(String service_image_url) {
        this.service_image_url = service_image_url;
    }

    public int getService_price() {
        return service_price;
    }

    public void setService_price(int service_price) {
        this.service_price = service_price;
    }

    public Services(String service_name, int service_price, String service_image_url,Boolean Available) {
        this.service_name = service_name;
        this.service_price = service_price;
        this.service_image_url = service_image_url;
        this.Available=Available;
    }

}
