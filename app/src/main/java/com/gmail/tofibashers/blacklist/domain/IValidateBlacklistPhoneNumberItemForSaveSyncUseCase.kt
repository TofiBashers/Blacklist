package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import io.reactivex.Single


/**
 * This UseCase validate [BlacklistPhoneNumberItem] for ability to save.
 * Created by TofiBashers on 05.02.2018.
 */
interface IValidateBlacklistPhoneNumberItemForSaveSyncUseCase {

    /**
     * Result [Single] subscribes and provides result in Android UI-Thread
     */
    fun build(phoneNumberItem: BlacklistPhoneNumberItem) : Single<Boolean>
}