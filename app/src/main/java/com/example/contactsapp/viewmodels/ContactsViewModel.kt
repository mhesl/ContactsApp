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
    private val _allContactsLiveData = MutableLiveData<List<Contact>>()
    private val _likedContactsLiveData = MutableLiveData<List<Contact>>()
    val contactsLiveData: LiveData<List<Contact>> = _allContactsLiveData
    val likedContactsLiveData: LiveData<List<Contact>> = _likedContactsLiveData


    fun fetchLikedContacts() {
        viewModelScope.launch {
            val contactsListAsync = async { getLikedContacts() }
            val contacts = contactsListAsync.await()
            _likedContactsLiveData.postValue(contacts)
        }
    }


    fun fetchAllContacts() {
        viewModelScope.launch {
            val contactsListAsync = async { getAllContacts() }
            val contacts = contactsListAsync.await()
            _allContactsLiveData.postValue(contacts)
        }
    }

    private suspend fun getAllContacts(): List<Contact> {
        mApplication.let {
            val notes: List<Contact> = ContactDataBase(it).getContactDao().getAllContacts()
            return notes
        }
    }

    private suspend fun getLikedContacts(): List<Contact> {
        val liked = ArrayList<Contact>()
        mApplication.let {
            val notes: List<Contact> = ContactDataBase(it).getContactDao().getAllContacts()
            for (note in notes) {
                if (note.isLiked)
                    liked.add(note)
            }
            return liked
        }
    }
}