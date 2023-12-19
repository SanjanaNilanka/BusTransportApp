package com.example.bustraveler.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bustraveler.R
import com.example.bustraveler.dataclasses.Journey

class JourneyAdapter(private val journeys: List<Journey>) :
    RecyclerView.Adapter<JourneyAdapter.JourneyViewHolder>() {

    class JourneyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val startLocationView: TextView = itemView.findViewById(R.id.textStartLocation)
        val endLocationView: TextView = itemView.findViewById(R.id.textEndLocation)
        val durationView: TextView = itemView.findViewById(R.id.textDuration)
        val fareView: TextView = itemView.findViewById(R.id.textFare)
        val dateView: TextView = itemView.findViewById(R.id.textDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_journey, parent, false)
        return JourneyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: JourneyViewHolder, position: Int) {
        val journey = journeys[position]
        holder.startLocationView.text = journey.startLocation.substring(0, 5)
        holder.endLocationView.text = journey.endLocation.substring(0, 5)
        holder.durationView.text = "${journey.duration} km"
        holder.fareView.text = "Rs. ${journey.fare}"
        holder.dateView.text = journey.date.substring(0, 10)
    }

    override fun getItemCount() = journeys.size
}
