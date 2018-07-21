package com.gmail.tofibashers.blacklist.data.db.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistContactPhoneItemTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.JoinBlacklistContactPhoneItemActivityIntervalTable


/**
 * Created by TofiBashers on 07.05.2018.
 */
data class DbBlacklistContactPhoneWithJoinBlacklistContactPhoneItemActivityInterval(

        @Embedded
        var blacklistContactPhoneItem: DbBlacklistContactPhoneItem,

        @Relation(parentColumn = BlacklistContactPhoneItemTable._ID,
                entityColumn = JoinBlacklistContactPhoneItemActivityIntervalTable.BLACKLIST_CONTACT_PHONE_ITEM_ID)
        var listOfJoins: List<DbJoinBlacklistContactPhoneItemActivityInterval>) {

    /**
     * Used only in Room generated code. Not call manually!
     */
    constructor() : this(DbBlacklistContactPhoneItem(), emptyList())
}