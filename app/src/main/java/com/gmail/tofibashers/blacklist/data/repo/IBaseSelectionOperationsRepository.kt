package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import io.reactivex.Completable
import io.reactivex.Maybe


/**
 * Created by TofiBashers on 13.04.2018.
 */
interface IBaseSelectionOperationsRepository<T> {

    /**
     * Remove value stored as "selected", if not stored - do nothing and completes.
     * Result [Completable] doesn't specify schedulers.
     */
    fun removeSelected(): Completable

    /**
     * Get value stored as "selected", if exists.
     * @return - [Maybe] with list, if it exists. Otherwise, empty [Maybe]
     * Result [Maybe] doesn't specify schedulers.
     */
    fun getSelected(): Maybe<T>

    /**
     * Insert or update value stored as "selected"
     * Result [Completable] doesn't specify schedulers.
     */
    fun putSelected(item: T): Completable

}