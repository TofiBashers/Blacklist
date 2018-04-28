package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase saves [BlacklistContactItem], list of [BlacklistContactPhoneNumberItem] and currently selected (or created) [ActivityInterval]'s.
 * Validate existence of [BlacklistContactItem] before saving.
 * Created by TofiBashers on 20.04.2018.
 */
interface ISaveBlacklistContactItemWithDeleteSelectionsUseCase {

    /**
     * Validate existence of [BlacklistContactItem] before saving.
     * Result [Completable] executes in [Schedulers.IO], provides result to android UI-thread
     */
    fun build(contactItem: BlacklistContactItem, contactPhones: List<BlacklistContactPhoneNumberItem>) : Completable
}