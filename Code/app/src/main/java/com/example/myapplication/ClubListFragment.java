// language: java
// File: `app/src/main/java/com/example/myapplication/ClubListFragment.java`
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
            String label = c.getName() != null ? c.getName() : "(Unnamed)";
            // append tags if present
            List<String> tags = c.getHashtags();
            if (tags != null && !tags.isEmpty()) {
                label += "\nTags: " + joinTags(tags);
            }
            b.setText(label);
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
            String tagLine = "";
            List<String> t = ev.getHashtags();
            if (t != null && !t.isEmpty()) tagLine = "\nTags: " + joinTags(t);
            items[i] = clubLine + ev.getName() + " — " + ev.getTime() + "\n" + ev.getLocation() + " (" + ev.getDate() + ")" + tagLine;
        }

        AlertDialog.Builder bldr = new AlertDialog.Builder(requireContext())
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
                .setNegativeButton("Close", null);

        // for students, allow browsing by tag if the club has tags
        List<String> clubTags = club.getHashtags();
        if (UserSession.isStudent() && clubTags != null && !clubTags.isEmpty()) {
            bldr.setNeutralButton("View by Tag", (d, w) -> showTagChoices(clubTags));
        }

        bldr.show();
    }

    private void showTagChoices(List<String> tags) {
        CharSequence[] items = new CharSequence[tags.size()];
        for (int i = 0; i < tags.size(); i++) items[i] = tags.get(i);
        new AlertDialog.Builder(requireContext())
                .setTitle("Select Tag")
                .setItems(items, (dialog, which) -> showMatches(tags.get(which)))
                .setNegativeButton("Close", null)
                .show();
    }

    private void showMatches(String tag) {
        List<Club> matchingClubs = new ArrayList<>();
        for (Club c : ClubRepository.getAllClubs()) {
            if (c.getHashtags() != null && c.getHashtags().contains(tag)) matchingClubs.add(c);
        }
        List<Event> matchingEvents = new ArrayList<>();
        for (Event e : EventRepository.getAllEvents()) {
            if (e.getHashtags() != null && e.getHashtags().contains(tag)) matchingEvents.add(e);
        }

        List<CharSequence> lines = new ArrayList<>();
        for (Club c : matchingClubs) lines.add("[Club] " + (c.getName() != null ? c.getName() : "(Unnamed)"));
        for (Event e : matchingEvents) lines.add("[Event] " + e.getName() + " — " + e.getDate() + " " + e.getTime());

        if (lines.isEmpty()) {
            Toast.makeText(requireContext(), "No clubs or events with tag " + tag, Toast.LENGTH_SHORT).show();
            return;
        }

        CharSequence[] arr = lines.toArray(new CharSequence[0]);
        new AlertDialog.Builder(requireContext())
                .setTitle("Matches: " + tag)
                .setItems(arr, null)
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

    private String joinTags(List<String> tags) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(tags.get(i));
        }
        return sb.toString();
    }
}
