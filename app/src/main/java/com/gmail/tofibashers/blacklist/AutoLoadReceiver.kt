package com.gmail.tofibashers.blacklist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AutoLoadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val startIntent = Intent(context, SmsAndCallsTrackingService::class.java)
        context.startService(startIntent)
    }

}
