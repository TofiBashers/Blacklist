package com.gmail.tofibashers.blacklist.entity

import com.gmail.tofibashers.blacklist.utils.BaseFactory
import com.google.auto.factory.AutoFactory
import io.michaelrocks.libphonenumber.android.Phonenumber
import org.joda.time.base.BaseSingleFieldPeriod


/**
 * Created by TofiBashers on 24.05.2018.
 */
sealed class PhoneNumberTypeWithValue {

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class Valid(val representation: Phonenumber.PhoneNumber) : PhoneNumberTypeWithValue()

    @AutoFactory(allowSubclasses = true, extending = BaseFactory::class)
    data class Invalid(val representation: String) : PhoneNumberTypeWithValue()
}