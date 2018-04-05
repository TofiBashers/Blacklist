package com.gmail.tofibashers.blacklist.data.repo

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
}