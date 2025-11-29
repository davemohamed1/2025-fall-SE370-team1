// language: java
package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Per-user in-app calendar storage. Events are saved in SharedPreferences keyed by the
 * current Firebase user's UID so students won't see advisor-saved events unless they add them.
 */
public class MyCalendarRepository {
    private static final String PREFS = "my_calendar_prefs";
    private static final String KEY = "saved_events_v1";
    private static final char SEP = '\u0001';

    // in-memory cache for the currently loaded user
    private static final List<Event> myEvents = new ArrayList<>();

    private static String prefsKeyForCurrentUser(Context ctx) {
        String uid = "anon";
        try {
            FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
            if (u != null && u.getUid() != null) uid = u.getUid();
        } catch (Exception ignored) {}
        return KEY + "_" + uid;
    }

    private static void load(Context ctx) {
        myEvents.clear();
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String key = prefsKeyForCurrentUser(ctx);
        Set<String> set = prefs.getStringSet(key, null);
        if (set == null) return;
        for (String enc : set) {
            try {
                String s = new String(Base64.decode(enc, Base64.NO_WRAP), "UTF-8");
                String[] parts = s.split(String.valueOf(SEP), -1);
                String name = emptyToNull(parts, 0);
                String time = emptyToNull(parts, 1);
                String location = emptyToNull(parts, 2);
                String date = emptyToNull(parts, 3);
                String clubId = emptyToNull(parts, 4);
                String clubName = emptyToNull(parts, 5);
                Event e = new Event(name, time, location, date, clubId, clubName);
                myEvents.add(e);
            } catch (Exception ignore) {
                // skip malformed entry
            }
        }
    }

    private static String emptyToNull(String[] parts, int idx) {
        if (idx >= parts.length) return null;
        return parts[idx].isEmpty() ? null : parts[idx];
    }

    private static String serialize(Event e) {
        StringBuilder sb = new StringBuilder();
        sb.append(nullToEmpty(e.getName())).append(SEP)
                .append(nullToEmpty(e.getTime())).append(SEP)
                .append(nullToEmpty(e.getLocation())).append(SEP)
                .append(nullToEmpty(e.getDate())).append(SEP)
                .append(nullToEmpty(e.getClubId())).append(SEP)
                .append(nullToEmpty(e.getClubName()));
        try {
            return Base64.encodeToString(sb.toString().getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (Exception ex) {
            return "";
        }
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }

    private static void persist(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String key = prefsKeyForCurrentUser(ctx);
        Set<String> set = new HashSet<>();
        for (Event e : myEvents) set.add(serialize(e));
        prefs.edit().putStringSet(key, set).apply();
    }

    public static synchronized boolean addEvent(Context ctx, Event e) {
        load(ctx);
        if (e == null) return false;
        if (isEventSaved(ctx, e)) return false;
        myEvents.add(e);
        persist(ctx);
        return true;
    }

    public static synchronized List<Event> getMyEvents(Context ctx) {
        load(ctx);
        return new ArrayList<>(myEvents);
    }

    public static synchronized boolean isEventSaved(Context ctx, Event e) {
        load(ctx);
        if (e == null) return false;
        for (Event ex : myEvents) {
            if (equalsEvent(ex, e)) return true;
        }
        return false;
    }

    private static boolean equalsEvent(Event a, Event b) {
        if (a == null || b == null) return false;
        return safeEq(a.getName(), b.getName())
                && safeEq(a.getDate(), b.getDate())
                && safeEq(a.getTime(), b.getTime())
                && safeEq(a.getLocation(), b.getLocation());
    }

    private static boolean safeEq(String x, String y) {
        if (x == null && y == null) return true;
        if (x == null || y == null) return false;
        return x.equals(y);
    }

    /**
     * Replace any saved event matching oldEvent with newEvent (used when advisor edits an event).
     * If oldEvent is not found, newEvent will be added if not already present.
     * Passing newEvent == null will remove matching saved events.
     */
    public static synchronized void replaceEvent(Context ctx, Event oldEvent, Event newEvent) {
        load(ctx);
        if (oldEvent != null) {
            List<Event> toRemove = new ArrayList<>();
            for (Event e : myEvents) {
                if (equalsEvent(e, oldEvent)) toRemove.add(e);
            }
            myEvents.removeAll(toRemove);
        }
        if (newEvent != null && !isEventSaved(ctx, newEvent)) {
            myEvents.add(newEvent);
        }
        persist(ctx);
    }
}
