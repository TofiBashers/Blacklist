package com.gmail.tofibashers.blacklist.data.db.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.gmail.tofibashers.blacklist.data.db.table_constants.*


/**
 * Created by TofiBashers on 07.05.2018.
 */
data class DbBlacklistContactWithPhonesWithJoinBlacklistContactPhoneItemActivityInterval(
        
        @Embedded
        var blacklistContactItem: DbBlacklistContactItem,

        @Relation(parentColumn = BlacklistContactItemTable._ID,
                entityColumn = BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID,
                entity = DbBlacklistContactPhoneItem::class)
        var listOfPhonesWithJoins: List<DbBlacklistContactPhoneWithJoinBlacklistContactPhoneItemActivityInterval>) {

    /**
     * Used only in Room generated code. Not call manually!
     */
    constructor() : this(DbBlacklistContactItem(), emptyList())
}