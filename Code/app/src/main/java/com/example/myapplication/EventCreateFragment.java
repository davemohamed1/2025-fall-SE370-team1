package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventCreateFragment extends Fragment {
    private static final String ARG_INDEX = "index";

    private int repoIndex = -1;

    private EditText eventName;
    private EditText eventDate;
    private EditText eventTime;
    private EditText eventLocation;
    private Spinner clubSpinner;
    private Button btnToggleTags;
    private TextView hashtagsLabel;
    private LinearLayout hashtagsContainer;
    private Button btnCreateClub;
    private Button saveButton;

    private final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm a", Locale.US);
    private final SimpleDateFormat[] parseTimeFormats = new SimpleDateFormat[] {
            new SimpleDateFormat("h:mm a", Locale.US),
            new SimpleDateFormat("hh:mm a", Locale.US),
            new SimpleDateFormat("H:mm", Locale.US),
            new SimpleDateFormat("HH:mm", Locale.US)
    };

    public EventCreateFragment() {}

    public static EventCreateFragment newInstance(int index) {
        EventCreateFragment f = new EventCreateFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_INDEX, index);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            repoIndex = getArguments().getInt(ARG_INDEX, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_create, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        eventName = view.findViewById(R.id.eventName);
        eventDate = view.findViewById(R.id.eventDate);
        eventTime = view.findViewById(R.id.eventTime);
        eventLocation = view.findViewById(R.id.eventLocation);
        clubSpinner = view.findViewById(R.id.clubSpinner);
        btnToggleTags = view.findViewById(R.id.btn_toggle_hashtags);
        hashtagsLabel = view.findViewById(R.id.hashtagsLabel);
        hashtagsContainer = view.findViewById(R.id.hashtagsContainer);
        btnCreateClub = view.findViewById(R.id.btn_create_club);
        saveButton = view.findViewById(R.id.saveButton);

        // populate club spinner
        List<Club> clubs = ClubRepository.getAllClubs();
        List<String> clubNames = new ArrayList<>();
        clubNames.add("(No club)");
        for (Club c : clubs) clubNames.add(c.getName() != null ? c.getName() : "(Unnamed)");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, clubNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubSpinner.setAdapter(adapter);

        // disable keyboard and show DatePicker on click/focus
        eventDate.setInputType(InputType.TYPE_NULL);
        eventDate.setOnClickListener(v -> showDatePicker());
        eventDate.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) showDatePicker(); });

        // disable keyboard and show TimePicker on click/focus
        eventTime.setInputType(InputType.TYPE_NULL);
        eventTime.setOnClickListener(v -> showTimePicker());
        eventTime.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) showTimePicker(); });

        // toggle hashtags container (advisors only UI already controlled by layout visibility)
        btnToggleTags.setOnClickListener(v -> {
            if (hashtagsContainer.getVisibility() == View.VISIBLE) {
                hashtagsContainer.setVisibility(View.GONE);
                hashtagsLabel.setVisibility(View.GONE);
                btnToggleTags.setText("Show Hashtags");
            } else {
                hashtagsContainer.setVisibility(View.VISIBLE);
                hashtagsLabel.setVisibility(View.VISIBLE);
                btnToggleTags.setText("Hide Hashtags");
                // populate checkboxes once if empty
                if (hashtagsContainer.getChildCount() == 0) {
                    String[] tagOptions = {"Sports","Learning","Relax","Gaming","Social","Outdoors"};
                    for (String t : tagOptions) {
                        CheckBox cb = new CheckBox(requireContext());
                        cb.setText(t);
                        hashtagsContainer.addView(cb);
                    }
                }
            }
        });

        btnCreateClub.setOnClickListener(v -> {
            ClubCreateFragment frag = new ClubCreateFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, frag)
                    .addToBackStack(null)
                    .commit();
        });

        // if editing an existing repo event, populate fields
        if (repoIndex >= 0) {
            Event existing = EventRepository.getEvent(repoIndex);
            if (existing != null) {
                eventName.setText(existing.getName());
                eventDate.setText(existing.getDate());
                eventTime.setText(existing.getTime());
                eventLocation.setText(existing.getLocation());
                // select club in spinner if names match
                if (existing.getClubName() != null) {
                    for (int i = 0; i < clubNames.size(); i++) {
                        if (existing.getClubName().equals(clubNames.get(i))) {
                            clubSpinner.setSelection(i);
                            break;
                        }
                    }
                }
                // populate hashtags if any
                List<String> h = existing.getHashtags();
                if (h != null && !h.isEmpty()) {
                    // ensure hashtags UI visible and check matching boxes
                    btnToggleTags.performClick();
                    for (int i = 0; i < hashtagsContainer.getChildCount(); i++) {
                        View child = hashtagsContainer.getChildAt(i);
                        if (child instanceof CheckBox) {
                            CheckBox cb = (CheckBox) child;
                            cb.setChecked(h.contains(cb.getText().toString()));
                        }
                    }
                }
            }
        }

        saveButton.setOnClickListener(v -> {
            String name = eventName.getText().toString().trim();
            String date = eventDate.getText().toString().trim();
            String time = eventTime.getText().toString().trim();
            String loc = eventLocation.getText().toString().trim();

            if (name.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter name, date and time.", Toast.LENGTH_SHORT).show();
                return;
            }

            // validate date format
            try {
                dateFmt.setLenient(false);
                Date d = dateFmt.parse(date);
                if (d == null) throw new ParseException("bad", 0);
            } catch (Exception ex) {
                Toast.makeText(requireContext(), "Date must be yyyy-MM-dd.", Toast.LENGTH_SHORT).show();
                return;
            }

            // normalize time to h:mm a
            String normTime = normalizeTimeString(time);
            if (normTime == null) {
                Toast.makeText(requireContext(), "Time not recognized.", Toast.LENGTH_SHORT).show();
                return;
            }

            // selected club (by name)
            String clubName = null;
            String clubId = null;
            int sel = clubSpinner.getSelectedItemPosition();
            if (sel > 0 && sel - 1 < clubs.size()) {
                Club sc = clubs.get(sel - 1);
                clubName = sc.getName();
                clubId = sc.getId();
            }

            Event ev = new Event(name, normTime, loc.isEmpty() ? null : loc, date, clubId, clubName);
            // collect hashtags
            List<String> selectedTags = new ArrayList<>();
            for (int i = 0; i < hashtagsContainer.getChildCount(); i++) {
                View child = hashtagsContainer.getChildAt(i);
                if (child instanceof CheckBox) {
                    CheckBox cb = (CheckBox) child;
                    if (cb.isChecked()) selectedTags.add(cb.getText().toString());
                }
            }
            if (!selectedTags.isEmpty()) ev.setHashtags(selectedTags);

            if (repoIndex >= 0) {
                EventRepository.updateEvent(repoIndex, ev);
                // also replace saved copies in students' app calendars so edits propagate
                MyCalendarRepository.replaceEvent(requireContext(), EventRepository.getEvent(repoIndex), ev);
                Toast.makeText(requireContext(), "Event updated.", Toast.LENGTH_SHORT).show();
            } else {
                EventRepository.addEvent(ev);
                Toast.makeText(requireContext(), "Event created.", Toast.LENGTH_SHORT).show();
            }

            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        // try to parse existing date
        String cur = eventDate.getText().toString().trim();
        if (!cur.isEmpty()) {
            try {
                Date d = dateFmt.parse(cur);
                if (d != null) {
                    c.setTime(d);
                }
            } catch (Exception ignored) {}
        }
        DatePickerDialog dp = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar sel = Calendar.getInstance();
                    sel.set(year, month, dayOfMonth);
                    eventDate.setText(dateFmt.format(sel.getTime()));
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        // try to parse existing time
        String cur = eventTime.getText().toString().trim();
        if (!cur.isEmpty()) {
            Date parsed = tryParseTime(cur);
            if (parsed != null) c.setTime(parsed);
        }
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog tp = new TimePickerDialog(requireContext(), (view, h, m) -> {
            Calendar sel = Calendar.getInstance();
            sel.set(Calendar.HOUR_OF_DAY, h);
            sel.set(Calendar.MINUTE, m);
            eventTime.setText(timeFmt.format(sel.getTime()));
        }, hour, minute, false); // false -> 12-hour view
        tp.show();
    }

    private Date tryParseTime(String s) {
        for (SimpleDateFormat f : parseTimeFormats) {
            try {
                f.setLenient(false);
                return f.parse(s);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private String normalizeTimeString(String s) {
        Date d = tryParseTime(s);
        if (d == null) return null;
        return timeFmt.format(d);
    }
}
