// File: `app/src/main/java/com/example/myapplication/EventCreateFragment.java`
package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventCreateFragment extends Fragment {

    private EditText nameInput, dateInput, timeInput, locationInput;
    private Spinner clubSpinner;
    private Button saveButton;
    private static final String ARG_EVENT_INDEX = "event_index";
    private int eventIndex = -1;
    private final List<Club> clubs = new ArrayList<>();
    private String dateKey = null;

    public static EventCreateFragment newInstance(int index) {
        EventCreateFragment fragment = new EventCreateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventIndex = getArguments().getInt(ARG_EVENT_INDEX, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_create, container, false);

        // Prevent students from opening the editor
        if (UserSession.isStudent()) {
            Toast.makeText(requireContext(), "Students cannot create or edit events.", Toast.LENGTH_SHORT).show();
            if (getParentFragmentManager() != null) getParentFragmentManager().popBackStack();
            return view;
        }

        nameInput = view.findViewById(R.id.eventName);
        dateInput = view.findViewById(R.id.eventDate);
        timeInput = view.findViewById(R.id.eventTime);
        locationInput = view.findViewById(R.id.eventLocation);
        saveButton = view.findViewById(R.id.saveButton);
        clubSpinner = view.findViewById(R.id.clubSpinner);

        // Load clubs into spinner
        loadClubs();

        if (clubs.isEmpty()) {
            saveButton.setEnabled(false);
            new AlertDialog.Builder(requireContext())
                    .setTitle("No clubs")
                    .setMessage("Please create a club before creating an event.")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            saveButton.setEnabled(true);
        }

        // If editing an existing event, prefill fields
        if (eventIndex >= 0) {
            Event existing = EventRepository.getEvent(eventIndex);
            if (existing != null) {
                nameInput.setText(existing.getName());
                // convert yyyy-MM-dd to MM/dd/yyyy for display
                try {
                    String existingDate = existing.getDate();
                    if (existingDate != null && !existingDate.isEmpty()) {
                        SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        Date d = in.parse(existingDate);
                        if (d != null) {
                            SimpleDateFormat out = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                            dateInput.setText(out.format(d));
                            dateKey = existingDate;
                        }
                    }
                } catch (Exception ignore) { /* leave date empty on parse failure */ }

                timeInput.setText(existing.getTime());
                locationInput.setText(existing.getLocation());

                // select club in spinner if available
                String clubId = existing.getClubId();
                String clubName = existing.getClubName();
                int sel = -1;
                for (int i = 0; i < clubs.size(); i++) {
                    Club c = clubs.get(i);
                    if ((clubId != null && clubId.equals(c.getId()))
                            || (clubName != null && clubName.equals(c.getName()))) {
                        sel = i;
                        break;
                    }
                }
                if (sel >= 0) clubSpinner.setSelection(sel);
            }
        }

        Button createClubButton = view.findViewById(R.id.btn_create_club);
        createClubButton.setOnClickListener(v -> {
            ClubCreateFragment frag = new ClubCreateFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, frag)
                    .addToBackStack(null)
                    .commit();
        });

        dateInput.setFocusable(false);
        dateInput.setClickable(true);
        dateInput.setOnClickListener(v -> showDatePicker());

        timeInput.setFocusable(false);
        timeInput.setClickable(true);
        timeInput.setOnClickListener(v -> showTimePicker());

        saveButton.setOnClickListener(v -> saveEvent());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh clubs in case a new club was created
        loadClubs();
        // enable save if clubs now exist
        if (saveButton != null) saveButton.setEnabled(!clubs.isEmpty());
    }

    private void loadClubs() {
        clubs.clear();
        clubs.addAll(ClubRepository.getAllClubs());

        List<String> names = new ArrayList<>();
        for (Club c : clubs) {
            names.add(c.getName() != null ? c.getName() : "(Unnamed)");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubSpinner.setAdapter(adapter);
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (DatePicker view, int y, int m, int d) -> {
                    String display = String.format(Locale.US, "%02d/%02d/%04d", m + 1, d, y);
                    dateInput.setText(display);
                    dateKey = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (TimePicker view, int hourOfDay, int minute1) -> {
                    String amPm = (hourOfDay >= 12) ? "PM" : "AM";
                    int hourTwelve = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
                    if (hourTwelve == 0) hourTwelve = 12;
                    String minuteStr = (minute1 < 10) ? "0" + minute1 : String.valueOf(minute1);
                    timeInput.setText(hourTwelve + ":" + minuteStr + " " + amPm);
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void saveEvent() {
        String name = nameInput.getText().toString().trim();
        String dateDisplay = dateInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        if (name.isEmpty() || dateDisplay.isEmpty() || time.isEmpty() || location.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String useDateKey = dateKey;
        if (useDateKey == null || useDateKey.isEmpty()) {
            try {
                SimpleDateFormat in = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Date parsed = in.parse(dateDisplay);
                SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                useDateKey = out.format(parsed);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Invalid date. Please pick a date.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (clubs.isEmpty()) {
            Toast.makeText(getActivity(), "Please create a club before creating an event.", Toast.LENGTH_SHORT).show();
            return;
        }

        String clubId = null;
        String clubName = null;
        int selected = clubSpinner.getSelectedItemPosition();
        if (selected >= 0 && selected < clubs.size()) {
            Club selectedClub = clubs.get(selected);
            clubId = selectedClub.getId();
            clubName = selectedClub.getName();
        }

        Event event = new Event(name, time, location, useDateKey, clubId, clubName);

        if (eventIndex >= 0) {
            // update existing
            Event old = EventRepository.getEvent(eventIndex);
            EventRepository.updateEvent(eventIndex, event);
            // update app calendar saved copy if present
            MyCalendarRepository.replaceEvent(requireContext(), old, event);
            Toast.makeText(getActivity(), "Event Updated!", Toast.LENGTH_SHORT).show();
        } else {
            // new event
            EventRepository.addEvent(event);
            // Ensure advisors see events on their app calendar immediately.
            MyCalendarRepository.addEvent(requireContext(), event);
            Toast.makeText(getActivity(), "Event Created!", Toast.LENGTH_SHORT).show();
        }

        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }
}
