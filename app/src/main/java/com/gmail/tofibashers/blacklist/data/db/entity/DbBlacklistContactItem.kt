package com.gmail.tofibashers.blacklist.data.db.entity

import android.arch.persistence.room.*
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistContactItemTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistItemTable


/**
 * Created by TofiBashers on 28.04.2018.
 */
@Entity(tableName = BlacklistContactItemTable.TABLE_NAME)
data class DbBlacklistContactItem(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = BlacklistContactItemTable._ID)
        val id: Long? = null,

        @ColumnInfo(name = BlacklistContactItemTable.DEVICE_DB_ID)
        val deviceDbId: Long? = null,

        @ColumnInfo(name = BlacklistContactItemTable.DEVICE_LOOKUP_KEY)
        val deviceLookupKey: String? = null,

        @ColumnInfo(name = BlacklistContactItemTable.NAME)
        val name: String,

        @ColumnInfo(name = BlacklistContactItemTable.PHOTO_URL)
        val photoUrl: String? = null
){
        /**
         * Only for combined classes with empty constructor. Not call manually in business logic!
         */
        @Ignore
        constructor() : this(name = "")
}