package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase set app mode to [InteractionMode.CREATE].
 * Created by TofiBashers on 21.01.2018.
 */
interface ISelectCreateModeUseCase {

    /**
     * Result [Completable] executes in [Schedulers.IO], provides result to android UI thread.
     */
    fun build(): Completable
}