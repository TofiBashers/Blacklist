package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase selects [BlacklistPhoneNumberItem] and it's timing information,
 * and set application interaction mode to [InteractionMode.EDIT].
 * Created by TofiBashers on 21.01.2018.
 */
interface ISelectEditModeAndPhoneNumberItemUseCase {

    /**
     * Result [Completable] executes in [Schedulers.IO], provides result to android UI thread
     */
    fun build(blacklistPhoneNumberItem: BlacklistPhoneNumberItem) : Completable
}