package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.WhitelistContactItemWithHasPhones
import io.reactivex.Completable


/**
 * This UseCase selects contact.
 * Created by TofiBashers on 14.04.2018.
 */
interface ISelectContactItemUseCase {

    /**
     * Result [Completable] subscribes and executes on [Schedulers.IO], provides result to Android UI-thread
     */
    fun build(whitelistContactItem: WhitelistContactItemWithHasPhones): Completable
}