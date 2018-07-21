package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.*
import io.reactivex.Single

/**
 * This UseCase get selected [WhitelistContactPhone]'s list and [BlacklistContactPhoneWithActivityIntervals]]'s list
 * (if interaction mode is Edit), and merged into one list of [BlacklistContactPhoneWithActivityIntervals].
 * For whitelist phones, creates default activity intervals. Result list selected before return.
 * Created by TofiBashers on 14.05.2018.
 */
interface ISelectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase {

    /**
     * @param mode is interaction mode for resolve that phones gets (blacklist and whitelist phones,
     * or only blacklist).
     */
    fun build(mode: InteractionMode) : Single<List<BlacklistContactPhoneWithActivityIntervals>>
}