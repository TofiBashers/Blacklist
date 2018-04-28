package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.BlacklistContactItemWithPhonesAndIntervals
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 21.04.2018.
 */
@Singleton
class BlacklistContactItemWithPhonesAndActivityIntervalsRepository
@Inject
constructor() : IBlacklistContactItemWithPhonesAndActivityIntervalsRepository{

    //TODO: not implemented
    override fun getAllWithChanges(): Flowable<List<BlacklistContactItemWithPhonesAndIntervals>> = Flowable.concat(Flowable.just(listOf()), Flowable.never())

    //TODO: not implemented
    override fun put(itemWithPhonesAndIntervals: BlacklistContactItemWithPhonesAndIntervals): Completable = Completable.complete()
}