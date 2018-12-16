package msk.android.academy.javatemplate.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

import io.reactivex.Single
import msk.android.academy.javatemplate.JavaUnit
import msk.android.academy.javatemplate.presentation.useractions.models.AlarmAction

@Dao
interface ActionsRepository {

    @Query("SELECT * FROM alarmaction")
    fun getAll(): Single<List<AlarmAction>>

    @Query("SELECT * FROM alarmaction WHERE id = :id")
    fun getById(id: Long): Single<AlarmAction>

    @Insert
    fun insert(alarmAction: AlarmAction): Long

    @Update
    fun update(alarmAction: AlarmAction): Int

    @Update
    fun updateSelected(alarmAction: AlarmAction): Int

    @Delete
    fun delete(alarmAction: AlarmAction): Int
}
