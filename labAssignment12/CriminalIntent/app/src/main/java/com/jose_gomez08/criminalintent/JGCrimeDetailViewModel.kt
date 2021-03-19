package com.jose_gomez08.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class JGCrimeDetailViewModel(): ViewModel() {

    private val jgCrimeRepository = JGCrimeRepository.get()
    private val jgCrimeIdLiveData = MutableLiveData<UUID>()

    var jgCrimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(jgCrimeIdLiveData) { crimeId->
            jgCrimeRepository.jgGetCrime(crimeId)
        }

    fun jgLoadCrime(crimeId: UUID) {
        jgCrimeIdLiveData.value = crimeId
    }

    fun jgSaveCrime(crime: Crime) {
        jgCrimeRepository.jgUpdateCrime(crime)
    }
}