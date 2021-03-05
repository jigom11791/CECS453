package com.jose_gomez08.criminalintent

import android.app.Application

class JGCriminalIntentApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        JGCrimeRepository.initialize(this)
    }
}