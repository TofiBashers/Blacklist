package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Created by TofiBashers on 14.01.2018.
 */
interface IBlacklistItemRepository {

    /**
     * Returns unsorted List of [BlacklistItem] every time, when it changed on data storage
     * Result [Flowable] doesn't modify backpressure strategies and schedulers, never calls onComplete()
     */
    fun getAllWithChanges(): Flowable<List<BlacklistItem>>

    /**
     * @return - [Maybe] with item, if exists, otherwise empty. Result [Maybe] doesn't specify schedulers.
     */
    fun getByNumber(number: String): Maybe<BlacklistItem>

    /**
     * Removes [BlacklistItem] with all associated and unused entities.
     * Result [Completable] doesn't specify schedulers.
     */
    fun deleteBlacklistItem(blacklistItem: BlacklistItem): Completable

    /**
     * Remove item stored as "selected", if exists. Otherwise, do nothing
     * Result [Completable] doesn't specify schedulers.
     */
    fun removeSelectedBlackListItem(): Completable

    /**
     * Get item stored as "selected", if exists.
     * Result [Maybe] doesn't specify schedulers.
     * @return - [Maybe] with value, if value exists. Otherwise, empty [Maybe]
     */
    fun getSelectedBlackListItem(): Maybe<BlacklistItem>

    /**
     * Insert or update item, stored as "selected"
     * Result [Completable] doesn't specify schedulers.
     */
    fun putSelectedBlackListItem(blacklistItem: BlacklistItem): Completable
}
