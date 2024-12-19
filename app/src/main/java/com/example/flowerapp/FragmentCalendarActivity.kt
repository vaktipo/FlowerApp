package com.example.flowerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private var events: MutableMap<String, String> = mutableMapOf()
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendar, container, false)
        calendarView = rootView.findViewById(R.id.calendar)
        auth = FirebaseAuth.getInstance()

        // Fetch events from Firestore and setup calendar
        fetchEventsAndSetupCalendar()

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
                        val eventTitle = document.getString("title") ?: "No Title"
                        val eventDateString = document.getString("eventdate")

                        if (eventDateString != null) {
                            try {
                                // Parse the event date string into a Calendar object
                                val eventDate = dateFormatter.parse(eventDateString)
                                val calendar = Calendar.getInstance().apply { time = eventDate }

                                // Create a CalendarDay for the event
                                val calendarDay = CalendarDay(calendar)
                                calendarDay.labelColor = R.color.pink
                                calendarDay.imageResource = R.drawable.flower
                                calendars.add(calendarDay)

                                // Map the event date to its title
                                events[eventDateString] = eventTitle
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    // Set the marked calendar days on the CalendarView
                    calendarView.setCalendarDays(calendars)

                    // Setup calendar click listeners
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
                val month = String.format("%02d", calendarDay.calendar.get(Calendar.MONTH) + 1) // Month is zero-based
                val year = calendarDay.calendar.get(Calendar.YEAR)
                val dateKey = "$day.$month.$year"

                if (events.containsKey(dateKey)) {
                    Toast.makeText(requireContext(), events[dateKey], Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Nothing to do", Toast.LENGTH_SHORT).show()
                }
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
}