package com.gmail.tofibashers.blacklist.data.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import com.gmail.tofibashers.blacklist.data.db.table_constants.ActivityIntervalTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistItemTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.JoinBlacklistItemActivityIntervalTable
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 26.01.2018.
 */

@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
@Entity(tableName = JoinBlacklistItemActivityIntervalTable.TABLE_NAME,
        primaryKeys = arrayOf(JoinBlacklistItemActivityIntervalTable.BLACKLIST_ITEM_ID,
                JoinBlacklistItemActivityIntervalTable.ACTIVITY_INTERVAL_ID),
        foreignKeys =
            arrayOf(
                    ForeignKey(entity = DbActivityInterval::class,
                            parentColumns = arrayOf(ActivityIntervalTable._ID),
                            childColumns = arrayOf(JoinBlacklistItemActivityIntervalTable.ACTIVITY_INTERVAL_ID)),
                    ForeignKey(entity = DbBlacklistPhoneNumberItem::class,
                            parentColumns = arrayOf(BlacklistItemTable._ID),
                            childColumns = arrayOf(JoinBlacklistItemActivityIntervalTable.BLACKLIST_ITEM_ID))
            ))
data class DbJoinBlacklistPhoneNumberItemActivityInterval(

        @ColumnInfo(name = JoinBlacklistItemActivityIntervalTable.BLACKLIST_ITEM_ID)
        var blacklistItemId: Long,

        @ColumnInfo(name = JoinBlacklistItemActivityIntervalTable.ACTIVITY_INTERVAL_ID)
        var activityIntervalId: Long)