package com.jose_gomez08.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jose_gomez08.criminalintent.Crime
import java.util.*

@Dao
interface JGCrimeDao {

    @Query("SELECT * FROM crime")
    fun jgGetCrimes(): LiveData<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun jgGetCrime(id: UUID): LiveData<Crime?>

    @Update
    fun jgUpdateCrime(crime: Crime)

    @Insert
    fun jgAddCrime(crime: Crime)
}