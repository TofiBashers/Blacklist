package com.gmail.tofibashers.blacklist.data.db.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItem
import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 02.02.2018.
 */

@Singleton
class DbBlacklistItemMapper
@Inject
constructor(){

    fun toBlacklistItem(item: DbBlacklistItem) : BlacklistItem =
            BlacklistItem(item.id,
                    item.number,
                    item.ignoreCalls,
                    item.ignoreSms)

    fun toBlacklistItemsList(items: List<DbBlacklistItem>) : List<BlacklistItem> =
            items.map { toBlacklistItem(it) }
}