package com.dev.nautica

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var petsCountTextView: TextView
    private lateinit var petNamesTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        petsCountTextView = view.findViewById(R.id.pets_count_text_view)
        petNamesTextView = view.findViewById(R.id.pet_names_text_view)

        // For demonstration, you can set some default text here
        petsCountTextView.text = "Total Pets: 0"
        petNamesTextView.text = "Pet Names: None"
    }
}
