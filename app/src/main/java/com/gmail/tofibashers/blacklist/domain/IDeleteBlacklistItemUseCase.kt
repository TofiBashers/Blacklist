package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase removes [BlacklistItem] with all associated and unused entities
 * Created by TofiBashers on 22.01.2018.
 */
interface IDeleteBlacklistItemUseCase {

    /**
     * Result [Completable] executes at [Schedulers.IO], provides result to Android UI thread
     */
    fun build(blacklistElement: BlacklistItem) : Completable
}