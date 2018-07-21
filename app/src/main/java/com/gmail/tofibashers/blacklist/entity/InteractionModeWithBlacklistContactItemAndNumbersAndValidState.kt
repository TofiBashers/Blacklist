package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 15.04.2018.
 */
@AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
data class InteractionModeWithBlacklistContactItemAndNumbersAndValidState(val mode: InteractionMode,
                                                                          val contactItem: BlacklistContactItem,
                                                                          val phoneNumbers: List<BlacklistContactPhoneNumberItem>,
                                                                          var isValidToSave: Boolean) : List<BlacklistContactPhoneNumberItem> by phoneNumbers