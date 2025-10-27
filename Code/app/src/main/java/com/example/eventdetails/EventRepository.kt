package com.example.eventdetails

object EventRepository {
    private val events = mutableListOf<Event>()

    fun addEvent(event: Event) {
        events.add(event)
    }

    fun getAllEvents(): List<Event> = events
}
