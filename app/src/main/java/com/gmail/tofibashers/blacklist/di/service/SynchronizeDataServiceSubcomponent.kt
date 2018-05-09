package com.gmail.tofibashers.blacklist.di.service

import com.gmail.tofibashers.blacklist.SmsAndCallsTrackingService
import com.gmail.tofibashers.blacklist.data.SynchronizeDataService
import dagger.Subcomponent
import dagger.android.AndroidInjector


/**
 * Created by TofiBashers on 07.05.2018.
 */
@Subcomponent
@PerService
interface SynchronizeDataServiceSubcomponent : AndroidInjector<SynchronizeDataService> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SynchronizeDataService>()
}