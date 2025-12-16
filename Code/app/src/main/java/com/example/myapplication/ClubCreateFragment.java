// language: java
// File: `app/src/main/java/com/example/myapplication/ClubCreateFragment.java`
package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class ClubCreateFragment extends Fragment {

    private static final String[] TAGS = {"Sports","Learning","Relax","Gaming","Social","Outdoors"};

    public ClubCreateFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_club_create, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final EditText nameInput = view.findViewById(R.id.clubNameInput);
        final EditText descInput = view.findViewById(R.id.clubDescInput);
        Button btnSave = view.findViewById(R.id.btnSaveClub);
        Button btnCancel = view.findViewById(R.id.btnCancelClub);

        // populate hashtag checkboxes if container exists
        LinearLayout hashtagsContainer = view.findViewById(R.id.hashtagsContainer);
        final List<CheckBox> chkBoxes = new ArrayList<>();
        if (hashtagsContainer != null) {
            for (String t : TAGS) {
                CheckBox cb = new CheckBox(requireContext());
                cb.setText(t);
                hashtagsContainer.addView(cb);
                chkBoxes.add(cb);
            }
        }

        btnSave.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a club name.", Toast.LENGTH_SHORT).show();
                return;
            }

            Club club = new Club(name, desc);
            // only advisors are expected to create clubs via event create flow; still allow hashtags if present
            if (!chkBoxes.isEmpty()) {
                List<String> tags = new ArrayList<>();
                for (CheckBox cb : chkBoxes) if (cb.isChecked()) tags.add(cb.getText().toString());
                club.setHashtags(tags);
            }

            ClubRepository.addClub(club);

            Toast.makeText(requireContext(), "Club created.", Toast.LENGTH_SHORT).show();

            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        btnCancel.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
}
