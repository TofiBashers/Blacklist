package com.gmail.tofibashers.blacklist.data.repo

import android.support.annotation.MainThread
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single


/**
 * Created by TofiBashers on 03.03.2018.
 */
interface IDeviceData {

    /**
     * @return Single with true if device version is Kitkat or higher, false - otherwise.
     *
     * Result [Single] doesn't specify schedulers.
     */
    fun isKitkatOrHigherSystemVersion() : Single<Boolean>

    /**
     * @return [Flowable], that provides result instantly after subscribe, and after any change, never calls
     * onComplete().
     * Result [Flowable] has [BackpressureStrategy.LATEST], doesn't specify any schedulers.
     */
    @MainThread
    fun getCelluarNetworkOrLocalCountryCodeWithChanges() : Flowable<String>
}