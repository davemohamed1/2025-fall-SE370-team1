package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;

public class CalendarFragment extends Fragment {

    private TextView monthTitle;
    private GridView gridView;
    private Calendar monthCalendar;

    public CalendarFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        monthTitle = view.findViewById(R.id.monthTitle);
        gridView = view.findViewById(R.id.calendarGrid);

        Button btnPrev = view.findViewById(R.id.btnPrev);
        Button btnNext = view.findViewById(R.id.btnNext);

        monthCalendar = Calendar.getInstance();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        btnPrev.setOnClickListener(v -> {
            monthCalendar.add(Calendar.MONTH, -1);
            refreshCalendar();
        });

        btnNext.setOnClickListener(v -> {
            monthCalendar.add(Calendar.MONTH, 1);
            refreshCalendar();
        });

        gridView.setOnItemClickListener((parent, cellView, position, id) -> {
            Object item = gridView.getAdapter().getItem(position);
            if (item == null) return;
            String key = item.toString(); // yyyy-MM-dd

            List<Event> events = new ArrayList<>();
            if (UserSession.isStudent()) {
                // students only see their saved app events
                List<Event> saved = MyCalendarRepository.getMyEvents(requireContext());
                for (Event e : saved) if (key.equals(e.getDate())) events.add(e);
            } else {
                // advisors/organizers: show repository events (plus saved ones without dupes)
                List<Event> repo = EventRepository.getAllEventsForDate(key);
                events.addAll(repo);
                // also include saved events not already present
                List<Event> saved = MyCalendarRepository.getMyEvents(requireContext());
                for (Event s : saved) {
                    if (!key.equals(s.getDate())) continue;
                    boolean found = false;
                    for (Event r : events) {
                        if (equalsEventLocal(r, s)) { found = true; break; }
                    }
                    if (!found) events.add(s);
                }
            }

            if (events.isEmpty()) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(key)
                        .setMessage("No events for this day.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            CharSequence[] items = new CharSequence[events.size()];
            for (int i = 0; i < events.size(); i++) {
                Event e = events.get(i);
                String clubLine = e.getClubName() != null ? e.getClubName() + "\n" : "";
                items[i] = clubLine + e.getName() + " — " + e.getTime() + "\n" + e.getLocation();
            }

            new AlertDialog.Builder(requireContext())
                    .setTitle(key)
                    .setItems(items, (dialog, which) -> {
                        Event chosen = events.get(which);
                        String details = (chosen.getClubName() != null ? chosen.getClubName() + "\n" : "") +
                                chosen.getName() + "\n" + chosen.getDate() + " " + chosen.getTime() + "\n" + chosen.getLocation();

                        if (UserSession.isStudent()) {
                            // allow student to remove from their app calendar
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Saved Event")
                                    .setMessage(details)
                                    .setPositiveButton("Remove from App Calendar", (d, w) -> {
                                        MyCalendarRepository.replaceEvent(requireContext(), chosen, null);
                                        Toast.makeText(requireContext(), "Removed from app calendar.", Toast.LENGTH_SHORT).show();
                                        refreshCalendar();
                                    })
                                    .setNegativeButton("Close", null)
                                    .show();
                        } else {
                            // advisors: read-only view
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Event")
                                    .setMessage(details)
                                    .setPositiveButton("Close", null)
                                    .show();
                        }
                    })
                    .setNegativeButton("Close", null)
                    .show();
        });

        refreshCalendar();
        return view;
    }

    private void refreshCalendar() {
        Map<String, List<Event>> eventsMap = new HashMap<>();

        if (UserSession.isStudent()) {
            // only events the student saved in the app calendar
            List<Event> saved = MyCalendarRepository.getMyEvents(requireContext());
            for (Event e : saved) {
                String dateStr = e.getDate();
                if (dateStr == null || dateStr.isEmpty()) continue;
                List<Event> list = eventsMap.get(dateStr);
                if (list == null) {
                    list = new ArrayList<>();
                    eventsMap.put(dateStr, list);
                }
                list.add(e);
            }
        } else {
            // advisors/organizers: show repository events and include saved events without duplicates
            List<Event> repoAll = EventRepository.getAllEvents();
            for (Event e : repoAll) {
                String dateStr = e.getDate();
                if (dateStr == null || dateStr.isEmpty()) continue;
                List<Event> list = eventsMap.get(dateStr);
                if (list == null) {
                    list = new ArrayList<>();
                    eventsMap.put(dateStr, list);
                }
                list.add(e);
            }
            List<Event> saved = MyCalendarRepository.getMyEvents(requireContext());
            for (Event s : saved) {
                String dateStr = s.getDate();
                if (dateStr == null || dateStr.isEmpty()) continue;
                List<Event> list = eventsMap.get(dateStr);
                if (list == null) {
                    list = new ArrayList<>();
                    eventsMap.put(dateStr, list);
                    list.add(s);
                } else {
                    boolean found = false;
                    for (Event r : list) {
                        if (equalsEventLocal(r, s)) { found = true; break; }
                    }
                    if (!found) list.add(s);
                }
            }
        }

        updateTitle();
        CalendarAdapter adapter = new CalendarAdapter(requireContext(), monthCalendar, eventsMap);
        gridView.setAdapter(adapter);
    }

    private void updateTitle() {
        String title = new SimpleDateFormat("MMMM yyyy", Locale.US).format(monthCalendar.getTime());
        monthTitle.setText(title);
    }

    // local equality matching name/date/time/location (mirrors MyCalendarRepository.equalsEvent)
    private boolean equalsEventLocal(Event a, Event b) {
        if (a == null || b == null) return false;
        return safeEq(a.getName(), b.getName())
                && safeEq(a.getDate(), b.getDate())
                && safeEq(a.getTime(), b.getTime())
                && safeEq(a.getLocation(), b.getLocation());
    }

    private boolean safeEq(String x, String y) {
        if (x == null && y == null) return true;
        if (x == null || y == null) return false;
        return x.equals(y);
    }
}
