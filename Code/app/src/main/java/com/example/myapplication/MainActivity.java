package com.example.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView; // Added missing import
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btnHome = findViewById(R.id.btn_home);
        Button btnCreate = findViewById(R.id.btn_create);
        Button btnBrowse = findViewById(R.id.btn_browse);

        btnHome.setOnClickListener(v -> replaceFragment(new HomeFragment()));
        btnCreate.setOnClickListener(v -> replaceFragment(new EventCreateFragment()));
        btnBrowse.setOnClickListener(v -> replaceFragment(new BrowseFragment()));

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}