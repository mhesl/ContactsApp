package com.example.contactsapp.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.contactsapp.R
import com.example.contactsapp.database.Contact
import com.example.contactsapp.ui.ContactsFragment


class ContactsAdapter(data: List<Contact>, context: Context, parentFragment: ContactsFragment) :
    ArrayAdapter<Contact?>(context, R.layout.contacts_model, data) {

    private var dataSet: List<Contact>? = null
    private var mContext: Context? = null
    private var mParentFragment = ContactsFragment()

    init {
        dataSet = data
        mContext = context
        mParentFragment = parentFragment
    }

    // View lookup cache
    class ViewHolder {
        var txtName: TextView? = null
        var imgContact: ImageView? = null
        var likeIcon: ImageView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val contact = getItem(position)
        val viewHolder: ViewHolder

        var convertViewEx = convertView
        if (convertViewEx == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertViewEx = inflater.inflate(R.layout.contacts_model, parent, false)
            viewHolder.txtName = convertViewEx.findViewById(R.id.name)
            viewHolder.imgContact = convertViewEx.findViewById(R.id.imgContact)
            viewHolder.likeIcon = convertViewEx.findViewById(R.id.contact_like_image)
            convertViewEx!!.tag = viewHolder
        } else {
            viewHolder = convertViewEx.tag as ViewHolder
        }
        if (contact!!.isLiked)
            viewHolder.likeIcon!!.setBackgroundResource(R.drawable.ic_heart)
        else
            viewHolder.likeIcon!!.setBackgroundResource(R.drawable.ic_like)

        viewHolder.txtName!!.text = contact.name
        viewHolder.imgContact!!.tag = position
        Glide.with(mContext!!)
            .load(contact.photo)
            .apply(RequestOptions.circleCropTransform())
            .fallback(android.R.drawable.sym_def_app_icon)
            .into(viewHolder.imgContact!!)
        convertViewEx.setOnClickListener {
            NavHostFragment.findNavController(mParentFragment)
                .navigate(
                    R.id.action_contactsFragment_to_detailsFragment, bundleOf(
                        "number" to contact.number
                    )
                )
        }

        return convertViewEx
    }


}



