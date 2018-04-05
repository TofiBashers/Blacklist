package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable


/**
 * If [BlacklistItem], [ActivityInterval] or [InteractionMode]
 * was selected previously, this UseCase remove all selections.
 * Executes synchronously.
 * Created by TofiBashers on 05.02.2018.
 */
interface IDeleteAllSelectionsSyncUseCase {

    /**
     * Result [Completable] doesn't specify any schedulers.
     */
    fun build(): Completable
}