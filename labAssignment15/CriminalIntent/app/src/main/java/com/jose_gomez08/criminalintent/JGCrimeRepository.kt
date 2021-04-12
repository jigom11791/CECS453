package com.jose_gomez08.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.jose_gomez08.criminalintent.database.JGCrimeDao
import com.jose_gomez08.criminalintent.database.JGCrimeDatabase
import com.jose_gomez08.criminalintent.database.jg_migration_1_2
import java.io.File
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val JG_DATABASE_NAME = "crime-database"

class JGCrimeRepository private constructor(context: Context) {

    private val jgDatabase: JGCrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        JGCrimeDatabase::class.java,
        JG_DATABASE_NAME
    ).addMigrations(jg_migration_1_2)
        .build()

    private val jgCrimeDao = jgDatabase.jgCrimeDao()
    private val jgExecutor = Executors.newSingleThreadExecutor()
    private val jgFileDir = context.applicationContext.filesDir

    fun jgGetCrimes(): LiveData<List<Crime>> = jgCrimeDao.jgGetCrimes()
    fun jgGetCrime(id: UUID): LiveData<Crime?> = jgCrimeDao.jgGetCrime(id)

    fun jgUpdateCrime(crime: Crime) {
        jgExecutor.execute{
            jgCrimeDao.jgUpdateCrime(crime)
        }
    }

    fun jgAddCrime(crime: Crime) {
        jgExecutor.execute {
            jgCrimeDao.jgAddCrime(crime)
        }
    }

    fun jgGetPhotoFile(crime: Crime): File = File(jgFileDir, crime.photoFileName)

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