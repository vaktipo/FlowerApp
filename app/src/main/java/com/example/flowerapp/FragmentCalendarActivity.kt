package com.example.flowerapp

import EventScreenFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmentCalendarActivity : Fragment(R.layout.fragment_calendar) {

    private lateinit var calendarView: CalendarView
    private lateinit var eventNameTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var eventDedicationTextView: TextView
    private var events: MutableMap<String, Event> = mutableMapOf()
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private var dateKey: String? = null // Store the selected date key

    data class Event(val eventName: String, val beneficiary: String, val eventDate: String, val eventId: String, val recurrence: String)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendar, container, false)
        calendarView = rootView.findViewById(R.id.calendar)
        auth = FirebaseAuth.getInstance()

        eventNameTextView = rootView.findViewById(R.id.eventName)
        dateTextView = rootView.findViewById(R.id.date)
        eventDedicationTextView = rootView.findViewById(R.id.eventDedication)

        val addBtn = rootView.findViewById<TextView>(R.id.addNewBttn)
        val goToEvent = rootView.findViewById<TextView>(R.id.eventName)

        fetchEventsAndSetupCalendar()

        // Add button listener
        addBtn.setOnClickListener {
            val addEventFragment = AddEventFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_wrapper, addEventFragment)
                .addToBackStack(null)
                .commit()
        }

        // Go to event listener
        goToEvent.setOnClickListener {
            if (dateKey != null && events.containsKey(dateKey)) {
                val selectedEvent = events[dateKey]
                if (selectedEvent != null) {
                    val eventScreen = EventScreenFragment()
                    val bundle = Bundle()
                    bundle.putString("eventName", selectedEvent.eventName)
                    bundle.putString("eventDate", selectedEvent.eventDate)
                    bundle.putString("beneficiary", selectedEvent.beneficiary)
                    bundle.putString("eventId", selectedEvent.eventId)
                    bundle.putString("recurrence",selectedEvent.recurrence)// Pass the eventId here
                    eventScreen.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fl_wrapper, eventScreen)
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                Toast.makeText(requireContext(), "No event found for this date", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    private fun fetchEventsAndSetupCalendar() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users")
                .document(userId)
                .collection("events")
                .get()
                .addOnSuccessListener { documents ->
                    val calendars: ArrayList<CalendarDay> = ArrayList()
                    val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

                    for (document in documents) {
                        val eventTitle = document.getString("eventname") ?: "No Title"
                        val eventDateString = document.getString("eventdate")
                        val beneficiary = document.getString("beneficiary") ?: "Unknown"
                        val recurrence = document.getString("recurrence") ?: "Unknown"
                        val eventId = document.id  // Get the event ID from the Firestore document ID

                        if (eventDateString != null) {
                            try {
                                val eventDate = dateFormatter.parse(eventDateString)
                                val calendar = Calendar.getInstance().apply { time = eventDate }

                                val calendarDay = CalendarDay(calendar)
                                calendarDay.labelColor = R.color.pink
                                calendarDay.imageResource = R.drawable.flower
                                calendars.add(calendarDay)

                                // Store the event data in the events map including the eventId
                                events[eventDateString] = Event(eventTitle, beneficiary, eventDateString, eventId, recurrence)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    // Update the calendar view
                    calendarView.setCalendarDays(calendars)
                    setupCalendar()
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCalendar() {
        calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                val day = String.format("%02d", calendarDay.calendar.get(Calendar.DAY_OF_MONTH))
                val month = String.format("%02d", calendarDay.calendar.get(Calendar.MONTH) + 1)
                val year = calendarDay.calendar.get(Calendar.YEAR)
                dateKey = "$day.$month.$year"  // Store the selected date in dateKey

                // Display event details
                displayEventDetails(dateKey!!)
            }
        })

        calendarView.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                val month = String.format("%02d", calendarView.currentPageDate.get(Calendar.MONTH) + 1)
                val year = calendarView.currentPageDate.get(Calendar.YEAR)
                Toast.makeText(requireContext(), "$month/$year", Toast.LENGTH_SHORT).show()
            }
        })

        calendarView.setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                val month = String.format("%02d", calendarView.currentPageDate.get(Calendar.MONTH) + 1)
                val year = calendarView.currentPageDate.get(Calendar.YEAR)
                Toast.makeText(requireContext(), "$month/$year", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayEventDetails(dateKey: String) {
        if (events.containsKey(dateKey)) {
            val event = events[dateKey]!!
            eventNameTextView.text = event.eventName
            dateTextView.text = event.eventDate
            eventDedicationTextView.text = "Present for: ${event.beneficiary}"
        } else {
            eventNameTextView.text = "No Event"
            dateTextView.text = "No Date"
            eventDedicationTextView.text = "No Dedication"
        }
    }
}