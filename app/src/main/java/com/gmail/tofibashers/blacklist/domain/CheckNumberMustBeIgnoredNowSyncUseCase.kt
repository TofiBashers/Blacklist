package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.TimeAndIgnoreSettingsByWeekdayId
import com.gmail.tofibashers.blacklist.entity.ActivityTimeIntervalWithIgnoreSettings
import com.gmail.tofibashers.blacklist.entity.PhoneNumberTypeWithValue
import com.gmail.tofibashers.blacklist.utils.PhoneNumberFormatUtils
import com.gmail.tofibashers.blacklist.utils.TimeFormatUtils
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 28.01.2018.
 */

@Singleton
class CheckNumberMustBeIgnoredNowSyncUseCase
@Inject
constructor(private val timeFormatUtils: TimeFormatUtils,
            private val phoneNumberFormatUtils: PhoneNumberFormatUtils): ICheckNumberMustBeIgnoredNowSyncUseCase {

    override fun build(number: String?,
                       isSms: Boolean,
                       ignoredSettingsByNumbers: HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>,
                       defaultPhoneCountry: String,
                       ignoreHiddenNumbers: Boolean): Single<Boolean> {
        return Single.defer<Boolean> {
            if(number != null && ignoredSettingsByNumbers.isEmpty()) {
                Single.just(false)
            }
            else if(number.isNullOrEmpty()) {
                Single.just(ignoreHiddenNumbers)
            }
            else {
                Single.fromCallable { phoneNumberFormatUtils.toPhoneNumberTypeWithValue(number!!, defaultPhoneCountry) }
                        .flatMapMaybe { phoneNumberWithValue: PhoneNumberTypeWithValue ->
                            Maybe.fromCallable { ignoredSettingsByNumbers[phoneNumberWithValue] }
                                    .switchIfEmpty(Maybe.defer {
                                        when(phoneNumberWithValue){
                                            is PhoneNumberTypeWithValue.Invalid -> Maybe.empty()
                                            is PhoneNumberTypeWithValue.Valid ->
                                                Single.fromCallable { phoneNumberFormatUtils.toInvalidPhoneNumberTypeWithValue(number!!) }
                                                        .flatMapMaybe { Maybe.fromCallable { ignoredSettingsByNumbers[phoneNumberWithValue] } }
                                        }
                                    })
                        }
                        .flatMap { timeSettingsByWeekdayId: TimeAndIgnoreSettingsByWeekdayId ->
                            Single.fromCallable { timeFormatUtils.currentWeekdayAsActivityIntervalFormat() }
                                    .flatMapMaybe { currentDay: Int ->
                                        val timeAndSettings = timeSettingsByWeekdayId[currentDay]
                                        if (timeAndSettings == null
                                                || (isSms && !timeAndSettings.isSmsBlocked)
                                                || (!isSms && !timeAndSettings.isCallsBlocked)) {
                                            return@flatMapMaybe Maybe.empty<ActivityTimeIntervalWithIgnoreSettings>()
                                        }
                                        return@flatMapMaybe Maybe.just(timeAndSettings)
                                    }
                        }
                        .map { settings: ActivityTimeIntervalWithIgnoreSettings ->
                            timeFormatUtils.isCurrentTimeInInterval(settings.beginTime, settings.endTime)
                        }
                        .toSingle(false)
            }
        }
    }
}