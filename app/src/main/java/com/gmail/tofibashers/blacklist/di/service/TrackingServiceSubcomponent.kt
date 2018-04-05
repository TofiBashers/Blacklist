package com.gmail.tofibashers.blacklist.di.service

import com.gmail.tofibashers.blacklist.SmsAndCallsTrackingService
import dagger.Subcomponent
import dagger.android.AndroidInjector


/**
 * Created by TofiBashers on 20.01.2018.
 */

@Subcomponent
@PerService
interface TrackingServiceSubcomponent : AndroidInjector<SmsAndCallsTrackingService> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SmsAndCallsTrackingService>()
}