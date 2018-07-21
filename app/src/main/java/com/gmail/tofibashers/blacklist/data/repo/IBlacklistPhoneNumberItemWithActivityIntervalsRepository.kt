package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItemWithActivityIntervals
import io.reactivex.Completable
import io.reactivex.Flowable


/**
 * Created by TofiBashers on 30.01.2018.
 */
interface IBlacklistPhoneNumberItemWithActivityIntervalsRepository {

    /**
     * Returns unsorted List of [BlacklistPhoneNumberItemWithActivityIntervals] every time, when it changed on data storage.
     * Result [Flowable] doesn't modify backpressure strategies and schedulers, never calls onComplete()
     */
    fun getAllWithChanges(): Flowable<List<BlacklistPhoneNumberItemWithActivityIntervals>>

    /**
     * Insert or update [BlacklistPhoneNumberItemWithActivityIntervals]
     * Result [Completable] doesn't specify schedulers.
     */
    fun put(itemWithIntervals: BlacklistPhoneNumberItemWithActivityIntervals): Completable
}