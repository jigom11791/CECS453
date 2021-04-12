package com.jose_gomez08.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jose_gomez08.criminalintent.Crime

@Database(entities = [ Crime::class ], version=2)
@TypeConverters(JGCrimeTypeConverters::class)
abstract class JGCrimeDatabase : RoomDatabase() {

    abstract fun jgCrimeDao(): JGCrimeDao

}

val jg_migration_1_2 = object : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
        )
    }
}