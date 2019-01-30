package com.example.maxime.tripshare.pageSteps;

import org.osmdroid.util.GeoPoint;

public class Step {

    int id;
    String name;
    String date;
    GeoPoint geo;
    String coverPic;
    String story;
    int idTrip;

    public Step(int id, String name, String date, GeoPoint geo, String coverPic, int idTrip) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.geo = geo;
        this.coverPic = coverPic;
        this.idTrip = idTrip;
    }

    public Step(String name, String date, GeoPoint geo, String coverPic, String story, int idTrip) {
        this.name = name;
        this.date = date;
        this.geo = geo;
        this.coverPic = coverPic;
        this.story = story;
        this.idTrip = idTrip;
    }

    public Step(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public GeoPoint getGeo() {
        return geo;
    }

    public void setGeo(GeoPoint geo) {
        this.geo = geo;
    }

    public String getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    public int getIdTrip() {
        return idTrip;
    }

    public void setIdTrip(int idTrip) {
        this.idTrip = idTrip;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }
}
