package com.gmail.tofibashers.blacklist.data.datasource

import com.gmail.tofibashers.blacklist.data.db.entity.DbActivityInterval
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItem
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItemWithActivityIntervals
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe


/**
 * Created by TofiBashers on 22.01.2018.
 */
interface IDatabaseSource {

    /**
     * Get items firstly and after all changes. Not modify any Backpressure policy.
     * Result [Flowable] doesn't modify backpressure strategies and schedulers, never calls onComplete().
     */
    fun getAllBlackListItemsWithChanges(): Flowable<List<DbBlacklistItem>>

    /**
     * Get items firstly and after all changes. Not modify any Backpressure policy.
     * Result [Flowable] doesn't modify backpressure strategies and schedulers, never calls onComplete().
     */
    fun getAllBlacklistItemsWithIntervalsWithChanges(): Flowable<List<DbBlacklistItemWithActivityIntervals>>

    /**
     * @return [Maybe] with item, if exists, otherwise empty. Result [Maybe] doesn't specify schedulers.
     */
    fun getBlacklistItemByNumber(number: String): Maybe<DbBlacklistItem>

    /**
     * Delete item and clear orphaned [DbActivityInterval]'s formed in operation.
     * Operations execute in transaction.
     * Result [Completable] doesn't specify schedulers.
     */
    fun deleteBlackListItem(dbBlacklistItem: DbBlacklistItem): Completable

    /**
     * Insert or update [DbBlacklistItem], then insert activity intervals if it non persist,
     * and update association with [DbBlacklistItem].
     * if not associated. Comparsion with existent performed by concatenation of [DbActivityInterval.beginTime],
     * [DbActivityInterval.endTime] and [DbActivityInterval.dayOfWeek].
     * Also removes orphaned [DbActivityInterval]'s formed in this operation.
     * Operations execute in transaction.
     * Result [Completable] doesn't specify schedulers.
     * @param itemWithIntervals - contains [DbBlacklistItem] and list of [DbActivityInterval]'s
     */
    fun putBlacklistItemWithActivityIntervals(itemWithIntervals: DbBlacklistItemWithActivityIntervals): Completable

    /**
     * @return [Maybe] with list of items, if exists, otherwise empty. Result [Maybe] doesn't specify schedulers.
     */
    fun getActivityIntervalsAssociatedWithBlacklistItem(blacklistItem: DbBlacklistItem): Maybe<List<DbActivityInterval>>
}