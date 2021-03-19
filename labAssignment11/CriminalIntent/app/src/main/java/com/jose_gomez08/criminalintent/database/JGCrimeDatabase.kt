package com.jose_gomez08.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.jose_gomez08.criminalintent.Crime

@Database(entities = [ Crime::class ], version=1)
@TypeConverters(JGCrimeTypeConverters::class)
abstract class JGCrimeDatabase : RoomDatabase() {

    abstract fun jgCrimeDao(): JGCrimeDao

}