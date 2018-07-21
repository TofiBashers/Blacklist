package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Created by TofiBashers on 14.01.2018.
 */
interface IBlacklistPhoneNumberItemRepository {

    /**
     * Returns unsorted List of [BlacklistPhoneNumberItem] every time, when it changed on data storage
     * Result [Flowable] doesn't modify backpressure strategies and schedulers, never calls onComplete()
     */
    fun getAllWithChanges(): Flowable<List<BlacklistPhoneNumberItem>>

    /**
     * @return - [Maybe] with phoneNumberItem, if exists, otherwise empty. Result [Maybe] doesn't specify schedulers.
     */
    fun getByNumber(number: String): Maybe<BlacklistPhoneNumberItem>

    /**
     * Removes [BlacklistPhoneNumberItem] with all associated and unused entities.
     * Result [Completable] doesn't specify schedulers.
     */
    fun deleteBlacklistPhoneNumberItem(blacklistPhoneNumberItem: BlacklistPhoneNumberItem): Completable

    /**
     * Remove phoneNumberItem stored as "selected", if exists. Otherwise, do nothing
     * Result [Completable] doesn't specify schedulers.
     */
    fun removeSelectedBlacklistPhoneNumberItem(): Completable

    /**
     * Get phoneNumberItem stored as "selected", if exists.
     * Result [Maybe] doesn't specify schedulers.
     * @return - [Maybe] with value, if value exists. Otherwise, empty [Maybe]
     */
    fun getSelectedBlacklistPhoneNumberItem(): Maybe<BlacklistPhoneNumberItem>

    /**
     * Insert or update phoneNumberItem, stored as "selected"
     * Result [Completable] doesn't specify schedulers.
     */
    fun putSelectedBlacklistPhoneNumberItem(blacklistPhoneNumberItem: BlacklistPhoneNumberItem): Completable
}
