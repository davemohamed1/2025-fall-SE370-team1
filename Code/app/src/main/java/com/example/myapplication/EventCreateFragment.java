package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class EventCreateFragment extends Fragment {

    private EditText nameInput, dateInput, timeInput, locationInput;
    private Button saveButton;
    private FirebaseFirestore db;
    // Add this key to store the index
    private static final String ARG_EVENT_INDEX = "event_index";
    private int eventIndex = -1;

    // This is the missing method causing the error
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

        db = FirebaseFirestore.getInstance();

        // These IDs now match your provided XML exactly
        nameInput = view.findViewById(R.id.eventName);
        dateInput = view.findViewById(R.id.eventDate);
        timeInput = view.findViewById(R.id.eventTime);
        locationInput = view.findViewById(R.id.eventLocation);
        saveButton = view.findViewById(R.id.saveButton);

        // Date Picker Logic
        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Time Picker Logic
        timeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        // Save Button Logic
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });

        return view;
    }

    private void showDatePicker() {
        // Requires: import java.util.Calendar;
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Requires: import android.app.DatePickerDialog;
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateInput.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        // Requires: import java.util.Calendar;
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Requires: import android.app.TimePickerDialog;
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String amPm = (hourOfDay >= 12) ? "PM" : "AM";
                        int hourTwelve = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
                        if (hourTwelve == 0) hourTwelve = 12;
                        String minuteStr = (minute < 10) ? "0" + minute : String.valueOf(minute);
                        timeInput.setText(hourTwelve + ":" + minuteStr + " " + amPm);
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void saveEvent() {
        String name = nameInput.getText().toString();
        String date = dateInput.getText().toString();
        String time = timeInput.getText().toString();
        String location = locationInput.getText().toString();

        if (name.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Make sure your Event.java constructor matches this order!
        Event event = new Event(name, time, location, date);

        db.collection("events")
                .add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "Event Created!", Toast.LENGTH_SHORT).show();
                        if (getParentFragmentManager() != null) {
                            getParentFragmentManager().popBackStack();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
