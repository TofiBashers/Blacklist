package com.gmail.tofibashers.blacklist.data.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import com.gmail.tofibashers.blacklist.data.db.table_constants.*
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 28.04.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
@Entity(tableName = JoinBlacklistContactPhoneItemActivityIntervalTable.TABLE_NAME,
        primaryKeys = arrayOf(
                JoinBlacklistContactPhoneItemActivityIntervalTable.BLACKLIST_CONTACT_PHONE_ITEM_ID,
                JoinBlacklistContactPhoneItemActivityIntervalTable.ACTIVITY_INTERVAL_ID),
        foreignKeys =
        arrayOf(
                ForeignKey(entity = DbActivityInterval::class,
                        parentColumns = arrayOf(ActivityIntervalTable._ID),
                        childColumns = arrayOf(JoinBlacklistContactPhoneItemActivityIntervalTable.ACTIVITY_INTERVAL_ID)),
                ForeignKey(entity = DbBlacklistContactPhoneItem::class,
                        parentColumns = arrayOf(BlacklistContactPhoneItemTable._ID),
                        childColumns = arrayOf(JoinBlacklistContactPhoneItemActivityIntervalTable.BLACKLIST_CONTACT_PHONE_ITEM_ID))
        ))
data class DbJoinBlacklistContactPhoneItemActivityInterval(

        @ColumnInfo(name = JoinBlacklistContactPhoneItemActivityIntervalTable.BLACKLIST_CONTACT_PHONE_ITEM_ID)
        var blacklistContactPhoneItemId: Long,

        @ColumnInfo(name = JoinBlacklistContactPhoneItemActivityIntervalTable.ACTIVITY_INTERVAL_ID)
        var activityIntervalId: Long)