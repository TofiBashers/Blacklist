package com.gmail.tofibashers.blacklist.data.memory.mapper

import com.gmail.tofibashers.blacklist.data.memory.MemoryActivityInterval
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 06.03.2018.
 */
@Singleton
class MemoryActivityIntervalMapper
@Inject
constructor(){

    fun toActivityInterval(interval: MemoryActivityInterval) : ActivityInterval =
            ActivityInterval(interval.dbId,
                    interval.weekDayId,
                    interval.beginTime,
                    interval.endTime)

    fun toActivityIntervalsList(intervals: List<MemoryActivityInterval>) : List<ActivityInterval> =
            intervals.map { toActivityInterval(it) }
}