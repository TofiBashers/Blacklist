package com.gmail.tofibashers.blacklist.utils

import com.gmail.tofibashers.blacklist.entity.PhoneNumberTypeWithValue
import com.gmail.tofibashers.blacklist.entity.PhoneNumberTypeWithValue_InvalidFactory
import com.gmail.tofibashers.blacklist.entity.PhoneNumberTypeWithValue_ValidFactory
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 24.05.2018.
 */
@Singleton
class PhoneNumberFormatUtils
@Inject
constructor(private val phoneNumberUtil: PhoneNumberUtil,
            private val phoneNumberTypeWithValue_ValidFactory: PhoneNumberTypeWithValue_ValidFactory,
            private val phoneNumberTypeWithValue_InvalidFactory: PhoneNumberTypeWithValue_InvalidFactory){

    fun toPhoneNumberTypeWithValue(srcNumber: String, defaultLocaleIso: String) : PhoneNumberTypeWithValue{
        val uppercaseLocale = defaultLocaleIso.toUpperCase(Locale.ENGLISH)
        val localeForParse = if(uppercaseLocale.isNotBlank()) uppercaseLocale else null
        return try{
            val phoneNumber = phoneNumberUtil.parse(srcNumber, localeForParse)
            phoneNumberTypeWithValue_ValidFactory.create(phoneNumber)
        }
        catch (e: NumberParseException){
            toInvalidPhoneNumberTypeWithValue(srcNumber)
        }
    }

    fun toInvalidPhoneNumberTypeWithValue(srcNumber: String) : PhoneNumberTypeWithValue.Invalid{
        val modifString = srcNumber.replace(PATTERN_ALL_EXCLUDE_DIGIT_OR_PLUS,"")
        return phoneNumberTypeWithValue_InvalidFactory.create(modifString)
    }

    companion object {

        private val PATTERN_ALL_EXCLUDE_DIGIT_OR_PLUS = Regex("[^0-9+]")
    }
}