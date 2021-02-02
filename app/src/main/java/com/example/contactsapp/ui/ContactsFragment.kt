package com.example.contactsapp.ui

import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
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
import kotlinx.coroutines.launch


class ContactsFragment : BaseFragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var _binding: ContactsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ContactsViewModel

    private var listView: ListView? = null
    private var spinner: ProgressBar? = null
    private var phones: HashMap<Long, ArrayList<String>> = HashMap()
    private var contacts: ArrayList<Contact> = ArrayList()
    private var dataBaseContacts: List<Contact> = ArrayList()


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
                    val photo: String = data.getString(2)!!
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
        contacts.add(contact!!)
    }

    private fun loadAdapter() {
        launch {
            context.let {
                dataBaseContacts = ContactDataBase(it!!).getContactDao().getAllContacts()
            }
        }
        viewModel.contactsLiveData.observe(viewLifecycleOwner, Observer {
            listView!!.adapter = ContactsAdapter(it, requireContext(), this)
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