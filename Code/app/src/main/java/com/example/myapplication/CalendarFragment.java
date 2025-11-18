package com.example.myapplication;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

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

    public CalendarFragment() {
    }

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

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthCalendar.add(Calendar.MONTH, -1);
                refreshCalendar();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthCalendar.add(Calendar.MONTH, 1);
                refreshCalendar();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View cellView, int position, long id) {
                Object item = gridView.getAdapter().getItem(position);
                if (item == null) return;
                String key = item.toString(); // yyyy-MM-dd

                // Try repository helper first
                List<Event> found = EventRepository.getAllEventsForDate(key);

                // Ensure a final list variable for use in the lambda
                final List<Event> events;
                if (found != null && !found.isEmpty()) {
                    events = found;
                } else {
                    // build list locally if repo returned empty
                    events = new ArrayList<>();
                    List<Event> all = EventRepository.getAllEvents();
                    for (Event e : all) {
                        if (key.equals(e.getDate())) events.add(e);
                    }
                }

                if (events.isEmpty()) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle(key)
                            .setMessage("No events")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }

                CharSequence[] items = new CharSequence[events.size()];
                for (int i = 0; i < events.size(); i++) {
                    Event e = events.get(i);
                    items[i] = e.getName() + " — " + e.getTime() + "\n" + e.getLocation();
                }

                new AlertDialog.Builder(requireContext())
                        .setTitle(key)
                        .setItems(items, (dialog, which) -> {
                            // open editor for selected event
                            Event chosen = events.get(which);
                            List<Event> all = EventRepository.getAllEvents();
                            int index = all.indexOf(chosen);
                            if (index >= 0) {
                                EventCreateFragment frag = EventCreateFragment.newInstance(index);
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, frag)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        })
                        .setNegativeButton("Close", null)
                        .show();
            }
        });

        refreshCalendar();
        return view;
    }

    private void refreshCalendar() {
        // Build map of date string -> list of events
        Map<String, List<Event>> eventsMap = new HashMap<>();
        List<Event> all = EventRepository.getAllEvents();
        for (Event e : all) {
            String dateStr = e.getDate(); // expected yyyy-MM-dd
            if (dateStr == null || dateStr.isEmpty()) continue;
            List<Event> list = eventsMap.get(dateStr);
            if (list == null) {
                list = new ArrayList<>();
                eventsMap.put(dateStr, list);
            }
            list.add(e);
        }

        updateTitle();
        CalendarAdapter adapter = new CalendarAdapter(requireContext(), monthCalendar, eventsMap);
        gridView.setAdapter(adapter);
    }

    private void updateTitle() {
        String title = new SimpleDateFormat("MMMM yyyy", Locale.US).format(monthCalendar.getTime());
        monthTitle.setText(title);
    }
}