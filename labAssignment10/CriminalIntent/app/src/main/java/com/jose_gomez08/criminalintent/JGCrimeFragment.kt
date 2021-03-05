package com.jose_gomez08.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment

class JGCrimeFragment: Fragment() {

    private lateinit var jgCrime: Crime
    private lateinit var jgTitleField: EditText
    private lateinit var jgDateButton: Button
    private lateinit var jgSolvedCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jgCrime = Crime()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val jgView = inflater.inflate(R.layout.fragment_crime, container, false)
        jgTitleField = jgView.findViewById(R.id.jg_crime_title) as EditText
        jgDateButton = jgView.findViewById(R.id.jg_crime_date) as Button
        jgSolvedCheckBox = jgView.findViewById(R.id.jg_crime_solved) as CheckBox

        jgDateButton.apply {
            text = jgCrime.date.toString()
            isEnabled = false
        }

        return jgView
    }

    override fun onStart() {
        super.onStart()

        var jgTitleWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                jgCrime.title = s.toString()

            }

            override fun afterTextChanged(s: Editable?) {
                // do nothing
            }
        }

        jgTitleField.addTextChangedListener(jgTitleWatcher)

        jgSolvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                jgCrime.isSolved = isChecked
            }
        }
    }

}