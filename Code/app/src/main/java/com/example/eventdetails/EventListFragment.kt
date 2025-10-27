package com.example.eventdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
class EventListFragment : Fragment() {

    private lateinit var eventList: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_list, container, false)
        eventList = view.findViewById(R.id.eventListText)

        val events = EventRepository.getAllEvents()
        if (events.isEmpty()) {
            eventList.text = "No events yet. Create one!"
        } else {
            eventList.text = events.joinToString("\n\n") {
                "Event: ${it.name}\nTime: ${it.time}\nLocation: ${it.location}"
            }
        }

        return view
    }
}
