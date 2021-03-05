package com.jose_gomez08.criminalintent

import androidx.lifecycle.ViewModel

class JGCrimeListViewModel : ViewModel() {

    val jgCrimes = mutableListOf<JGCrime>()

    init {
        for (i in 0 until 100) {
            val jgCrime = JGCrime()
            jgCrime.jgTitle = "Crime #$i"
            jgCrime.jgIsSolved = i % 2 == 0
            jgCrimes += jgCrime
        }
    }
}