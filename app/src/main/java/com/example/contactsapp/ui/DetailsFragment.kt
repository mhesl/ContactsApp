package com.example.contactsapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.contactsapp.R
import com.example.contactsapp.database.ContactDataBase
import com.example.contactsapp.databinding.ContactDetailFragmentBinding
import com.example.contactsapp.database.Contact
import com.example.contactsapp.viewmodels.ContactDetailViewModel
import kotlinx.coroutines.launch


class DetailsFragment : BaseFragment() {

    private var _binding: ContactDetailFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ContactDetailViewModel

    private lateinit var contact: Contact
    private lateinit var number: String

    private lateinit var detail_image: ImageView
    private lateinit var detail_name: TextView
    private lateinit var detail_number: TextView
    private lateinit var likeIcon: ImageView
    var photo_condition = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ContactDetailFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        number = requireArguments().getString("number").toString()
        detail_image = view.findViewById(R.id.detail_fragment_image)
        detail_name = view.findViewById(R.id.detail_fragment_name)
        detail_number = view.findViewById(R.id.detail_fragment_number)
        likeIcon = view.findViewById(R.id.like_detail)

        likeIcon.setOnClickListener {
            if (!photo_condition) {
                likeIcon.setBackgroundResource(R.drawable.ic_heart)
                photo_condition = true
                contact.isLiked = true
            } else {
                likeIcon.setBackgroundResource(R.drawable.ic_like)
                photo_condition = false
                contact.isLiked = false
            }

            launch {
                context.let {
                    if (it != null) {
                        ContactDataBase(it).getContactDao().updateContact(contact)
                    }
                }
            }
        }


        return view
    }

    private fun init(number: String) {
        viewModel.contactsLiveData.observe(viewLifecycleOwner, Observer {
            contact = it
            makeUI(contact)
        })
        viewModel.fetchContacts(number)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactDetailViewModel::class.java)
        init(number)
    }

    private fun makeUI(contact: Contact) {
        detail_name.text = contact.name
        detail_number.text = contact.number
        Glide.with(requireActivity())
            .load(contact.photo)
            .apply(RequestOptions.circleCropTransform())
            .fallback(android.R.drawable.sym_def_app_icon)
            .into(detail_image)
        if (contact.isLiked)
            likeIcon.setBackgroundResource(R.drawable.ic_heart)
        else
            likeIcon.setBackgroundResource(R.drawable.ic_like)
    }


}