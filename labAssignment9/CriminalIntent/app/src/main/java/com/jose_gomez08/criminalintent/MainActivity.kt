package com.jose_gomez08.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
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
}