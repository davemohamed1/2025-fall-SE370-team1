package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class ClubCreateFragment extends Fragment {

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

        btnSave.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a club name.", Toast.LENGTH_SHORT).show();
                return;
            }

            Club club = new Club(name, desc);
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
