package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.TimeAndIgnoreSettingsByWeekdayId
import com.gmail.tofibashers.blacklist.entity.PhoneNumberTypeWithValue
import io.reactivex.Single


/**
 * This UseCase Synchronously (fully on Android UI thread) and fast (by O(1))
 * checks number must be ignored by multiple conditions.
 * Created by TofiBashers on 29.01.2018.
 */
interface ICheckNumberMustBeIgnoredNowSyncUseCase {

    /**
     * Result [Single] doesn't specify any schedulers.
     * @param number can be empty or null, if number is hidden or unknown
     * @param isSms type of input event, associated with number (Sms or Call)
     * @param ignoredSettingsByNumbers [HashMap] with ignoring policies
     * @param ignoreHiddenNumbers policy for hidden numbers
     * @param defaultPhoneCountry current default iso-2 country for phones without country code
     * @return [Single] with true, if number must be ignored now, else - with false.
     */
    fun build(number: String?,
              isSms: Boolean,
              ignoredSettingsByNumbers: HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>,
              defaultPhoneCountry: String,
              ignoreHiddenNumbers: Boolean) : Single<Boolean>
}