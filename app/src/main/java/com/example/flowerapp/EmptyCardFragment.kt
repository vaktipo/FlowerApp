package com.example.flowerapp

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.w3c.dom.Text

class EmptyCardFragment: Fragment(R.layout.empty_card) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addCard = view.findViewById<TextView>(R.id.textView2)
        addCard.setOnClickListener {
            val addCardFragment = AddCardFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_wrapper, addCardFragment)
                .addToBackStack(null)
                .commit()
        }
    }

}