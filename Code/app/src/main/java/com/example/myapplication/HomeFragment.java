// java
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
        // Inflate a dedicated fragment layout (no duplicated bottom menu)
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Attach sign-out action to the activity's button (button lives in activity_main)
        Button signOutButton = requireActivity().findViewById(R.id.btn_sign_out);
        signOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // When this fragment is visible, replace the home button with sign-out
        View activityView = requireActivity().findViewById(android.R.id.content);
        Button btnHome = activityView.findViewById(R.id.btn_home);
        Button btnSignOut = activityView.findViewById(R.id.btn_sign_out);
        if (btnHome != null && btnSignOut != null) {
            btnHome.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Restore buttons when leaving this fragment
        View activityView = requireActivity().findViewById(android.R.id.content);
        Button btnHome = activityView.findViewById(R.id.btn_home);
        Button btnSignOut = activityView.findViewById(R.id.btn_sign_out);
        if (btnHome != null && btnSignOut != null) {
            btnHome.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
        }
    }
}
