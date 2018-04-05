package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 02.02.2018.
 */

@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class MutableActivityIntervalsWithEnableAndValidState(
        var isValidToSave: Boolean,
        var intervalsWithEnabled: MutableList<Pair<Boolean, ActivityInterval>>)
    : MutableList<Pair<Boolean, ActivityInterval>> by intervalsWithEnabled{

    constructor(instance: MutableActivityIntervalsWithEnableAndValidState)
            : this(instance.isValidToSave, instance.intervalsWithEnabled)
}