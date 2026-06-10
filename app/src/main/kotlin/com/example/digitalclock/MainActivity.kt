package com.example.digitalclock

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var timeZoneAdapter: AdvancedTimeZoneAdapter
    private lateinit var formatSpinner: Spinner
    private lateinit var sortSpinner: Spinner
    private var is24HourFormat = true
    private var currentSortBy = SortBy.NAME

    private val selectedTimeZones = mutableListOf(
        TimeZoneData("America/New_York", "EST - Eastern Standard"),
        TimeZoneData("Europe/London", "GMT - Greenwich Mean"),
        TimeZoneData("Asia/Tokyo", "JST - Japan Standard"),
        TimeZoneData("Australia/Sydney", "AEDT - Australian Eastern")
    )

    enum class SortBy {
        NAME, TIME_OFFSET, ALPHABETICAL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentTimeDisplay = findViewById<TextView>(R.id.currentTimeDisplay)
        val timeZoneInput = findViewById<EditText>(R.id.timeZoneInput)
        val addButton = findViewById<Button>(R.id.addTimeZoneButton)
        val recyclerView = findViewById<RecyclerView>(R.id.timeZoneRecyclerView)
        formatSpinner = findViewById(R.id.formatSpinner)
        sortSpinner = findViewById(R.id.sortSpinner)
        val searchButton = findViewById<Button>(R.id.searchTimeZonesButton)

        // Setup RecyclerView
        timeZoneAdapter = AdvancedTimeZoneAdapter(
            selectedTimeZones,
            is24HourFormat,
            { removeTimeZone(it) },
            { favoriteTimeZone(it) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = timeZoneAdapter

        // Setup Format Spinner
        val formatAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.time_formats,
            android.R.layout.simple_spinner_item
        )
        formatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        formatSpinner.adapter = formatAdapter
        formatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                is24HourFormat = position == 0
                timeZoneAdapter.update24HourFormat(is24HourFormat)
                updateTime(currentTimeDisplay)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Setup Sort Spinner
        val sortAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sort_options,
            android.R.layout.simple_spinner_item
        )
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = sortAdapter
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentSortBy = SortBy.values()[position]
                sortTimeZones()
                timeZoneAdapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Add button listener
        addButton.setOnClickListener {
            val input = timeZoneInput.text.toString().trim()
            if (input.isNotEmpty() && isValidTimeZone(input)) {
                selectedTimeZones.add(TimeZoneData(input, getTimeZoneDescription(input)))
                sortTimeZones()
                timeZoneAdapter.notifyDataSetChanged()
                timeZoneInput.text.clear()
            } else {
                timeZoneInput.error = "Invalid timezone"
            }
        }

        // Search button listener
        searchButton.setOnClickListener {
            showTimeZoneSearchDialog()
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
        val formatter = if (is24HourFormat) {
            DateTimeFormatter.ofPattern("HH:mm:ss")
        } else {
            DateTimeFormatter.ofPattern("hh:mm:ss a")
        }
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

    private fun getTimeZoneDescription(zoneId: String): String {
        val zoneOffset = ZonedDateTime.now(ZoneId.of(zoneId)).offset
        return "$zoneId (UTC${zoneOffset})"
    }

    private fun removeTimeZone(index: Int) {
        selectedTimeZones.removeAt(index)
        timeZoneAdapter.notifyItemRemoved(index)
    }

    private fun favoriteTimeZone(index: Int) {
        val timezone = selectedTimeZones[index]
        timezone.isFavorite = !timezone.isFavorite
        sortTimeZones()
        timeZoneAdapter.notifyDataSetChanged()
    }

    private fun sortTimeZones() {
        when (currentSortBy) {
            SortBy.NAME -> selectedTimeZones.sortBy { it.name }
            SortBy.TIME_OFFSET -> selectedTimeZones.sortBy { getTimeOffset(it.name) }
            SortBy.ALPHABETICAL -> selectedTimeZones.sortBy { it.name.lowercase() }
        }
        // Move favorites to top
        val favorites = selectedTimeZones.filter { it.isFavorite }
        val nonFavorites = selectedTimeZones.filter { !it.isFavorite }
        selectedTimeZones.clear()
        selectedTimeZones.addAll(favorites)
        selectedTimeZones.addAll(nonFavorites)
    }

    private fun getTimeOffset(zoneId: String): Int {
        return try {
            ZonedDateTime.now(ZoneId.of(zoneId)).offset.totalSeconds / 3600
        } catch (e: Exception) {
            0
        }
    }

    private fun showTimeZoneSearchDialog() {
        val commonTimeZones = arrayOf(
            "America/New_York",
            "America/Chicago",
            "America/Denver",
            "America/Los_Angeles",
            "Europe/London",
            "Europe/Paris",
            "Europe/Berlin",
            "Asia/Tokyo",
            "Asia/Shanghai",
            "Asia/Hong_Kong",
            "Asia/Dubai",
            "Asia/Bangkok",
            "Australia/Sydney",
            "Australia/Melbourne",
            "Pacific/Auckland",
            "Africa/Cairo"
        )

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Timezone")
        builder.setItems(commonTimeZones) { _, which ->
            val selectedZone = commonTimeZones[which]
            if (!selectedTimeZones.any { it.name == selectedZone }) {
                selectedTimeZones.add(TimeZoneData(selectedZone, getTimeZoneDescription(selectedZone)))
                sortTimeZones()
                timeZoneAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Timezone already added", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
    }
}

data class TimeZoneData(
    val name: String,
    val description: String,
    var isFavorite: Boolean = false
)

class AdvancedTimeZoneAdapter(
    private val timeZones: List<TimeZoneData>,
    private var is24HourFormat: Boolean,
    private val onRemove: (Int) -> Unit,
    private val onFavorite: (Int) -> Unit
) : RecyclerView.Adapter<AdvancedTimeZoneAdapter.TimeZoneViewHolder>() {

    inner class TimeZoneViewHolder(itemView: android.view.View) :
        RecyclerView.ViewHolder(itemView) {
        private val zoneNameTextView: TextView = itemView.findViewById(R.id.zoneNameTextView)
        private val zoneTimeTextView: TextView = itemView.findViewById(R.id.zoneTimeTextView)
        private val zoneDetailsTextView: TextView = itemView.findViewById(R.id.zoneDetailsTextView)
        private val sunriseSunsetTextView: TextView = itemView.findViewById(R.id.sunriseSunsetTextView)
        private val removeButton: Button = itemView.findViewById(R.id.removeButton)
        private val favoriteButton: Button = itemView.findViewById(R.id.favoriteButton)

        fun bind(zoneData: TimeZoneData, position: Int) {
            zoneNameTextView.text = zoneData.name
            zoneNameTextView.setCompoundDrawablesWithIntrinsicBounds(
                if (zoneData.isFavorite) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off,
                0, 0, 0
            )

            val now = ZonedDateTime.now(ZoneId.of(zoneData.name))
            val timeFormatter = if (is24HourFormat) {
                DateTimeFormatter.ofPattern("HH:mm:ss")
            } else {
                DateTimeFormatter.ofPattern("hh:mm:ss a")
            }
            val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")
            val offsetFormatter = DateTimeFormatter.ofPattern("Z")

            zoneTimeTextView.text = now.format(timeFormatter)
            zoneDetailsTextView.text = "${now.format(dateFormatter)} • UTC${now.format(offsetFormatter)}"

            // Sunrise/Sunset simulation (in real app, would use actual API)
            val sunriseTime = now.plusHours(7)
            val sunsetTime = now.plusHours(19)
            val sunriseFormatter = DateTimeFormatter.ofPattern("HH:mm")
            sunriseSunsetTextView.text = "☀ ${sunriseTime.format(sunriseFormatter)} • 🌙 ${sunsetTime.format(sunriseFormatter)}"

            removeButton.setOnClickListener {
                onRemove(position)
            }

            favoriteButton.setOnClickListener {
                onFavorite(position)
                favoriteButton.text = if (zoneData.isFavorite) "★ Favorite" else "☆ Favorite"
            }

            favoriteButton.text = if (zoneData.isFavorite) "★ Favorite" else "☆ Favorite"
            favoriteButton.setBackgroundColor(
                if (zoneData.isFavorite) itemView.context.getColor(android.R.color.holo_orange_dark)
                else itemView.context.getColor(android.R.color.darker_gray)
            )
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): TimeZoneViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.advanced_timezone_item, parent, false)
        return TimeZoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeZoneViewHolder, position: Int) {
        holder.bind(timeZones[position], position)
    }

    override fun getItemCount(): Int = timeZones.size

    fun update24HourFormat(format: Boolean) {
        is24HourFormat = format
        notifyDataSetChanged()
    }
}
