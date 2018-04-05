package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 04.02.2018.
 */

@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class InteractionModeWithBlacklistItemAndValidState(val mode: InteractionMode,
                                                         val item: BlacklistItem,
                                                         var isValidToSave: Boolean)