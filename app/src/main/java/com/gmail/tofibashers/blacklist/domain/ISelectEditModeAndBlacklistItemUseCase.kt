package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase selects [BlacklistItem] and it's timing information,
 * and set application interaction mode to [InteractionMode.EDIT].
 * Created by TofiBashers on 21.01.2018.
 */
interface ISelectEditModeAndBlacklistItemUseCase {

    /**
     * Result [Completable] executes in [Schedulers.IO], provides result to android UI thread
     */
    fun build(blacklistItem: BlacklistItem) : Completable
}