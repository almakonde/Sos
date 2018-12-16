package msk.android.academy.javatemplate.presentation.useractions.adapter

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_action.view.*

import msk.android.academy.javatemplate.R
import msk.android.academy.javatemplate.presentation.useractions.models.AlarmAction
import msk.android.academy.javatemplate.presentation.useractions.models.ActionEditingMode
import msk.android.academy.javatemplate.presentation.useractions.models.ActionModel

class ActionsAdapter(
        private val onActionClick: (AlarmAction, ActionEditingMode) -> Void,
        private val onSelected: (AlarmAction, ActionEditingMode) -> Void,
        private val onItemDeleteClickListener: (action: AlarmAction) -> Unit
) : RecyclerView.Adapter<ActionsAdapter.ActionViewHolder>() {

    var data: List<AlarmAction> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedItem = 0L
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ActionViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_action, viewGroup,
                false)
        return ActionViewHolder(view)
    }

    override fun onBindViewHolder(actionViewHolder: ActionViewHolder, i: Int) {
        actionViewHolder.bind(data[i], i)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ActionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: AlarmAction, position: Int) {
            itemView.tvActionName.text = item.name
            val type = item.model.type
            val typeName = when (type) {
                ActionModel.Type.SMS -> itemView.resources.getString(R.string.sms)
                ActionModel.Type.PHONE_CALLING -> itemView.resources.getString(R.string.phone_calling)
            }
            itemView.tvActionType.text = typeName
            val notifiableUsers = item.model.contacts.joinToString { it.name }
            itemView.tvNotifiableUsers.text = notifiableUsers
            itemView.cbAction.isChecked = item.id == selectedItem
            itemView.cbAction.setOnClickListener {
                selectedItem = item.id
                onSelected.invoke(item, ActionEditingMode.EDITING)
            }
            itemView.setOnClickListener {
                onActionClick.invoke(item, ActionEditingMode.EDITING)
            }
            itemView.setOnCreateContextMenuListener(ContextMenuListener(item))
        }

        private inner class ContextMenuListener(private val item: AlarmAction) : View.OnCreateContextMenuListener {

            override fun onCreateContextMenu(menu: ContextMenu?, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
//                logd("""
//
//                Удаляемый перевод:
//                $item
//
//            """.trimIndent())
                menu?.add("Удалить")?.setOnMenuItemClickListener {
                    onItemDeleteClickListener(item)
//                    logd(item)
                    true
                }
            }
        }

    }
}
