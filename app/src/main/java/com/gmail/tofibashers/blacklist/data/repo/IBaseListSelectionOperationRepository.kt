package com.gmail.tofibashers.blacklist.data.repo

import io.reactivex.Completable
import io.reactivex.Maybe


/**
 * Created by TofiBashers on 10.05.2018.
 */
interface IBaseListSelectionOperationRepository<T> {

    /**
     * Remove values list stored as "selected", if not stored - do nothing and completes.
     * Result [Completable] doesn't specify schedulers.
     */
    fun removeSelectedList(): Completable

    /**
     * Get values stored as "selected", if exists.
     * @return - [Maybe] with list, if it exists. Otherwise, empty [Maybe]
     * Result [Maybe] doesn't specify schedulers.
     */
    fun getSelectedList(): Maybe<List<T>>

    /**
     * Insert or update value stored as "selected"
     * Result [Completable] doesn't specify schedulers.
     */
    fun putSelectedList(items: List<T>): Completable
}