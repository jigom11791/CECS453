package com.jose_gomez08.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.util.*
import androidx.lifecycle.Observer

private const val TAG = "JGCrimeFragment"
private const val JG_ARG_CRIME_ID = "jg_crime_id"

class JGCrimeFragment: Fragment() {

    private lateinit var jgCrime: Crime
    private lateinit var jgTitleField: EditText
    private lateinit var jgDateButton: Button
    private lateinit var jgSolvedCheckBox: CheckBox
    private val  jgCrimeDetailViewModel: JGCrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(JGCrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jgCrime = Crime()
        val jgCrimeId: UUID = arguments?.getSerializable(JG_ARG_CRIME_ID) as UUID
        //Log.d(TAG, "args bundle crime ID: $jgCrimeId")
        //load crime data from database
        jgCrimeDetailViewModel.jgLoadCrime(jgCrimeId)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        jgCrimeDetailViewModel.jgCrimeLiveData.observe(
            viewLifecycleOwner,
            Observer{ crime->
                crime?.let {
                    this.jgCrime = crime
                    updateUI()
                }
            }
        )
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

    override fun onStop() {
        super.onStop()
        jgCrimeDetailViewModel.jgSaveCrime(jgCrime)
    }

    private fun updateUI() {
        jgTitleField.setText(jgCrime.title)
        jgDateButton.text = jgCrime.date.toString()
        jgSolvedCheckBox.apply {
            isChecked = jgCrime.isSolved
            jumpDrawablesToCurrentState()
        }
    }

    companion object {
        fun newInstance(crimeId: UUID): JGCrimeFragment {
            val jgArgs = Bundle().apply {
                putSerializable(JG_ARG_CRIME_ID, crimeId)
            }
            return JGCrimeFragment().apply {
                arguments = jgArgs
            }
        }
    }

}