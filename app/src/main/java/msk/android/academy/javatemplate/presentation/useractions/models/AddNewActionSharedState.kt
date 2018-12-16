package msk.android.academy.javatemplate.presentation.useractions.models

import msk.android.academy.javatemplate.App
import java.util.*

class AddNewActionSharedState(private val alarmId: Long?) {

    private val contacts = HashMap<Long, Contact>()

    init {
        App.getInstance().database.getContactsByAlarmId(alarmId)
                .forEach { contacts.put(it.id, it) }
    }

    fun addContact(contact: Contact) {
        contacts.put(contact.id, contact)
    }

    fun getContacts(): List<Contact> {
        return contacts.values.toList()
    }

    fun deleteContact(contact: Contact) {
        contacts.remove(contact.id)
    }
}
