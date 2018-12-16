package msk.android.academy.javatemplate.presentation.useractions.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import java.io.Serializable
import java.util.*

//@Entity
data class ActionModel(
        @PrimaryKey
        val id: Long,
        @TypeConverters(ActionTypeConverter::class)
        val type: Type,
        @Embedded
        val contacts: List<Contact>
) : Serializable {

    enum class Type constructor(val code: Int) {
        SMS(0), PHONE_CALLING(1)
    }
}
