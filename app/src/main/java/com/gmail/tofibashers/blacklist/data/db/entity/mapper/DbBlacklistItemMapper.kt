package com.gmail.tofibashers.blacklist.data.db.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItem
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 02.02.2018.
 */

@Singleton
class DbBlacklistItemMapper
@Inject
constructor(){

    fun toBlacklistItem(item: DbBlacklistItem) : BlacklistPhoneNumberItem =
            BlacklistPhoneNumberItem(item.id,
                    item.number,
                    item.ignoreCalls,
                    item.ignoreSms)

    fun toBlacklistItemsList(items: List<DbBlacklistItem>) : List<BlacklistPhoneNumberItem> =
            items.map { toBlacklistItem(it) }
}