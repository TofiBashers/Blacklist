package com.gmail.tofibashers.blacklist.data.db.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 01.05.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class DbBlacklistContactPhoneWithActivityIntervals(var dbBlacklistContactPhone: DbBlacklistContactPhoneItem,
                                                        var activityIntervals: List<DbActivityInterval>)