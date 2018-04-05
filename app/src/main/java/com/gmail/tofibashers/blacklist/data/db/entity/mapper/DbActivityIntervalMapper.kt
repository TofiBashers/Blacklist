package com.gmail.tofibashers.blacklist.data.db.entity.mapper

import com.gmail.tofibashers.blacklist.data.db.entity.DbActivityInterval
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 02.02.2018.
 */

@Singleton
class DbActivityIntervalMapper
@Inject
constructor(){

    fun toActivityInterval(interval: DbActivityInterval) : ActivityInterval =
            ActivityInterval(interval.id,
                    interval.dayOfWeek,
                    interval.beginTime,
                    interval.endTime)

    fun toActivityIntervalsList(intervals: List<DbActivityInterval>) : List<ActivityInterval> =
            intervals.map { toActivityInterval(it) }
}