package com.gmail.tofibashers.blacklist.data.repo

import android.os.Build
import android.support.annotation.MainThread
import android.support.v4.os.LocaleListCompat
import android.telephony.PhoneStateListener
import android.telephony.ServiceState
import android.telephony.TelephonyManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 03.03.2018.
 */
@Singleton
class DeviceData
@Inject
constructor(private val telephonyManager: TelephonyManager) : IDeviceData {

    override fun isKitkatOrHigherSystemVersion(): Single<Boolean> =
        Single.fromCallable { Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT }

    @MainThread
    override fun getCelluarNetworkOrLocalCountryCodeWithChanges(): Flowable<String> {
        return Flowable.create({ emitter: FlowableEmitter<String> ->
            getAndEmitCodeIfNotCancelled(emitter)
            val listener = object : PhoneStateListener() {
                override fun onServiceStateChanged(serviceState: ServiceState?) {
                    getAndEmitCodeIfNotCancelled(emitter)
                }
            }
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_SERVICE_STATE)
            emitter.setCancellable {
                telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE)
            }
        }, BackpressureStrategy.LATEST)
                .distinctUntilChanged()
    }

    private fun getAndEmitCodeIfNotCancelled(emitter: FlowableEmitter<String>) {
        if(!emitter.isCancelled){
            emitter.onNext(getSimCodeOrResource())
        }
    }

    private fun getSimCodeOrResource() : String {
        return if(telephonyManager.networkCountryIso.isNotBlank()) telephonyManager.networkCountryIso
        else LocaleListCompat.getAdjustedDefault()[0].country
    }
}