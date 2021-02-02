package com.example.contactsapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    var id: Long,
    var name: String,
    @PrimaryKey var number: String,
    var photo: String,
    var isLiked: Boolean

)