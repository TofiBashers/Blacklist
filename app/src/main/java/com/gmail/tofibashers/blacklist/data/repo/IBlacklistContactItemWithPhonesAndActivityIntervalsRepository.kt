package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.BlacklistContactItemWithPhonesAndIntervals
import com.gmail.tofibashers.blacklist.entity.BlacklistItemWithActivityIntervals
import io.reactivex.Completable
import io.reactivex.Flowable


/**
 * Created by TofiBashers on 20.04.2018.
 */
interface IBlacklistContactItemWithPhonesAndActivityIntervalsRepository {

    /**
     * Returns unsorted List of [BlacklistContactItemWithPhonesAndIntervals] every time, when it changed on data storage.
     * Result [Flowable] doesn't modify backpressure strategies and schedulers, never calls onComplete()
     */
    fun getAllWithChanges(): Flowable<List<BlacklistContactItemWithPhonesAndIntervals>>

    /**
     * Insert or update [BlacklistContactItemWithPhonesAndIntervals]
     * Result [Completable] doesn't specify schedulers.
     */
    fun put(itemWithPhonesAndIntervals: BlacklistContactItemWithPhonesAndIntervals): Completable
}