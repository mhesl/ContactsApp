package com.example.contactsapp.ui

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.example.contactsapp.R
import com.example.contactsapp.database.ContactDataBase
import com.example.contactsapp.adapter.ContactsAdapter
import com.example.contactsapp.databinding.ContactsFragmentBinding
import com.example.contactsapp.database.Contact
import com.example.contactsapp.viewmodels.ContactsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


class ContactsFragment : BaseFragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var _binding: ContactsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ContactsViewModel
    private lateinit var searchView: SearchView
    private lateinit var adapter: ContactsAdapter
    private lateinit var filterButton: FloatingActionButton

    private var listView: ListView? = null
    private var spinner: ProgressBar? = null
    private var phones: HashMap<Long, ArrayList<String>> = HashMap()
    private var contacts: ArrayList<Contact> = ArrayList()
    private var contactsNames: ArrayList<String> = ArrayList()
    private var dataBaseContacts: List<Contact> = ArrayList()
    private var filterFlag = true

    private var PROJECTION_NUMBERS: Array<String> = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )
    private var PROJECTION_DETAILS: Array<String> = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.PHOTO_URI
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ContactsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        listView = view.findViewById(R.id.list)
        spinner = view.findViewById(R.id.spinner)
        searchView = view.findViewById(R.id.search_view)
        filterButton = view.findViewById(R.id.filter_button)

        filterButton.setOnClickListener {
            if (filterFlag) {
                viewModel.likedContactsLiveData.observe(viewLifecycleOwner, Observer {
                    adapter = ContactsAdapter(it, requireContext(), this)
                    listView!!.adapter = adapter
                    listView!!.deferNotifyDataSetChanged()
                })
                viewModel.fetchLikedContacts()
                filterFlag = false
            } else {
                loadAdapter()
                filterFlag = true
            }
        }
        return view
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoaderManager.getInstance(this).initLoader(0, null, this)
    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return when (id) {
            0 -> CursorLoader(
                requireContext(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION_NUMBERS,
                null,
                null,
                null
            )
            else -> CursorLoader(
                requireContext(),
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION_DETAILS,
                null,
                null,
                null
            )
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        when (loader.id) {
            0 -> {
                phones = HashMap()
                if (data != null) {

                    while (!data.isClosed && data.moveToNext()) {
                        val contactId: Long = data.getLong(0)
                        val phone: String = data.getString(1)
                        var list: ArrayList<String>?
                        if (phones.containsKey(contactId)) {
                            list = phones[contactId]
                        } else {
                            list = ArrayList()
                            phones[contactId] = list
                        }
                        list!!.add(phone)
                    }
                    data.close()
                }
                LoaderManager.getInstance(this@ContactsFragment).initLoader(1, null, this)
            }
            1 -> if (data != null) {

                while (!data.isClosed && data.moveToNext()) {
                    val contactId: Long = data.getLong(0)
                    val name: String = data.getString(1)
                    var photo: String? = null
                    photo = if (data.getString(2) != null) {
                        data.getString(2)!!
                    } else
                        ""
                    val contactPhones = phones[contactId]
                    if (contactPhones != null) {
                        for (phone in contactPhones) {
                            addContact(Contact(contactId, name, phone, photo, false))
                        }
                    }
                }
                data.close()
                loadAdapter()
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    private fun addContact(contact: Contact?) {
        launch {
            saveContact(contact!!)
        }
        contactsNames.add(contact!!.name)
        contacts.add(contact)
    }

    private fun loadAdapter() {
        launch {
            context.let {
                dataBaseContacts = ContactDataBase(it!!).getContactDao().getAllContacts()
            }
        }
        viewModel.contactsLiveData.observe(viewLifecycleOwner, Observer {
            adapter = ContactsAdapter(it, requireContext(), this)
            listView!!.adapter = adapter
            listView!!.deferNotifyDataSetChanged()
        })
        viewModel.fetchAllContacts()
        spinner!!.visibility = View.GONE
    }

    private suspend fun saveContact(contact: Contact) {
        context?.let {
            ContactDataBase(it).getContactDao().addContact(contact)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)
    }


}