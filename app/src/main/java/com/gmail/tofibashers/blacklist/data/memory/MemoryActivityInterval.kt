package com.gmail.tofibashers.blacklist.data.memory

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory
import org.joda.time.LocalTime


/**
 * Created by TofiBashers on 27.01.2018.
 */

@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class MemoryActivityInterval (
        var dbId: Long? = null,

        /**
         * Id of weekday, where 1 id of Sunday, 7 for Saturday
         */
        var weekDayId: Int,
        var beginTime: LocalTime,
        var endTime: LocalTime)