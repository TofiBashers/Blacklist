package com.gmail.tofibashers.blacklist

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.telephony.SmsMessage
import android.telephony.TelephonyManager
import android.util.Log
import com.android.internal.telephony.ITelephony
import com.gmail.tofibashers.blacklist.domain.IGetAllIgnoredInfoOptimizedForAccessWithChangesUseCase
import com.gmail.tofibashers.blacklist.domain.ICheckNumberMustBeIgnoredNowSyncUseCase
import dagger.android.DaggerService
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject


class SmsAndCallsTrackingService : DaggerService() {

    private var compositeSubscription = CompositeDisposable()
    private var ignoreHiddenNumbers: Boolean? = null
    private var ignoredNumbersWithTimeAndSettings: HashMap<String, TimeAndIgnoreSettingsByWeekdayId>? = null

    @Inject
    lateinit var syncCheckNumberMustBeIgnoredNowUseCase: ICheckNumberMustBeIgnoredNowSyncUseCase

    @Inject
    lateinit var getIgnoredNumbersWithTimeAndSettingsUseCase: IGetAllIgnoredInfoOptimizedForAccessWithChangesUseCase

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = Service.START_STICKY

    override fun onCreate() {
        super.onCreate()
        Log.i(LOG_TAG, "Service created")
        initReceiver()
        getIgnoredNumbersWithTimeAndSettingsUseCase.build()
                .subscribe(GetIgnoredNumbersWithActivityTimeObserver())
    }

    override fun onDestroy() {
        Log.i(LOG_TAG, "Service was destroyed")
        unregisterReceiver(receiver)
        compositeSubscription.clear()
        super.onDestroy()
    }

    private fun initReceiver(){
        val filter = IntentFilter()
        filter.addAction(ACTION_SMS_RECEIVED)
        filter.addAction(ACTION_PHONE_STATE)
        filter.priority = PHONE_CALLS_FILTER_PRIORITY
        registerReceiver(receiver, filter)
    }

    private fun tryAbortIncomingCall() {
        val telephony = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            val c = Class.forName(telephony.javaClass.name)
            val m = c.getDeclaredMethod(GET_ITELEPHONY_METHOD_NAME)
            m.isAccessible = true
            val telephonyService = m.invoke(telephony) as ITelephony
            telephonyService.endCall()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Exception when try to block call", e)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(LOG_TAG, "Intent Received")
            val bundle = intent.extras
            val ignoreHiddenNumbers = this@SmsAndCallsTrackingService.ignoreHiddenNumbers
            val ignoredNumbersWithTimeAndSettings = this@SmsAndCallsTrackingService.ignoredNumbersWithTimeAndSettings
            if(ignoreHiddenNumbers != null && ignoredNumbersWithTimeAndSettings != null){
                if (intent.action == ACTION_SMS_RECEIVED) {
                    Log.i(LOG_TAG, "Sms")
                    bundle?.let {
                        @Suppress("UNCHECKED_CAST")
                        val pdus = it.get(EXTRA_SMS_RECEIVED_PDUS) as Array<Any>
                        @Suppress("DEPRECATION")
                        val mess = SmsMessage.createFromPdu(pdus[0] as ByteArray)
                        if(mess != null){
                            val senderNumber = mess.originatingAddress
                            syncCheckNumberMustBeIgnoredNowUseCase.build(senderNumber,
                                    true,
                                    ignoredNumbersWithTimeAndSettings,
                                    ignoreHiddenNumbers)
                                    .subscribe(CheckIsSmsMustBeIgnoredSingleObserver())
                        }
                    }
                } else {
                    Log.i(LOG_TAG, "Call")
                    bundle?.let {
                        val incomingNumber: String? = it.getString(EXTRA_PHONE_STATE_INCOMING_NUMBER)
                        syncCheckNumberMustBeIgnoredNowUseCase.build(incomingNumber,
                                false,
                                ignoredNumbersWithTimeAndSettings,
                                ignoreHiddenNumbers)
                                .subscribe(CheckIsCallMustBeIgnoredSingleObserver())
                    }
                }
            }
        }
    }

    inner class CheckIsCallMustBeIgnoredSingleObserver : SingleObserver<Boolean> {

        override fun onSuccess(isIgnored: Boolean) {
            if(isIgnored){
                tryAbortIncomingCall()
            }
        }

        override fun onError(e: Throwable) = throw e

        override fun onSubscribe(d: Disposable) { compositeSubscription.add(d) }
    }

    inner class CheckIsSmsMustBeIgnoredSingleObserver : SingleObserver<Boolean> {

        override fun onSuccess(isIgnored: Boolean) {
            if(isIgnored){
                receiver.abortBroadcast()
            }
        }

        override fun onError(e: Throwable) = throw e

        override fun onSubscribe(d: Disposable) { compositeSubscription.add(d) }
    }

    inner class GetIgnoredNumbersWithActivityTimeObserver : Observer<Pair<Boolean, HashMap<String, TimeAndIgnoreSettingsByWeekdayId>>> {

        override fun onSubscribe(d: Disposable) { compositeSubscription.add(d) }

        override fun onNext(hiddenAndNumberSettings: Pair<Boolean, HashMap<String, TimeAndIgnoreSettingsByWeekdayId>>) {
            this@SmsAndCallsTrackingService.ignoreHiddenNumbers = hiddenAndNumberSettings.first
            this@SmsAndCallsTrackingService.ignoredNumbersWithTimeAndSettings = hiddenAndNumberSettings.second
        }

        override fun onComplete() {}

        override fun onError(e: Throwable) {
            throw RuntimeException(e)
        }

    }

    companion object {

        private val LOG_TAG = SmsAndCallsTrackingService::class.simpleName

        private const val ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED" // inaccessible from Telephony before api 19
        private const val ACTION_PHONE_STATE = "android.intent.action.PHONE_STATE"
        private const val PHONE_CALLS_FILTER_PRIORITY = 999

        private const val GET_ITELEPHONY_METHOD_NAME = "getITelephony"
        private const val EXTRA_SMS_RECEIVED_PDUS = "pdus"
        private const val EXTRA_PHONE_STATE_INCOMING_NUMBER = "incoming_number"
    }
}
