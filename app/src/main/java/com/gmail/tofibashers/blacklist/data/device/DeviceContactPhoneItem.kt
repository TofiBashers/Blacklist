package com.gmail.tofibashers.blacklist.data.device

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 01.05.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class DeviceContactPhoneItem(var id: Long? = null,
                                  var number: String)