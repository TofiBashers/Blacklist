package com.gmail.tofibashers.blacklist.data.repo

import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


/**
 * Created by TofiBashers on 29.01.2018.
 */
interface IPreferencesData {

    /**
     * Get param and observe all changes. Not produce any errors to stream, and never completes.
     * Result [Flowable] use [BackpressureStrategy.LATEST], doesn't modify schedulers.
     */
    fun getIgnoreHiddenNumbersWithChanges() : Flowable<Boolean>

    /**
     * Result [Single] doesn't specify schedulers.
     */
    fun getIgnoreHiddenNumbers() : Single<Boolean>

    /**
     * Set new or reset current value
     * Result [Completable] doesn't specify schedulers.
     */
    fun setIgnoreHiddenNumbers(ignoreHiddenNumber: Boolean) : Completable

    /**
     * Result [Single] doesn't specify schedulers.
     */
    fun getIsFirstTimeLaunchBeforeKitkat() : Single<Boolean>

    /**
     * Result [Completable] doesn't specify schedulers.
     */
    fun setIsFirstTimeLaunchBeforeKitkatFalse() : Completable

    /**
     * Result [Single] doesn't specify schedulers.
     */
    fun getIsFirstTimeLaunchOnKitkatOrHigher() : Single<Boolean>

    /**
     * Result [Completable] doesn't specify schedulers.
     */
    fun setIsFirstTimeLaunchOnKitkatOrHigherFalse() : Completable
}