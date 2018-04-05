package com.gmail.tofibashers.blacklist.data.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.gmail.tofibashers.blacklist.data.db.table_constants.ActivityIntervalTable
import org.joda.time.LocalTime

/**
 * Created by TofiBashers on 18.01.2018.
 */

@Entity(tableName = ActivityIntervalTable.TABLE_NAME)
data class DbActivityInterval(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = ActivityIntervalTable._ID)
        val id: Long? = null,

        /**
         * Ordered number of weekday, where 1 corresponds of Sunday, 7 to Saturday
         */
        @ColumnInfo(name = ActivityIntervalTable.WEEKDAY_ID)
        val dayOfWeek: Int,

        /**
         * Time saved as [String] in "hh:mm" format (ISO-8601)
         */
        @ColumnInfo(name = ActivityIntervalTable.BEGIN_TIME)
        val beginTime: LocalTime,

        /**
         * Time saved as [String] in "hh:mm" format (ISO-8601)
         */
        @ColumnInfo(name = ActivityIntervalTable.END_TIME)
        val endTime: LocalTime)
