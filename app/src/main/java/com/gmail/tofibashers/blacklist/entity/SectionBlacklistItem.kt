package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory


/**
 * Created by TofiBashers on 08.04.2018.
 */
sealed class SectionBlacklistItem {

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class Contact(val contactItem: BlacklistContactItemWithNonIgnoredNumbersFlag) : SectionBlacklistItem()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class PhoneNumber(val phoneNumberItem: BlacklistPhoneNumberItem) : SectionBlacklistItem()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class Header(val type: Type) : SectionBlacklistItem() {
        enum class Type{
            NUMBERS,
            CONTACTS
        }
    }
}