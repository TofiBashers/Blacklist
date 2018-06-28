package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.TimeAndIgnoreSettingsByWeekdayId
import com.gmail.tofibashers.blacklist.entity.PhoneNumberTypeWithValue
import io.reactivex.Observable


/**
 * This UseCase provides param ignore hidden numbers, and all ignored numbers info in structure, that allows
 * getting full number info by O(1) complexity.
 * Created by TofiBashers on 01.02.2018.
 */
interface IGetAllIgnoredInfoOptimizedForAccessWithChangesUseCase {

    /**
     * Provides ignore hidden flag, current default iso-2 country code for input phones,
     * and ignore personal number settings firstly, and after one of their
     * would be update.
     * Result [Observable] executes in [Schedulers.IO], provides result to Android UI thread.
     * Never calls onComplete.
     * @return [Observable] with [Triple], where [Triple.first] is true when hidden numbers must be ignore,
     * [Triple.second] current default iso-2 country for phones without country code and
     * and [Triple.third] is a [HashMap] with numbers and it's time settings
     */
    fun build() : Observable<Triple<Boolean, String, HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>>>
}