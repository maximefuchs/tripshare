package com.example.maxime.tripshare.pageTrips;


public class Trip {

  int id;
  String city;
  String country;
  String date;
  String endDate;
  String endCity;
  String endCountry;
  String pic;
  int userId;
  String creator;
  String picCreator;


  public Trip(String city, String country, String date, String endDate, String endCity, String endCountry, String pic, int userId, String creator, String picCreator) {
    this.city = city;
    this.country = country;
    this.date = date;
    this.endDate = endDate;
    this.endCity = endCity;
    this.endCountry = endCountry;
    this.pic = pic;
    this.userId = userId;
    this.creator = creator;
    this.picCreator = picCreator;
  }

  public Trip(String city, String country, String date, String endCountry, String pic, int userId, String creator, String picCreator) {
    this.city = city;
    this.country = country;
    this.date = date;
    this.endCountry = endCountry;
    this.pic = pic;
    this.userId = userId;
    this.creator = creator;
    this.picCreator = picCreator;
  }

  public Trip(){}

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getPic() {
    return pic;
  }

  public void setPic(String pic) {
    this.pic = pic;
  }

  public String getEndCity() {
    return endCity;
  }

  public void setEndCity(String endCity) {
    this.endCity = endCity;
  }

  public String getEndCountry() {
    return endCountry;
  }

  public void setEndCountry(String endCountry) {
    this.endCountry = endCountry;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getPicCreator() {
    return picCreator;
  }

  public void setPicCreator(String picCreator) {
    this.picCreator = picCreator;
  }

}
