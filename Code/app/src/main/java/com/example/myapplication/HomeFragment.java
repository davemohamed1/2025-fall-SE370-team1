package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 1. Inflate the layout and store it in a 'view' variable
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 2. Find the button using the 'view' variable
        Button signOutButton = view.findViewById(R.id.btn_sign_out);

        // 3. Set the click listener
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out of Firebase
                FirebaseAuth.getInstance().signOut();

                // Navigate back to LoginActivity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                // This flag clears the back stack so the user can't press "Back" to return to the home screen
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }
}
