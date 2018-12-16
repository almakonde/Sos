package msk.android.academy.javatemplate.presentation.useractions.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

//@Entity
data class AlarmAction(
        @PrimaryKey val id: Long,
        val name: String,
        @Embedded val model: ActionModel
) : Serializable
