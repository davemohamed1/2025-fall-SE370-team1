package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventCreateFragment extends Fragment {

    private static final String ARG_EVENT_INDEX = "event_index";

    private EditText nameEt;
    private EditText dateEt;
    private EditText timeEt;
    private EditText locationEt;
    private Button saveBtn;
    private Spinner clubSpinner;
    private Button createClubBtn;

    private int editingIndex = -1;
    private List<Club> clubs = new ArrayList<>();
    private int selectedClubPosition = -1;

    public EventCreateFragment() {}

    public static EventCreateFragment newInstance(int index) {
        EventCreateFragment fragment = new EventCreateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_create, container, false);

        nameEt = view.findViewById(R.id.eventName);
        dateEt = view.findViewById(R.id.eventDate);
        timeEt = view.findViewById(R.id.eventTime);
        locationEt = view.findViewById(R.id.eventLocation);
        saveBtn = view.findViewById(R.id.saveButton);
        clubSpinner = view.findViewById(R.id.clubSpinner);
        createClubBtn = view.findViewById(R.id.btn_create_club);

        // Always allow creating clubs / saving UI-wise (no advisor/student gating)
        saveBtn.setEnabled(true);
        saveBtn.setAlpha(1.0f);
        createClubBtn.setEnabled(true);

        loadClubsIntoSpinner();

        createClubBtn.setOnClickListener(v -> showCreateClubDialog());

        if (getArguments() != null) {
            editingIndex = getArguments().getInt(ARG_EVENT_INDEX, -1);
            if (editingIndex >= 0) {
                Event e = EventRepository.getEvent(editingIndex);
                if (e != null) {
                    nameEt.setText(e.getName());
                    dateEt.setText(e.getDate());
                    timeEt.setText(e.getTime());
                    locationEt.setText(e.getLocation());
                    // select club if present
                    if (e.getClubName() != null) {
                        for (int i = 0; i < clubs.size(); i++) {
                            if (e.getClubName().equals(clubs.get(i).getName())) {
                                clubSpinner.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }
        }

        dateEt.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String formatted = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        dateEt.setText(formatted);
                    },
                    year, month, day
            );
            datePicker.show();
        });

        timeEt.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(
                    requireContext(),
                    (view12, selectedHour, selectedMinute) -> {
                        String formatted = String.format("%02d:%02d", selectedHour, selectedMinute);
                        timeEt.setText(formatted);
                    },
                    hour,
                    minute,
                    false
            );
            timePicker.show();
        });

        saveBtn.setOnClickListener(v -> {
            if (clubs.isEmpty()) {
                Toast.makeText(requireContext(), "Please create a club first", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = nameEt.getText().toString().trim();
            String date = dateEt.getText().toString().trim();
            String time = timeEt.getText().toString().trim();
            String location = locationEt.getText().toString().trim();

            if (name.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty()) {
                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int pos = clubSpinner.getSelectedItemPosition();
            Club chosen = pos >= 0 && pos < clubs.size() ? clubs.get(pos) : null;
            String clubId = chosen != null ? chosen.getId() : null;
            String clubName = chosen != null ? chosen.getName() : null;

            if (editingIndex >= 0) {
                EventRepository.updateEvent(editingIndex, new Event(name, time, location, date, clubId, clubName));
            } else {
                EventRepository.addEvent(new Event(name, time, location, date, clubId, clubName));
            }

            Toast.makeText(requireContext(), "Event saved", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        });

        clubSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClubPosition = position;
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { selectedClubPosition = -1; }
        });

        return view;
    }

    private void loadClubsIntoSpinner() {
        clubs = ClubRepository.getAllClubs();
        List<String> names = new ArrayList<>();
        for (Club c : clubs) names.add(c.getName());
        if (names.isEmpty()) {
            names.add("No clubs - create one");
            clubSpinner.setEnabled(false);
        } else {
            clubSpinner.setEnabled(true);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubSpinner.setAdapter(adapter);
    }

    private void showCreateClubDialog() {
        final EditText input = new EditText(requireContext());
        new AlertDialog.Builder(requireContext())
                .setTitle("Create Club")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(requireContext(), "Club name required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Club c = new Club(name);
                    ClubRepository.addClub(c);
                    // reload spinner
                    loadClubsIntoSpinner();
                    // select the newly added club (last)
                    int last = Math.max(0, ClubRepository.getAllClubs().size() - 1);
                    clubSpinner.setSelection(last);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
