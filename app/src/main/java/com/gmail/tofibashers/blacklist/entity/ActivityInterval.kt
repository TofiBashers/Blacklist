package com.gmail.tofibashers.blacklist.entity

import android.support.annotation.IntRange
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.gmail.tofibashers.blacklist.utils.TimeFormatUtils
import com.google.auto.factory.AutoFactory
import org.joda.time.LocalTime

/**
 * Created by TofixXx on 06.09.2015.
 */

@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class ActivityInterval(
        var dbId: Long? = null,

        /**
         * Id of weekday, where 1 id of Sunday, 7 for Saturday
         */
        @param:IntRange(from=1, to=7)
        @get:IntRange(from=1, to=7)
        @setparam:IntRange(from=1, to=7)
        var weekDayId: Int,
        var beginTime: LocalTime,
        var endTime: LocalTime) {

        constructor(weekDayId: Int) : this(weekDayId = weekDayId,
                beginTime = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME,
                endTime = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME)
}
