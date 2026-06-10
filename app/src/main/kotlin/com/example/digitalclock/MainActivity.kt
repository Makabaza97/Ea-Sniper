package com.example.digitalclock

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var timeZoneAdapter: TimeZoneAdapter
    private val selectedTimeZones = mutableListOf(
        "America/New_York",      // EST
        "Europe/London",          // GMT
        "Asia/Tokyo",             // JST
        "Australia/Sydney"        // AEDT
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentTimeDisplay = findViewById<TextView>(R.id.currentTimeDisplay)
        val timeZoneInput = findViewById<EditText>(R.id.timeZoneInput)
        val addButton = findViewById<Button>(R.id.addTimeZoneButton)
        val recyclerView = findViewById<RecyclerView>(R.id.timeZoneRecyclerView)

        // Setup RecyclerView
        timeZoneAdapter = TimeZoneAdapter(selectedTimeZones)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = timeZoneAdapter

        // Add button listener
        addButton.setOnClickListener {
            val input = timeZoneInput.text.toString().trim()
            if (input.isNotEmpty() && isValidTimeZone(input)) {
                selectedTimeZones.add(input)
                timeZoneAdapter.notifyItemInserted(selectedTimeZones.size - 1)
                timeZoneInput.text.clear()
            } else {
                timeZoneInput.error = "Invalid timezone"
            }
        }

        // Update time every second
        updateTime(currentTimeDisplay)
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    updateTime(currentTimeDisplay)
                    timeZoneAdapter.notifyDataSetChanged()
                }
            }
        }, 1000, 1000)
    }

    private fun updateTime(textView: TextView) {
        val now = ZonedDateTime.now(ZoneId.of("UTC"))
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        textView.text = now.format(formatter)
    }

    private fun isValidTimeZone(zoneId: String): Boolean {
        return try {
            ZoneId.of(zoneId)
            true
        } catch (e: Exception) {
            false
        }
    }
}

class TimeZoneAdapter(private val timeZones: List<String>) :
    RecyclerView.Adapter<TimeZoneAdapter.TimeZoneViewHolder>() {

    inner class TimeZoneViewHolder(itemView: android.view.View) :
        RecyclerView.ViewHolder(itemView) {
        private val zoneNameTextView: TextView = itemView.findViewById(R.id.zoneNameTextView)
        private val zoneTimeTextView: TextView = itemView.findViewById(R.id.zoneTimeTextView)
        private val zoneDetailsTextView: TextView = itemView.findViewById(R.id.zoneDetailsTextView)
        private val removeButton: Button = itemView.findViewById(R.id.removeButton)

        fun bind(zoneId: String) {
            zoneNameTextView.text = zoneId

            val now = ZonedDateTime.now(ZoneId.of(zoneId))
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")

            zoneTimeTextView.text = now.format(timeFormatter)
            zoneDetailsTextView.text = now.format(dateFormatter)

            removeButton.setOnClickListener {
                val adapter = this@TimeZoneAdapter
                val position = adapterPosition
                if (position >= 0) {
                    (adapter.timeZones as? MutableList)?.removeAt(position)
                    adapter.notifyItemRemoved(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): TimeZoneViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.timezone_item, parent, false)
        return TimeZoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeZoneViewHolder, position: Int) {
        holder.bind(timeZones[position])
    }

    override fun getItemCount(): Int = timeZones.size
}
