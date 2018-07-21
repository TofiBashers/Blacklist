package com.gmail.tofibashers.blacklist.data.db.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 02.02.2018.
 */

@Singleton
class DbBlacklistPhoneNumberItemMapper
@Inject
constructor(){

    fun toBlacklistPhoneNumberItemItem(item: DbBlacklistPhoneNumberItem) : BlacklistPhoneNumberItem =
            BlacklistPhoneNumberItem(item.id,
                    item.number,
                    item.ignoreCalls,
                    item.ignoreSms)

    fun toBlacklistPhoneNumberItemsList(items: List<DbBlacklistPhoneNumberItem>) : List<BlacklistPhoneNumberItem> =
            items.map { toBlacklistPhoneNumberItemItem(it) }
}