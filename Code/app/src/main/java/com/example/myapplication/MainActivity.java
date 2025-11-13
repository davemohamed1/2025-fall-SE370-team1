<<<<<<< HEAD
package com.example.eventdetails_java;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private MenuItem toggleMenuItem;
=======
package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView helloText;
    private Button clickButton;
>>>>>>> parent of 11df180 (home page)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // make menu visible when using NoActionBar theme

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EventListFragment())
                    .commit();
        }

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        toggleMenuItem = menu.findItem(R.id.action_toggle_layout);
        toggleMenuItem.setTitle("Calendar");
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
=======
        helloText = findViewById(R.id.helloText);
        clickButton = findViewById(R.id.clickButton);

        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helloText.setText("Button has pressed by Jose !");
            }
        });
>>>>>>> parent of 11df180 (home page)
    }
}
