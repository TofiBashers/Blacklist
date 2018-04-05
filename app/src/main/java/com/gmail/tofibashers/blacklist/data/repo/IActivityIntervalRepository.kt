package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import io.reactivex.Completable
import io.reactivex.Maybe


/**
 * Created by TofiBashers on 16.01.2018.
 */
interface IActivityIntervalRepository {

    /**
     * Get value by , if exists.
     * Result [Maybe] doesn't specify schedulers.
     * @return - [Maybe] with value, if value exists. Otherwise, empty [Maybe]
     */
    fun getActivityIntervalsAssociatedWithBlacklistItem(item: BlacklistItem): Maybe<List<ActivityInterval>>

    /**
     * Remove value stored as "selected", if not stored - do nothing and completes.
     * Result [Completable] doesn't specify schedulers.
     */
    fun removeSelectedActivityIntervals(): Completable

    /**
     * Get value stored as "selected", if exists.
     * Result [Maybe] doesn't specify schedulers.
     * @return - [Maybe] with list, if it exists. Otherwise, empty [Maybe]
     */
    fun getSelectedActivityIntervals(): Maybe<List<ActivityInterval>>

    /**
     * Insert or update value stored as "selected"
     * Result [Completable] doesn't specify schedulers.
     */
    fun putSelectedActivityIntervals(activityIntervals: List<ActivityInterval>): Completable
}