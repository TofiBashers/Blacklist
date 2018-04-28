package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 13.04.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class WhitelistContactItem(var deviceDbId: Long,
                                var deviceKey: String,
                                var name: String,
                                var photoUrl: String)