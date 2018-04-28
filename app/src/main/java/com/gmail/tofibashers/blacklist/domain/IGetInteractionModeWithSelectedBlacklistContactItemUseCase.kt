package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.InteractionModeWithBlacklistContactItemAndNumbersAndValidState
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItem
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase provides selected mode, and depends on it provides selected [BlacklistContactItem]
 * or selected whitelist item and converts, and then provides all phones (blacklist and whitelist)
 * as list of [BlacklistPhoneNumberItem]. Also, provides validation settings, that depends on selected item and mode.
 * Created by TofiBashers on 15.04.2018.
 */
interface IGetInteractionModeWithSelectedBlacklistContactItemUseCase {

    /**
     * Result [Single] subscribes and executes in [Schedulers.IO], provides result to Android UI-thread
     */
    fun build() : Single<InteractionModeWithBlacklistContactItemAndNumbersAndValidState>
}