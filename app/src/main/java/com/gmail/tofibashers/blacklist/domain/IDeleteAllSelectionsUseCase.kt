package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers


/**
 * If [BlacklistItem], [ActivityInterval] or [InteractionMode]
 * was selected previously, this UseCase remove all selections.
 * Executes async.
 * Created by TofiBashers on 22.01.2018.
 */
interface IDeleteAllSelectionsUseCase {

    /**
     * Result [Completable] executes in [Schedulers.IO], provides result to android UI-thread
     */
    fun build(): Completable
}