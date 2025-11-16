// java
package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private static final List<Event> events = new ArrayList<>();

    public static void addEvent(Event event) {
        events.add(event);
    }

    public static void updateEvent(int index, Event event) {
        if (index >= 0 && index < events.size()) {
            events.set(index, event);
        }
    }

    public static Event getEvent(int index) {
        if (index >= 0 && index < events.size()) {
            return events.get(index);
        }
        return null;
    }

    public static List<Event> getAllEvents() {
        return new ArrayList<>(events);
    }

    // New helper: return events matching a specific date (yyyy-MM-dd)
    public static List<Event> getAllEventsForDate(String date) {
        List<Event> result = new ArrayList<>();
        if (date == null) return result;
        for (Event e : events) {
            if (date.equals(e.getDate())) {
                result.add(e);
            }
        }
        return result;
    }
}
