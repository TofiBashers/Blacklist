package com.gmail.tofibashers.blacklist.data.db.entity

import android.arch.persistence.room.*
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistItemTable


/**
 * Created by TofiBashers on 18.01.2018.
 */

@Entity(tableName = BlacklistItemTable.TABLE_NAME,
        indices = arrayOf(Index(value = BlacklistItemTable.NUMBER, unique = true))
)
data class DbBlacklistItem(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = BlacklistItemTable._ID)
        val id: Long? = null,

        @ColumnInfo(name = BlacklistItemTable.NUMBER)
        val number: String,

        @ColumnInfo(name = BlacklistItemTable.IGNORE_CALLS)
        val ignoreCalls: Boolean,

        @ColumnInfo(name = BlacklistItemTable.IGNORE_SMS)
        val ignoreSms: Boolean
){
        /**
         * Only for combined classes in Room generated code. Not call manually!
         */
        @Ignore
        constructor() : this(null, "", false, false)
}