package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;

    // Constructor to pass the data list
    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This inflates the individual layout for each item (row)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // Get the data for the specific item at this position
        Event event = eventList.get(position);

        // Bind the data to the views
        holder.nameText.setText(event.getName());
        holder.dateText.setText(event.getDate() + " at " + event.getTime());
        holder.locationText.setText(event.getLocation());
    }

    @Override
    public int getItemCount() {
        // Tells the RecyclerView how many items to display
        return eventList.size();
    }

    // ViewHolder class describes an item view and metadata about its place within the RecyclerView
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, dateText, locationText;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find views by ID from item_event.xml
            nameText = itemView.findViewById(R.id.text_event_name);
            dateText = itemView.findViewById(R.id.text_event_date);
            locationText = itemView.findViewById(R.id.text_event_location);
        }
    }
}
