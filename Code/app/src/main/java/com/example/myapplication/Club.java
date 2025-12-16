// language: java
// File: `app/src/main/java/com/example/myapplication/Club.java`
package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class Club {
    private String id;
    private String name;
    private String description;
    private List<String> hashtags; // new

    // required by Firestore
    public Club() {}

    public Club(String name, String description) {
        this.name = name;
        this.description = description;
        this.hashtags = new ArrayList<>();
    }

    public Club(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.hashtags = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getHashtags() {
        if (hashtags == null) hashtags = new ArrayList<>();
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }
}
