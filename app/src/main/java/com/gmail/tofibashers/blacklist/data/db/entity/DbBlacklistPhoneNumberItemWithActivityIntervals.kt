package com.gmail.tofibashers.blacklist.data.db.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 02.02.2018.
 */

@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
class DbBlacklistPhoneNumberItemWithActivityIntervals(
        var dbBlacklistPhoneNumberItem: DbBlacklistPhoneNumberItem,
        var dbActivityIntervals: List<DbActivityInterval>)