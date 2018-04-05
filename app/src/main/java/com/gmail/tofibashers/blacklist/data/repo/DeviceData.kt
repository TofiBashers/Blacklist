package com.gmail.tofibashers.blacklist.data.repo

import android.os.Build
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 03.03.2018.
 */
@Singleton
class DeviceData
@Inject
constructor() : IDeviceData {
    override fun isKitkatOrHigherSystemVersion(): Single<Boolean> =
        Single.fromCallable { Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT }
}