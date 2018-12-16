package msk.android.academy.javatemplate.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert

import msk.android.academy.javatemplate.presentation.useractions.models.Contact

@Dao
interface ContactRepository {

    @Insert
    fun insert(contact: Contact): Long
}
