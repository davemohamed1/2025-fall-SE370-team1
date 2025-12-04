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

import java.util.ArrayList;
import java.util.List;

public class ClubListFragment extends Fragment {

    public ClubListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_club_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LinearLayout container = view.findViewById(R.id.clubsContainer);
        container.removeAllViews();

        List<Club> clubs = ClubRepository.getAllClubs();
        if (clubs.isEmpty()) {
            Button placeholder = new Button(requireContext());
            placeholder.setText("No clubs yet. Create one!");
            placeholder.setEnabled(false);
            container.addView(placeholder);
            return;
        }

        for (Club c : clubs) {
            Button b = new Button(requireContext());
            b.setAllCaps(false);
            b.setText(c.getName() != null ? c.getName() : "(Unnamed)");
            b.setOnClickListener(v -> showClubEvents(c));
            container.addView(b);
        }
    }

    private void showClubEvents(Club club) {
        List<Event> all = EventRepository.getAllEvents();
        final List<Event> clubEvents = new ArrayList<>();
        for (Event e : all) {
            if ((club.getId() != null && club.getId().equals(e.getClubId()))
                    || (club.getName() != null && club.getName().equals(e.getClubName()))) {
                clubEvents.add(e);
            }
        }

        if (clubEvents.isEmpty()) {
            Toast.makeText(requireContext(), "No events for this club.", Toast.LENGTH_SHORT).show();
            return;
        }

        CharSequence[] items = new CharSequence[clubEvents.size()];
        for (int i = 0; i < clubEvents.size(); i++) {
            Event ev = clubEvents.get(i);
            String clubLine = ev.getClubName() != null ? ev.getClubName() + "\n" : "";
            items[i] = clubLine + ev.getName() + " — " + ev.getTime() + "\n" + ev.getLocation() + " (" + ev.getDate() + ")";
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(club.getName())
                .setItems(items, (dialog, which) -> {
                    Event chosen = clubEvents.get(which);
                    if (UserSession.isStudent()) {
                        boolean added = MyCalendarRepository.addEvent(requireContext(), chosen);
                        Toast.makeText(requireContext(), added ? "Added to app calendar." : "Already in app calendar.", Toast.LENGTH_SHORT).show();
                        if (added) promptNotificationChoice(chosen);
                    } else {
                        List<Event> allEvents = EventRepository.getAllEvents();
                        int index = allEvents.indexOf(chosen);
                        if (index >= 0) {
                            EventCreateFragment frag = EventCreateFragment.newInstance(index);
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, frag)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                })
                .setNegativeButton("Close", null)
                .show();
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
