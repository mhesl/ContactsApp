package com.example.contactsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [Contact::class],
    version = 1
)
abstract class ContactDataBase : RoomDatabase() {

    abstract fun getContactDao(): ContactDao

    companion object {
        @Volatile
        private var instance: ContactDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDataBase(context).also {
                instance = it
            }
        }

        private fun buildDataBase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ContactDataBase::class.java,
            "contactdatabase"
        ).build()
    }
}