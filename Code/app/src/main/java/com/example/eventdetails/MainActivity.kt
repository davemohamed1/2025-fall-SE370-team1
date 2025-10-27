package com.example.eventdetails

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.eventdetails.ui.theme.EventDetailsTheme
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class MainActivity : AppCompatActivity () {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start with EventListFragment by default
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, EventListFragment())
            .commit()

        val createBtn = findViewById<Button>(R.id.btnCreate)
        val listBtn = findViewById<Button>(R.id.btnList)

        createBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateEventFragment())
                .addToBackStack(null)
                .commit()
        }

        listBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EventListFragment())
                .commit()
        }
    }
}