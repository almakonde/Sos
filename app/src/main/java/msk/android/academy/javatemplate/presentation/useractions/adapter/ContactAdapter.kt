package msk.android.academy.javatemplate.presentation.useractions.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_user.view.*
import msk.android.academy.javatemplate.R
import msk.android.academy.javatemplate.presentation.useractions.models.Contact

class ContactAdapter(
        private val onDeleteContact: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.UserViewHolder>() {

    var data = emptyList<Contact>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
        get() {
            return ArrayList(field)
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): UserViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_user, viewGroup, false)
        return UserViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(viewHolder: UserViewHolder, i: Int) {
        viewHolder.bind(data[i])
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(model: Contact) {
            itemView.tvContact.text = model.name
            itemView.ivRemoveContact.setOnClickListener {
                onDeleteContact.invoke(model)
            }
        }
    }
}
