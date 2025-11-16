// File: `app/src/main/java/com/example/eventdetails_java/EventCreateFragment.java`
package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class EventCreateFragment extends Fragment {

    private static final String ARG_EVENT_INDEX = "event_index";

    private EditText nameEt;
    private EditText dateEt;
    private EditText timeEt;
    private EditText locationEt;
    private Button saveBtn;

    private int editingIndex = -1;

    public EventCreateFragment() {
        // Required empty public constructor
    }

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

        if (getArguments() != null) {
            editingIndex = getArguments().getInt(ARG_EVENT_INDEX, -1);
            if (editingIndex >= 0) {
                Event e = EventRepository.getEvent(editingIndex);
                if (e != null) {
                    nameEt.setText(e.getName());
                    dateEt.setText(e.getDate());
                    timeEt.setText(e.getTime());
                    locationEt.setText(e.getLocation());
                }
            }
        }

        dateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(android.widget.DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                String formatted = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                                dateEt.setText(formatted);
                            }
                        },
                        year, month, day
                );
                datePicker.show();
            }
        });

        timeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog timePicker = new TimePickerDialog(
                        requireContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(android.widget.TimePicker view, int selectedHour, int selectedMinute) {
                                String formatted = String.format("%02d:%02d", selectedHour, selectedMinute);
                                timeEt.setText(formatted);
                            }
                        },
                        hour,
                        minute,
                        false
                );
                timePicker.show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEt.getText().toString().trim();
                String date = dateEt.getText().toString().trim();
                String time = timeEt.getText().toString().trim();
                String location = locationEt.getText().toString().trim();

                if (name.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty()) {
                    Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editingIndex >= 0) {
                    // update existing
                    EventRepository.updateEvent(editingIndex, new Event(name, time, location, date));
                } else {
                    EventRepository.addEvent(new Event(name, time, location, date));
                }

                Toast.makeText(requireContext(), "Event saved", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
