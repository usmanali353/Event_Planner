package com.example.event_planner.Model;

public class approvalRequest {
    String requestDate;
    String serviceProviderName;
    String address;
    String Phone;
    String email;
    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    String hotelId;

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getServiceProviderName() {
        return serviceProviderName;
    }

    public void setServiceProviderName(String serviceProviderName) {
        this.serviceProviderName = serviceProviderName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public approvalRequest(String requestDate, String serviceProviderName, String address, String phone, String email,String hotelId) {
        this.requestDate = requestDate;
        this.serviceProviderName = serviceProviderName;
        this.address = address;
        this.Phone = phone;
        this.email = email;
        this.hotelId=hotelId;
    }
    public approvalRequest(){

    }
}
