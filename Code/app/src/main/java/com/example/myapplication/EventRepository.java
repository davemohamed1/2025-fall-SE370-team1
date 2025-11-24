package com.example.myapplication;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private static final List<Event> events = new ArrayList<>();
    private static final String TAG = "EventRepository";

    public static void addEvent(Event event) {
        // keep in-memory for current app session
        events.add(event);

        // persist to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .add(event)
                .addOnSuccessListener(docRef -> Log.d(TAG, "Event saved to Firestore: " + docRef.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Failed to save event to Firestore", e));
    }

    public static void updateEvent(int index, Event event) {
        if (index >= 0 && index < events.size()) {
            events.set(index, event);
            // optionally update Firestore document if you store the doc ID (not implemented here)
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
