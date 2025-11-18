
package com.example.myapplication;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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

    public EventListFragment() {
        // Required empty public constructor
    }

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
                    return d1.compareTo(d2); // ascending: earliest first
                } catch (ParseException ex) {
                    return dt1.compareTo(dt2);
                }
            }
        });

        for (int i = 0; i < events.size(); i++) {
            final int index = i;
            Event e = events.get(i);
            Button item = new Button(requireContext());
            item.setText(e.getName() + "\n" + e.getDate() + " " + e.getTime() + "\n" + e.getLocation());
            item.setAllCaps(false);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventCreateFragment frag = EventCreateFragment.newInstance(index);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, frag)
                            .addToBackStack(null)
                            .commit();
                }
            });
            container.addView(item);
        }
    }
}
