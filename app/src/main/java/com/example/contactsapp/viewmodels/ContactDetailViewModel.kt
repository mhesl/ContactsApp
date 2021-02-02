package com.example.contactsapp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.contactsapp.database.ContactDataBase
import com.example.contactsapp.database.Contact
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ContactDetailViewModel(private val mApplication: Application) :
    AndroidViewModel(mApplication) {
    private val _contactsLiveData = MutableLiveData<Contact>()
    val contactsLiveData: LiveData<Contact> = _contactsLiveData

    fun fetchContacts(number: String) {
        viewModelScope.launch {
            val contactsListAsync = async { getContact(number) }
            val contact = contactsListAsync.await()
            _contactsLiveData.postValue(contact)
        }
    }


    private suspend fun getContact(number: String): Contact {
        mApplication.let {
            val notes: List<Contact> = ContactDataBase(it).getContactDao().getAllContacts()
            for (contact in notes) {
                if (contact.number == number)
                    return contact
            }
        }
        return Contact(0, "", "", "", false)
    }

}