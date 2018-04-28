package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItemWithNonIgnoredNumbersFlag
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * This UseCase selects [InteractionMode.EDIT], blacklist contact and all it's phone numbers [ActivityInterval].
 * Created by TofiBashers on 10.04.2018.
 */
interface ISelectEditModeAndContactItemUseCase {

    /**
     * Result [Completable] subscribes and executes in [Schedulers.IO], provides result to Android UI-thread
     */
    fun build(item: BlacklistContactItemWithNonIgnoredNumbersFlag) : Completable
}
