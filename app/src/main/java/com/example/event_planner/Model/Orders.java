package com.example.event_planner.Model;
import java.util.ArrayList;

public class Orders {
 public Orders() {

 }
 public Orders(int number_of_guests, int foods_price, int services_price, int theme_price, String date, String start_time, String theme, String status, ArrayList<Food> foods, ArrayList<Services> services,String customer_id,String hotel_id,String hotel_name,String user_email,String user_phone,ArrayList<Halls> halls,int hallsRent,String partyType) {
  this.number_of_guests = number_of_guests;
  this.foods_price = foods_price;
  this.services_price = services_price;
  this.theme_price = theme_price;
  this.date = date;
  this.event_time = start_time;
  this.theme = theme;
  this.status = status;
  this.foods = foods;
  this.services = services;
  this.customer_id=customer_id;
  this.hotel_id=hotel_id;
  this.hotel_name=hotel_name;
  this.user_email=user_email;
  this.user_phone=user_phone;
  this.halls=halls;
  this.hallsRent=hallsRent;
  this.partyType=partyType;
 }

 public int getNumber_of_guests() {
  return number_of_guests;
 }

 public void setNumber_of_guests(int number_of_guests) {
  this.number_of_guests = number_of_guests;
 }

 public int getFoods_price() {
  return foods_price;
 }

 public void setFoods_price(int foods_price) {
  this.foods_price = foods_price;
 }

 public int getServices_price() {
  return services_price;
 }

 public void setServices_price(int services_price) {
  this.services_price = services_price;
 }

 public int getTheme_price() {
  return theme_price;
 }

 public void setTheme_price(int theme_price) {
  this.theme_price = theme_price;
 }

 public String getDate() {
  return date;
 }

 public void setDate(String date) {
  this.date = date;
 }

 public String getTheme() {
  return theme;
 }

 public void setTheme(String theme) {
  this.theme = theme;
 }

 public String getStatus() {
  return status;
 }

 public void setStatus(String status) {
  this.status = status;
 }

 public ArrayList<Food> getFoods() {
  return foods;
 }

 public void setFoods(ArrayList<Food> foods) {
  this.foods = foods;
 }

 public ArrayList<Services> getServices() {
  return services;
 }

 public void setServices(ArrayList<Services> services) {
  this.services = services;
 }

 int number_of_guests;
 int foods_price,services_price,theme_price;
 String date;
 String event_time;

 public String getEvent_time() {
  return event_time;
 }

 public void setEvent_time(String event_time) {
  this.event_time = event_time;
 }

 String theme;
 String status;

 public String getCustomer_id() {
  return customer_id;
 }

 public void setCustomer_id(String customer_id) {
  this.customer_id = customer_id;
 }

 public String getHotel_id() {
  return hotel_id;
 }

 public void setHotel_id(String hotel_id) {
  this.hotel_id = hotel_id;
 }

 public String getHotel_name() {
  return hotel_name;
 }

 public void setHotel_name(String hotel_name) {
  this.hotel_name = hotel_name;
 }

 String hotel_id;
 String hotel_name;
 String customer_id;
 ArrayList<Food> foods;
 ArrayList<Services> services;

 public ArrayList<Halls> getHalls() {
  return halls;
 }

 public void setHalls(ArrayList<Halls> halls) {
  this.halls = halls;
 }

 ArrayList<Halls> halls;
 int hallsRent;

 public int getHallsRent() {
  return hallsRent;
 }

 public void setHallsRent(int hallsRent) {
  this.hallsRent = hallsRent;
 }

 public String getUser_email() {
  return user_email;
 }

 public void setUser_email(String user_email) {
  this.user_email = user_email;
 }

 public String getUser_phone() {
  return user_phone;
 }

 public void setUser_phone(String user_phone) {
  this.user_phone = user_phone;
 }

 String user_email;
 String user_phone;

 public String getPartyType() {
  return partyType;
 }

 public void setPartyType(String partyType) {
  this.partyType = partyType;
 }

 String partyType;
}
