<<<<<<< HEAD
package com.example.eventdetails_java;
=======
package com.example.myapplication;
>>>>>>> b37682dbe98402d3f87e8dfbe534ceefe9f7dfc3

public class Event {
    private String name;
    private String time;
    private String location;
    private String date;

    public Event(String name, String time, String location, String date) {
        this.name = name;
        this.time = time;
        this.location = location;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
