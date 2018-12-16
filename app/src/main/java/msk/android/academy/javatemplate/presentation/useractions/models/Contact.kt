package msk.android.academy.javatemplate.presentation.useractions.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

//@Entity
data class Contact(
        @PrimaryKey val id: Long,
        val name: String,
        val phoneNumber: String
) : Serializable
