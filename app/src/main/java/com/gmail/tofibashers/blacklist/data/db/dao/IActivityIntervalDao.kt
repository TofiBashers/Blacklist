package com.gmail.tofibashers.blacklist.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.gmail.tofibashers.blacklist.data.db.entity.DbActivityInterval
import com.gmail.tofibashers.blacklist.data.db.table_constants.ActivityIntervalTable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.joda.time.LocalTime


/**
 * Created by TofiBashers on 20.01.2018.
 */
@Dao
abstract class IActivityIntervalDao {

    fun insertActivityIntervalWithGetIdAsSingle(dbActivityInterval: DbActivityInterval) : Single<Long> =
            Single.fromCallable { insertActivityIntervalWithGetId(dbActivityInterval) }

    /**
     * Return [Single] with updated rows count
     */
    fun updateActivityIntervalAsSingle(dbActivityInterval: DbActivityInterval) : Single<Int> =
            Single.fromCallable { updateActivityInterval(dbActivityInterval) }

    @Query("SELECT * FROM " + ActivityIntervalTable.TABLE_NAME
            + " WHERE " + ActivityIntervalTable._ID + " = :id")
    fun getActivityIntervalByIdWithChanges(id: Long?) : Flowable<DbActivityInterval> =
            getActivityIntervalByIdWithTableChanges(id).distinctUntilChanged()

    @Insert
    abstract fun insertActivityIntervalWithGetId(dbActivityInterval: DbActivityInterval) : Long

    /**
     * Return updated rows count
     */
    @Update
    abstract fun updateActivityInterval(dbActivityInterval: DbActivityInterval) : Int

    @Query("SELECT * FROM " + ActivityIntervalTable.TABLE_NAME
            + " WHERE " + ActivityIntervalTable._ID + " = :id")
    abstract fun getActivityIntervalByIdOrException(id: Long?) : Single<DbActivityInterval>

    @Query("SELECT * FROM " + ActivityIntervalTable.TABLE_NAME
            + " WHERE " + ActivityIntervalTable.WEEKDAY_ID + " = :weekdayId"
            + " AND " + ActivityIntervalTable.BEGIN_TIME + " = :beginTime"
            + " AND " + ActivityIntervalTable.END_TIME + " = :endTime")
    abstract fun getActivityIntervalByTimesAndWeekday(weekdayId: Int, beginTime: LocalTime, endTime: LocalTime)
            : Maybe<DbActivityInterval>

    @Query("SELECT * FROM " + ActivityIntervalTable.TABLE_NAME
            + " WHERE " + ActivityIntervalTable._ID + " = :id")
    protected abstract fun getActivityIntervalByIdWithTableChanges(id: Long?) : Flowable<DbActivityInterval>
}