// language: java
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        Button btnHome = findViewById(R.id.btn_home);
        Button btnSignOut = findViewById(R.id.btn_sign_out);
        Button btnCreate = findViewById(R.id.btn_create);
        Button btnBrowse = findViewById(R.id.btn_browse);

        // Hide create button for students
        if (UserSession.isStudent()) {
            btnCreate.setVisibility(View.GONE);
        } else {
            btnCreate.setVisibility(View.VISIBLE);
        }

        // Default: show Browse (calendar) fragment
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new BrowseFragment())
                    .commit();
        }

        btnBrowse.setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new BrowseFragment())
                        .addToBackStack(null)
                        .commit()
        );

        btnCreate.setOnClickListener(v -> {
            // open event create if organizer, otherwise do nothing
            if (!UserSession.isStudent()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new EventCreateFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnHome.setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new BrowseFragment())
                        .addToBackStack(null)
                        .commit()
        );

        btnSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent it = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(it);
            finish();
        });
    }
}
