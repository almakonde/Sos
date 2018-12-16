package msk.android.academy.javatemplate.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import msk.android.academy.javatemplate.Optional
import msk.android.academy.javatemplate.presentation.useractions.models.AlarmAction
import msk.android.academy.javatemplate.presentation.useractions.models.Contact

class Repository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    private val map = LinkedHashMap<Long, AlarmAction>()
    private val gson = Gson()

    init {
        //достать все объекты
        val result = prefs.getString(KEY_OP, "")

        gson.fromJson<List<AlarmAction>>(result, object : TypeToken<List<AlarmAction>>() {

        }.type)?.forEach {
            map[it.id] = it
        }
    }

    companion object {
        val KEY_OP = "key_op"
        val KEY_SELECTED = "key_selected"
    }

    @SuppressLint("ApplySharedPref")
    fun updateAction(actionModel: AlarmAction): AlarmAction {
        map[actionModel.id] = actionModel
        val result = gson.toJson(map.values)
        prefs.edit().putString(KEY_OP, result).commit()
        return actionModel
    }

    fun getActions(): List<AlarmAction> {
        return map.values.toList()
    }

    fun getActionById(id: Long): Optional<AlarmAction> {
        return Optional.ofNullable(map[id])
    }

    @SuppressLint("ApplySharedPref")
    fun setAsActive(alarmAction: AlarmAction) {
        prefs.edit().putLong(KEY_SELECTED, alarmAction.id).commit()
    }

    fun getActive(): Optional<AlarmAction> {
        return Optional.ofNullable(map[prefs.getLong(KEY_SELECTED, 0)])
    }

    fun getContactsByAlarmId(id: Long?): List<Contact> {
        return if (id != null) {
            map[id]?.model?.contacts ?: emptyList()
        } else emptyList()
    }

    fun deleteAction(alarmModelId: Long?) {
        if (alarmModelId != null) {
            map.remove(alarmModelId)
            val result = gson.toJson(map.values)
            prefs.edit().putString(KEY_OP, result).commit()
        }
    }
}
