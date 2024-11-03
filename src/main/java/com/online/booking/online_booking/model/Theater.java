package com.online.booking.online_booking.model;

import java.util.List;



public class Theater {
    private final String id;
    private final String name;
    private final String location;
    private final List<Show> shows;
    private String city;

    public Theater(String id, String name, String location, String city, List<Show> shows) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.shows = shows;
        this.city = city;
    }


    public String getCity() {
        return this.city;
    }

    public String getName() {
        return this.name;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
}
