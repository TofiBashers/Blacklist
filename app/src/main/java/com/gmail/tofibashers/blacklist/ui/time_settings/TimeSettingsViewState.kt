package com.gmail.tofibashers.blacklist.ui.time_settings

import com.gmail.tofibashers.blacklist.entity.MutableActivityIntervalsWithEnableAndValidState
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 23.01.2018.
 */
sealed class TimeSettingsViewState {

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class DataViewState(val activtiyIntervals: MutableActivityIntervalsWithEnableAndValidState) : TimeSettingsViewState()


    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class LoadingViewState : TimeSettingsViewState()
}