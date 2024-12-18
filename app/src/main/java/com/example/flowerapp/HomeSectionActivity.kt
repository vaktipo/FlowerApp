package com.example.flowerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeSectionActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_section)

        // Завантажуємо початковий фрагмент
        loadFragment(FragmentHomeActivity())

        // Знаходимо BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.BottomNavigationView)

        // Налаштовуємо прослуховувач для вибору елементів меню
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    loadFragment(FragmentHomeActivity())
                    true
                }
                R.id.bottom_calendar -> {
                    loadFragment(FragmentCalendarActivity())
                    true
                }
                R.id.bottom_account -> {
                    loadFragment(FragmentProfileActivity())
                    true
                }
                else -> false
            }
        }
    }

    // Функція для завантаження фрагмента
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_wrapper, fragment)
            .commit()
    }
}

