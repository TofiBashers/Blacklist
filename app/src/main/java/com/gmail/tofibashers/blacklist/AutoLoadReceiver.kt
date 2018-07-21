package com.gmail.tofibashers.blacklist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AutoLoadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == Intent.ACTION_BOOT_COMPLETED) { //for disable lint warning and low cohesion
            val startIntent = Intent(context, SmsAndCallsTrackingService::class.java)
            context.startService(startIntent)
        }
    }

}
