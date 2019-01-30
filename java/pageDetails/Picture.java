package com.example.maxime.tripshare.pageDetails;

public class Picture {

    int id;
    String picture;
    int idStep;

    public Picture(int id, String picture, int idStep) {
        this.id = id;
        this.picture = picture;
        this.idStep = idStep;
    }

    public Picture(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getIdStep() {
        return idStep;
    }

    public void setIdStep(int idStep) {
        this.idStep = idStep;
    }
}
