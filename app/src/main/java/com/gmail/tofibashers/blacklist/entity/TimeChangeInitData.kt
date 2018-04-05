package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory
import org.joda.time.LocalTime


/**
 * Created by TofiBashers on 14.03.2018.
 */

@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class TimeChangeInitData(val position: Int,
                              val isBeginTimeToChange: Boolean,
                              val selectableTimes: List<LocalTime>,
                              val initTime: LocalTime)