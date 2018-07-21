package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe


/**
 * Created by TofiBashers on 13.04.2018.
 */
interface IBlacklistContactItemRepository  : IBaseSelectionOperationsRepository<BlacklistContactItem> {

    /**
     * @return - [Maybe] with [BlacklistContactItem], if exists, otherwise empty. Result [Maybe] doesn't specify schedulers.
     */
    fun getByDbId(dbId: Long? = null) : Maybe<BlacklistContactItem>

    /**
     * Returns sorted List of [BlacklistContactItem] after subscribe, and after any change.
     * Result [Flowable] doesn't modify any backpressure or scheduling policy/
     */
    fun getAllSortedByNameAscWithChanges() : Flowable<List<BlacklistContactItem>>

    /**
     * Removes [item] with all associated and unused entities.
     * @return - [Completable] when item successfully deleted.
     * Result [Completable] doesn't specify schedulers.
     */
    fun delete(item: BlacklistContactItem) : Completable
}