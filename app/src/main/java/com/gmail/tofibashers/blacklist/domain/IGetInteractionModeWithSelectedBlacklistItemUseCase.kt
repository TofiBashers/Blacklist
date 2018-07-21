package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.InteractionModeWithBlacklistPhoneNumberItemAndValidState
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase provides selected mode, and depends on it provides selected phoneNumberItem,
 * or create default. Also, provides validation settings, that depends on phoneNumberItem and mode.
 * Created by TofiBashers on 16.01.2018.
 */
interface IGetInteractionModeWithSelectedBlacklistItemUseCase {

    /**
     * Result [Single] operate in [Schedulers.IO], provides result to Android UI thread.
     */
    fun build(): Single<InteractionModeWithBlacklistPhoneNumberItemAndValidState>
}