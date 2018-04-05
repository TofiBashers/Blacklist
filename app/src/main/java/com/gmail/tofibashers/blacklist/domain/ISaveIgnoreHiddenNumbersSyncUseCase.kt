package com.gmail.tofibashers.blacklist.domain

import io.reactivex.Completable


/**
 * This UseCase saves new policy for ignoring hidden numbers.
 * Created by TofiBashers on 08.02.2018.
 */
interface ISaveIgnoreHiddenNumbersSyncUseCase {

    /**
     * Result [Completable] doesn't specify any schedulers.
     */
    fun build(ignoreHiddenNumbers: Boolean) : Completable
}