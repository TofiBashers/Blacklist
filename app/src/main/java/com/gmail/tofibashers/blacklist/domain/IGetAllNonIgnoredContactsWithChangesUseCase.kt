package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.WhitelistContactItemWithHasPhones
import io.reactivex.Observable
import org.reactivestreams.Subscriber


/**
 * This UseCase provides all non ignored contacts once, and after all changes.
 * Created by TofiBashers on 14.04.2018.
 */
interface IGetAllNonIgnoredContactsWithChangesUseCase {

    /**
     * Every [WhitelistContactItemWithHasPhones.hasPhones] flag depends on count of [WhitelistContactPhone]
     * @return Observable with list of [hitelistContactItemWithHasPhones] firstly, and after any change.
     *
     * Result [Observable] subscribes and executes in [Schedulers.IO], provides result to [AndroidSchedulers.mainThread].
     * Never calls [Subscriber.onComplete]
     */
    fun build() : Observable<List<WhitelistContactItemWithHasPhones>>
}