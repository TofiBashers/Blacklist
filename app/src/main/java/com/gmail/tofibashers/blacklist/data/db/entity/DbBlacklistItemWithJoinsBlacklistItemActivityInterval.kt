package com.gmail.tofibashers.blacklist.data.db.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistItemTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.JoinBlacklistItemActivityIntervalTable


/**
 * Created by TofiBashers on 03.02.2018.
 */
data class DbBlacklistItemWithJoinsBlacklistItemActivityInterval(
        @Embedded
        var blacklistItem: DbBlacklistItem,

        @Relation(parentColumn = BlacklistItemTable._ID,
                entityColumn = JoinBlacklistItemActivityIntervalTable.BLACKLIST_ITEM_ID)
        var listOfJoins: List<DbJoinBlacklistItemActivityInterval>) {

        /**
         * Used only in Room generated code. Not call manually!
         */
        constructor() : this(DbBlacklistItem(), emptyList())
}