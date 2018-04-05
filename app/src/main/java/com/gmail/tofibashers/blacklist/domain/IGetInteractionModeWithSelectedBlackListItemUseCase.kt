package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.InteractionModeWithBlacklistItemAndValidState
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase provides selected mode, and depends on it provides selected item,
 * or create default. Also, provides validation settings, that depends on item and mode.
 * Created by TofiBashers on 16.01.2018.
 */
interface IGetInteractionModeWithSelectedBlackListItemUseCase {

    /**
     * Result [Single] operate in [Schedulers.IO], provides result to Android UI thread.
     */
    fun build(): Single<InteractionModeWithBlacklistItemAndValidState>
}