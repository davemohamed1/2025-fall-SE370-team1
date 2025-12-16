// language: java
// File: `app/src/main/java/com/example/myapplication/Event.java`
package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class Event {
    private String name;
    private String time;
    private String location;
    private String date;
    private String clubId;
    private String clubName;
    private List<String> hashtags; // new

    // Firestore requires a public no-arg constructor
    public Event() {}

    public Event(String name, String time, String location, String date, String clubId, String clubName) {
        this.name = name;
        this.time = time;
        this.location = location;
        this.date = date;
        this.clubId = clubId;
        this.clubName = clubName;
        this.hashtags = new ArrayList<>();
    }

    // convenience constructor for backward compatibility (will have empty club info)
    public Event(String name, String time, String location, String date) {
        this(name, time, location, date, null, null);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getClubId() { return clubId; }
    public void setClubId(String clubId) { this.clubId = clubId; }
    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    // hashtags
    public List<String> getHashtags() {
        if (hashtags == null) hashtags = new ArrayList<>();
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }
}
