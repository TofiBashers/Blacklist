package com.gmail.tofibashers.blacklist.data.device

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 01.05.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class DeviceContactItem(var id: Long? = null,
                             var lookupKey: String? = null,
                             var name: String,
                             var photoUrl: String? = null)