package com.jose_gomez08.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.jose_gomez08.criminalintent.database.JGCrimeDao
import com.jose_gomez08.criminalintent.database.JGCrimeDatabase
import java.lang.IllegalStateException
import java.util.*

private const val JG_DATABASE_NAME = "crime-database"

class JGCrimeRepository private constructor(context: Context) {

    private val jgDatabase: JGCrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        JGCrimeDatabase::class.java,
        JG_DATABASE_NAME
    ).build()

    private val jgCrimeDao = jgDatabase.jgCrimeDao()
    fun jgGetCrimes(): LiveData<List<Crime>> = jgCrimeDao.jgGetCrimes()
    fun jgGetCrime(id: UUID): LiveData<Crime?> = jgCrimeDao.jgGetCrime(id)

    companion object {
        private var JGINSTANCE: JGCrimeRepository? = null

        fun initialize(context: Context) {
            if(JGINSTANCE == null) {
                JGINSTANCE = JGCrimeRepository(context)
            }
        }

        fun get(): JGCrimeRepository {
            return JGINSTANCE ?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}