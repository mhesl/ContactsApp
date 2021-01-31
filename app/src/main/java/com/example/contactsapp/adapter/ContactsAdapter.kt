package com.example.contactsapp.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.contactsapp.R
import com.example.contactsapp.model.Contact


class ContactsAdapter(data: List<Contact>, context: Context) :
    ArrayAdapter<Contact?>(context, R.layout.contacts_model, data) {

    private var dataSet: List<Contact>? = null
    private var mContext: Context? = null

    init {
        dataSet = data
        mContext = context
    }

    // View lookup cache
    class ViewHolder {
        var txtName: TextView? = null
        var txtNumber: TextView? = null
        var imgContact: ImageView? = null
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
            viewHolder.txtNumber = convertViewEx.findViewById(R.id.number)
            viewHolder.imgContact = convertViewEx.findViewById(R.id.imgContact)
            convertViewEx!!.tag = viewHolder
        } else {
            viewHolder = convertViewEx.tag as ViewHolder
        }
        viewHolder.txtName!!.text = contact!!.name
        viewHolder.txtNumber!!.text = contact.number
        viewHolder.imgContact!!.tag = position
        Glide.with(mContext!!)
            .load(contact.photo)
            .apply(RequestOptions.circleCropTransform())
            .fallback(android.R.drawable.sym_def_app_icon)
            .into(viewHolder.imgContact!!)
        return convertViewEx
    }
}



