package com.example.myapplication;

public class Club {
    private String id;
    private String name;

    // required by Firestore
    public Club() {}

    public Club(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Club(String name) {
        this.name = name;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
