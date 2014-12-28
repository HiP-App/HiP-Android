package com.example.timo.hip;

public class Exhibit {

    public int id;
    public String name;
    public String description;
    public double lat;
    public double lng;
    public String[] categories;
    public String[] tags;

    public Exhibit (int id, String name, String description, double lat, double lng, String categories, String tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.categories = categories.split(",");
        this.tags = tags.split(",");
    }
}
