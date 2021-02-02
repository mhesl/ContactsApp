package com.example.contactsapp.database

import androidx.room.*

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addContact(contact: Contact)

    @Query("SELECT * FROM contact ORDER BY number DESC")
    suspend fun getAllContacts(): List<Contact>

    @Insert
    suspend fun addMultipleContacts(vararg contacts: Contact)

    @Update
    suspend fun updateContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("SELECT * FROM contact WHERE number LIKE :number ")
    fun findByNumber(number: String): Contact
}