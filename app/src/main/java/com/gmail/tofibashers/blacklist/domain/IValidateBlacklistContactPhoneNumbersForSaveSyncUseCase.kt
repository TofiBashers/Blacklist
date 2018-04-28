package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import io.reactivex.Single


/**
 * This UseCase validate list of [BlacklistContactPhoneNumberItem] for ability to save.
 * Created by TofiBashers on 17.04.2018.
 */
interface IValidateBlacklistContactPhoneNumbersForSaveSyncUseCase {

    /**
     * Result [Single] doesn't modify any schedulers.
     */
    fun build(phoneNumbers: List<BlacklistContactPhoneNumberItem>) : Single<Boolean>
}