package com.gmail.tofibashers.blacklist.ui.time_settings

import com.gmail.tofibashers.blacklist.entity.TimeChangeInitData
import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory

sealed class TimeSettingsNavData {

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class ItemDetailsRoute : TimeSettingsNavData()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    class TimeChangeRoute(val timeChangeInitData: TimeChangeInitData) : TimeSettingsNavData()
}