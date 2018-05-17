package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single


/**
 * Created by TofiBashers on 16.01.2018.
 */
interface IActivityIntervalRepository {

    /**
     * Get value by , if exists.
     * Result [Maybe] doesn't specify schedulers.
     * @return - [Maybe] with value, if value exists. Otherwise, empty [Maybe]
     */
    fun getActivityIntervalsAssociatedWithBlacklistItem(phoneNumberItem: BlacklistPhoneNumberItem): Maybe<List<ActivityInterval>>

    /**
     * Get value by , if exists.
     * Result [Single] doesn't specify schedulers.
     */
    @Deprecated("not need")
    fun getActivityIntervalsAssociatedWithBlacklistContactItem(contactItem: BlacklistContactItem): Single<List<ActivityInterval>>

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

    /**
     * Remove multiple intervals lists, stored as "selected" (with [putSelectedMultipleActivityIntervalsLists]), if not stored - do nothing and completes.
     * Result [Completable] doesn't specify schedulers.
     */
    @Deprecated("all logic replaced by BlacklistContactPhoneWithActivityIntervalsRepository")
    fun removeSelectedMultipleActivityIntervalsLists(): Completable

    /**
     * Get multiple intervals lists, stored as "selected" (with [putSelectedMultipleActivityIntervalsLists]), if exists.
     * Result [Maybe] doesn't specify schedulers.
     * @return - [Maybe] with list, if it exists. Otherwise, empty [Maybe]
     */
    @Deprecated("all logic replaced by BlacklistContactPhoneWithActivityIntervalsRepository")
    fun getSelectedMultipleActivityIntervalsLists(): Maybe<List<List<ActivityInterval>>>

    /**
     * Insert or update multiple intervals lists, stored as "selected" (with [putSelectedMultipleActivityIntervalsLists])
     * Result [Completable] doesn't specify schedulers.
     */
    @Deprecated("all logic replaced by BlacklistContactPhoneWithActivityIntervalsRepository")
    fun putSelectedMultipleActivityIntervalsLists(activityIntervalsLists: List<List<ActivityInterval>>): Completable

}