// language: java
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
    // store canonical date key used by Calendar (yyyy-MM-dd)
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

        nameInput = view.findViewById(R.id.eventName);
        dateInput = view.findViewById(R.id.eventDate);
        timeInput = view.findViewById(R.id.eventTime);
        locationInput = view.findViewById(R.id.eventLocation);
        saveButton = view.findViewById(R.id.saveButton);
        clubSpinner = view.findViewById(R.id.clubSpinner);

        // Load clubs into spinner
        loadClubs();

        // If no clubs exist -> force user to create one before making an event
        if (clubs.isEmpty()) {
            saveButton.setEnabled(false);
            new AlertDialog.Builder(requireContext())
                    .setTitle("No clubs")
                    .setMessage("You must create a club before creating an event.")
                    .setPositiveButton("Create Club", (d, which) -> {
                        ClubCreateFragment frag = new ClubCreateFragment();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, frag)
                                .addToBackStack(null)
                                .commit();
                    })
                    .setNegativeButton("Cancel", (d, which) -> {
                        // go back to previous screen
                        if (getParentFragmentManager() != null) getParentFragmentManager().popBackStack();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            saveButton.setEnabled(true);
        }

        Button createClubButton = view.findViewById(R.id.btn_create_club);
        createClubButton.setOnClickListener(v -> {
            ClubCreateFragment frag = new ClubCreateFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, frag)
                    .addToBackStack(null)
                    .commit();
        });

        // restore date/time pickers
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
        saveButton.setEnabled(!clubs.isEmpty());
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
                    // Display to user as MM/dd/yyyy
                    String display = String.format(Locale.US, "%02d/%02d/%04d", m + 1, d, y);
                    dateInput.setText(display);
                    // Store canonical key as yyyy-MM-dd for calendar matching
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

        // Ensure we have a canonical date key (yyyy-MM-dd)
        String useDateKey = dateKey;
        if (useDateKey == null || useDateKey.isEmpty()) {
            // try to parse MM/dd/yyyy into yyyy-MM-dd
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

        // Must have a club selected (enforced earlier) - still guard here
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

        // store the canonical dateKey in Event so calendar can find it
        Event event = new Event(name, time, location, useDateKey, clubId, clubName);

        // Add to repository (in-memory + persist)
        EventRepository.addEvent(event);

        Toast.makeText(getActivity(), "Event Created!", Toast.LENGTH_SHORT).show();
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }
}
