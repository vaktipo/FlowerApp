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
import java.util.Calendar

class FragmentCalendarActivity : Fragment(R.layout.fragment_calendar) {
    private lateinit var calendarView: CalendarView
    private var events: MutableMap<String, String> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendar, container, false)
        calendarView = rootView.findViewById(R.id.calendar)

        // Initialize calendar events and functionality
        setupCalendar()

        return rootView
    }

    private fun setupCalendar() {
        val calendars: ArrayList<CalendarDay> = ArrayList()
        val calendar = Calendar.getInstance()

        // Setting up an event on a specific date
        calendar.set(2024, Calendar.DECEMBER, 20) // Month is zero-based
        val calendarDay = CalendarDay(calendar)
        calendarDay.labelColor = R.color.pink
        calendarDay.imageResource = R.drawable.flower
        calendars.add(calendarDay)

        events["2024-12-20"] = "Bouquet for Mother"
        calendarView.setCalendarDays(calendars)

        calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                val day = String.format("%02d", calendarDay.calendar.get(Calendar.DAY_OF_MONTH))
                val month = String.format("%02d", calendarDay.calendar.get(Calendar.MONTH) + 1) // Month is zero-based
                val year = calendarDay.calendar.get(Calendar.YEAR)
                val dateKey = "$year-$month-$day"

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