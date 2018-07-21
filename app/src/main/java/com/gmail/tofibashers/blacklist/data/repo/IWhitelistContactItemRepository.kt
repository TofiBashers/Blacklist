package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.WhitelistContactItem
import io.reactivex.Flowable


/**
 * Created by TofiBashers on 13.04.2018.
 */
interface IWhitelistContactItemRepository : IBaseSelectionOperationsRepository<WhitelistContactItem> {

    /**
     * Returns sorted List of [WhitelistContactItem] after subscribe, and after any change.
     * Result [Flowable] doesn't modify any backpressure or scheduling policy/
     */
    fun getAllSortedByNameAscWithChanges() : Flowable<List<WhitelistContactItem>>
}