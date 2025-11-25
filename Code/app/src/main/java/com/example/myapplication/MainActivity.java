package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button btnHome;
    private Button btnSignOut;
    private Button btnCreate;
    private Button btnBrowse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find bottom buttons
        btnHome = findViewById(R.id.btn_home);
        btnSignOut = findViewById(R.id.btn_sign_out);
        btnCreate = findViewById(R.id.btn_create);
        btnBrowse = findViewById(R.id.btn_browse);

        // initial fragment = HomeFragment (root)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // button listeners
        btnHome.setOnClickListener(v -> {
            // navigate to home and clear backstack so home is root
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            updateBottomButtons();
        });

        btnSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnCreate.setOnClickListener(v -> {
            // open event create (no editing index)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EventCreateFragment())
                    .addToBackStack(null)
                    .commit();
            updateBottomButtons();
        });

        btnBrowse.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new BrowseFragment())
                    .addToBackStack(null)
                    .commit();
            updateBottomButtons();
        });

        // listen for backstack changes (e.g., user pressed back)
        getSupportFragmentManager().addOnBackStackChangedListener(this::updateBottomButtons);

        // ensure buttons reflect the initial fragment
        updateBottomButtons();
    }

    private void updateBottomButtons() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        boolean isHome = current instanceof HomeFragment;
        // when on HomeFragment, hide the home button and show sign out
        btnHome.setVisibility(isHome ? View.GONE : View.VISIBLE);
        btnSignOut.setVisibility(isHome ? View.VISIBLE : View.GONE);
        // btn_create and btn_browse remain visible/active always
    }
}
