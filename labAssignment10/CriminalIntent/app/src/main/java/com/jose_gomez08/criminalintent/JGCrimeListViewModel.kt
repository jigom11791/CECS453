package com.jose_gomez08.criminalintent

import androidx.lifecycle.ViewModel

class JGCrimeListViewModel : ViewModel() {

    private val jgCrimeRepository = JGCrimeRepository.get()
    val jgCrimeListLiveData = jgCrimeRepository.jgGetCrimes()
}