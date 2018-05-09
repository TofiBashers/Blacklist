package com.gmail.tofibashers.blacklist.data.db.entity

import android.arch.persistence.room.*
import com.gmail.tofibashers.blacklist.data.db.table_constants.*
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 28.04.2018.
 */
@Entity(tableName = BlacklistContactPhoneItemTable.TABLE_NAME,
        foreignKeys =
        arrayOf(
                ForeignKey(entity = DbBlacklistContactItem::class,
                        parentColumns = arrayOf(BlacklistContactItemTable._ID),
                        childColumns = arrayOf(BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID))
        ),
        indices = arrayOf(Index(value = BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID, unique = true)))
data class DbBlacklistContactPhoneItem(

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = BlacklistContactPhoneItemTable._ID)
        val id: Long? = null,

        @ColumnInfo(name = BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID)
        var blacklistContactId: Long? = null,

        @ColumnInfo(name = BlacklistContactPhoneItemTable.DEVICE_DB_ID)
        val deviceDbId: Long? = null,

        @ColumnInfo(name = BlacklistContactPhoneItemTable.NUMBER)
        val number: String,

        @ColumnInfo(name = BlacklistContactPhoneItemTable.IGNORE_CALLS)
        val ignoreCalls: Boolean,

        @ColumnInfo(name = BlacklistContactPhoneItemTable.IGNORE_SMS)
        val ignoreSms: Boolean) {

        /**
         * Only for combined classes with empty constructor. Not call manually in business logic!
         */
        @Ignore
        constructor() : this(blacklistContactId = -1, number = "", ignoreSms = true, ignoreCalls = true)
}