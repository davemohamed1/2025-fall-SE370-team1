package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NotificationScheduler {

    /**
     * Schedule a local notification for an Event minutesBefore minutes before the event time.
     * minutesBefore: number of minutes before the event (e.g. 1440, 60, 10). If <= 0, no scheduling.
     * Returns true if scheduled, false otherwise (e.g. if time already passed).
     */
    public static boolean scheduleNotification(Context ctx, Event event, int minutesBefore) {
        if (event == null || event.getDate() == null || event.getTime() == null || minutesBefore <= 0) return false;

        long eventMillis = parseEventDateTimeMillis(event.getDate(), event.getTime());
        if (eventMillis <= 0) return false;

        long triggerAt = eventMillis - minutesBefore * 60L * 1000L;
        long now = System.currentTimeMillis();
        if (triggerAt <= now) return false;

        Intent i = new Intent(ctx, NotificationReceiver.class);
        String title = event.getName() != null ? event.getName() : "Event";
        String text = (event.getClubName() != null ? event.getClubName() + " — " : "") +
                event.getDate() + " " + event.getTime() + " @ " + (event.getLocation() != null ? event.getLocation() : "");
        i.putExtra("title", title);
        i.putExtra("text", text);
        int notifId = Math.abs((event.hashCode() ^ minutesBefore));
        i.putExtra("notif_id", notifId);

        PendingIntent pi = PendingIntent.getBroadcast(ctx, notifId, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return false;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        }
        return true;
    }

    // Parse event date (yyyy-MM-dd) and time (various formats used in the app) into millis.
    private static long parseEventDateTimeMillis(String dateYmd, String timeStr) {
        try {
            // Combine and try a few common formats
            String combined = dateYmd + " " + timeStr;
            String[] patterns = {
                    "yyyy-MM-dd h:mm a",   // example: 2025-12-04 3:05 PM
                    "yyyy-MM-dd hh:mm a",
                    "yyyy-MM-dd H:mm",     // 24-hour fallback
                    "yyyy-MM-dd HH:mm"
            };
            for (String p : patterns) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(p, Locale.US);
                    sdf.setLenient(false);
                    sdf.setTimeZone(TimeZone.getDefault());
                    Date d = sdf.parse(combined);
                    if (d != null) return d.getTime();
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return -1;
    }
}
