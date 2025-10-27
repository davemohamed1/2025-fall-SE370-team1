package com.example.eventdetails

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.*
import androidx.fragment.app.Fragment

class CreateEventFragment : Fragment() {

    private lateinit var eventName: EditText
    private lateinit var eventTime: EditText
    private lateinit var eventLocation: EditText
    private lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_event, container, false)

        eventName = view.findViewById(R.id.eventName)
        eventTime = view.findViewById(R.id.eventTime)
        eventLocation = view.findViewById(R.id.eventLocation)
        saveButton = view.findViewById(R.id.saveButton)

        // Open TimePicker when user clicks the time EditText
        eventTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                val amPm = if (selectedHour >= 12) "PM" else "AM"
                val hourFormatted = if (selectedHour > 12) selectedHour - 12 else if (selectedHour == 0) 12 else selectedHour
                eventTime.setText(String.format("%d:%02d %s", hourFormatted, selectedMinute, amPm))
            }, hour, minute, false).show()
        }

        saveButton.setOnClickListener {
            val name = eventName.text.toString()
            val time = eventTime.text.toString()
            val location = eventLocation.text.toString()

            if (name.isEmpty() || time.isEmpty() || location.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val event = Event(name, time, location)
            EventRepository.addEvent(event)

            Toast.makeText(requireContext(), "Event saved!", Toast.LENGTH_SHORT).show()

            // Clear fields after saving
            eventName.text.clear()
            eventTime.text.clear()
            eventLocation.text.clear()
        }

        return view
    }
}
