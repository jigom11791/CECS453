package com.jose_gomez08.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(),
    JGCrimeListFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val jgCurrentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (jgCurrentFragment == null) {
            val jgFragment = JGCrimeListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, jgFragment)
                .commit()
        }
    }

    override fun jgOnCrimeSelected(crimeId: UUID) {
        Log.d(TAG, "MainActivity.onCrimeSelected: $crimeId")
        val jgFragment = JGCrimeFragment.newInstance(crimeId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, jgFragment)
            .addToBackStack(null)
            .commit()
    }
}