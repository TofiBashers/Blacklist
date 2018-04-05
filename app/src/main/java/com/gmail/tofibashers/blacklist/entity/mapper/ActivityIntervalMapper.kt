package com.gmail.tofibashers.blacklist.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbActivityInterval
import com.gmail.tofibashers.blacklist.data.memory.MemoryActivityInterval
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 27.01.2018.
 */
@Singleton
class ActivityIntervalMapper
@Inject
constructor(){

    fun toDbActivityInterval(interval: ActivityInterval) : DbActivityInterval =
            DbActivityInterval(interval.dbId,
                    interval.weekDayId,
                    interval.beginTime,
                    interval.endTime)

    fun toDbActivityIntervalsList(intervals: List<ActivityInterval>) : List<DbActivityInterval> =
            intervals.map { toDbActivityInterval(it) }

    fun toMemoryActivityInterval(interval: ActivityInterval) : MemoryActivityInterval =
            MemoryActivityInterval(interval.dbId,
                    interval.weekDayId,
                    interval.beginTime,
                    interval.endTime)

    fun toMemoryActivityIntervalsList(intervals: List<ActivityInterval>) : List<MemoryActivityInterval> =
            intervals.map { toMemoryActivityInterval(it) }
}