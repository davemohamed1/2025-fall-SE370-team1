package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventListFragment extends Fragment {

    public EventListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        updateList(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        if (view != null) updateList(view);
    }

    private void updateList(View root) {
        LinearLayout container = root.findViewById(R.id.eventsContainer);
        container.removeAllViews();

        List<Event> events = new ArrayList<>(EventRepository.getAllEvents());
        if (events.isEmpty()) {
            Button placeholder = new Button(requireContext());
            placeholder.setText("No events yet. Create one!");
            placeholder.setEnabled(false);
            container.addView(placeholder);
            return;
        }

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                String dt1 = e1.getDate() + " " + e1.getTime();
                String dt2 = e2.getDate() + " " + e2.getTime();
                try {
                    Date d1 = sdf.parse(dt1);
                    Date d2 = sdf.parse(dt2);
                    if (d1 == null || d2 == null) {
                        return dt1.compareTo(dt2);
                    }
                    return d1.compareTo(d2);
                } catch (ParseException ex) {
                    return dt1.compareTo(dt2);
                }
            }
        });

        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            String clubLine = e.getClubName() != null ? e.getClubName() + "\n" : "";
            Button item = new Button(requireContext());
            item.setText(clubLine + e.getName() + "\n" + e.getDate() + " " + e.getTime() + "\n" + e.getLocation());
            item.setAllCaps(false);
            item.setOnClickListener(v -> {
                if (UserSession.isStudent()) {
                    // student: show dialog with Add to App Calendar (then ask notification preference)
                    String details = (e.getClubName() != null ? e.getClubName() + "\n" : "") +
                            e.getName() + "\n" + e.getDate() + " " + e.getTime() + "\n" + e.getLocation();
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Event")
                            .setMessage(details)
                            .setPositiveButton("Add to App Calendar", (d, w) -> {
                                boolean added = MyCalendarRepository.addEvent(requireContext(), e);
                                Toast.makeText(requireContext(),
                                        added ? "Added to app calendar." : "Already in app calendar.",
                                        Toast.LENGTH_SHORT).show();
                                if (added) promptNotificationChoice(e);
                            })
                            .setNegativeButton("Close", null)
                            .show();
                } else {
                    // organizer/advisor: open editor for the actual repository index
                    List<Event> repoEvents = EventRepository.getAllEvents();
                    int repoIndex = repoEvents.indexOf(e);
                    EventCreateFragment frag = (repoIndex >= 0)
                            ? EventCreateFragment.newInstance(repoIndex)
                            : EventCreateFragment.newInstance(-1);

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, frag)
                            .addToBackStack(null)
                            .commit();
                }
            });
            container.addView(item);
        }
    }

    private void promptNotificationChoice(Event e) {
        CharSequence[] options = new CharSequence[] {
                "No notification",
                "24 hours before",
                "1 hour before",
                "10 minutes before"
        };
        new AlertDialog.Builder(requireContext())
                .setTitle("Notify me")
                .setItems(options, (dialog, which) -> {
                    int minutes = 0;
                    switch (which) {
                        case 1: minutes = 24 * 60; break;
                        case 2: minutes = 60; break;
                        case 3: minutes = 10; break;
                        default: minutes = 0; break;
                    }
                    if (minutes > 0) {
                        boolean scheduled = NotificationScheduler.scheduleNotification(requireContext(), e, minutes);
                        Toast.makeText(requireContext(),
                                scheduled ? "Notification scheduled." : "Could not schedule notification (time may have passed).",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
