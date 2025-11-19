package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class BrowseFragment extends Fragment {
    public BrowseFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        Button btnList = view.findViewById(R.id.btn_list_sub);
        Button btnCalendar = view.findViewById(R.id.btn_calendar_sub);

        btnList.setOnClickListener(v ->
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.sub_container, new EventListFragment())
                        .commit()
        );

        btnCalendar.setOnClickListener(v ->
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.sub_container, new CalendarFragment())
                        .commit()
        );

        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.sub_container, new CalendarFragment())
                    .commit();
        }

        return view;
    }
}
