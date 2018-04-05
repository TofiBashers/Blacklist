package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.BlacklistItemWithActivityIntervals
import io.reactivex.Completable
import io.reactivex.Flowable


/**
 * Created by TofiBashers on 30.01.2018.
 */
interface IBlacklistItemWithActivityIntervalsRepository {

    /**
     * Returns unsorted List of [BlacklistItemWithActivityIntervals] every time, when it changed on data storage.
     * Result [Flowable] doesn't modify backpressure strategies and schedulers, never calls onComplete()
     */
    fun getAllWithChanges(): Flowable<List<BlacklistItemWithActivityIntervals>>

    /**
     * Insert or update [BlacklistItemWithActivityIntervals]
     * Result [Completable] doesn't specify schedulers.
     */
    fun put(itemWithIntervals: BlacklistItemWithActivityIntervals): Completable
}