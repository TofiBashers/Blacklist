package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase provides selected mode, and depends on it provides selected [BlacklistContactItem]
 * or selected whitelist item and converts, and then provides all phones (blacklist and whitelist)
 * as list of [BlacklistPhoneNumberItem], sorted by number. Updates all selection of blacklist phones.
 * Also, provides validation settings, that depends on selected item and mode
 * (valid when edit, invalid in first create).
 * Created by TofiBashers on 15.04.2018.
 */
interface IGetInteractionModeWithSelectedBlacklistContactItemUseCase {

    /**
     * Result [Single] subscribes and executes in [Schedulers.IO], provides result to Android UI-thread
     */
    fun build() : Single<InteractionModeWithBlacklistContactItemAndNumbersAndValidState>

}