package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.*
import io.reactivex.Completable


/**
 * If [BlacklistPhoneNumberItem], [ActivityInterval], [BlacklistContactItem], [WhitelistContactItem] or [InteractionMode]
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