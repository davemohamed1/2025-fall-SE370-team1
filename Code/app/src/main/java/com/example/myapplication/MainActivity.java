package com.example.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;import android.widget.Button;
import android.widget.TextView; // Added missing import
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private MenuItem toggleMenuItem;
    // Consolidated variables from the second class definition
    private TextView helloText;
    private Button clickButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // make menu visible when using NoActionBar theme

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EventListFragment())
                    .commit();
        }

        // --- Logic from the first block ---
        Button createBtn = findViewById(R.id.btnCreate);
        Button listBtn = findViewById(R.id.btnList);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new EventCreateFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new EventListFragment())
                        .commit();
            }
        });

        // --- Logic from the second (merged) block ---
        helloText = findViewById(R.id.helloText);
        clickButton = findViewById(R.id.clickButton);

        // Note: You should ensure your layout XML actually has IDs "helloText" and "clickButton"
        // alongside "btnCreate" and "btnList", otherwise this will cause a crash at runtime.
        if (clickButton != null) {
            clickButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    helloText.setText("Button has pressed by Jose !");
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        toggleMenuItem = menu.findItem(R.id.action_toggle_layout);
        if (toggleMenuItem != null) {
            toggleMenuItem.setTitle("Calendar");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_layout) {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (current instanceof CalendarFragment) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new EventListFragment())
                        .commit();
                item.setTitle("Calendar");
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CalendarFragment())
                        .addToBackStack(null)
                        .commit();
                item.setTitle("List");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
