package com.example.contactsapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.contactsapp.database.ContactDataBase
import com.example.contactsapp.database.Contact
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ContactsViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {
    private val _AllcontactsLiveData = MutableLiveData<List<Contact>>()
    val contactsLiveData: LiveData<List<Contact>> = _AllcontactsLiveData


    fun fetchAllContacts() {
        viewModelScope.launch {
            val contactsListAsync = async { getAllContacts() }
            val contacts = contactsListAsync.await()
            _AllcontactsLiveData.postValue(contacts)
        }
    }

    private suspend fun getAllContacts(): List<Contact> {
        mApplication.let {
            val notes: List<Contact> = ContactDataBase(it).getContactDao().getAllContacts()
            return notes
        }
    }
}