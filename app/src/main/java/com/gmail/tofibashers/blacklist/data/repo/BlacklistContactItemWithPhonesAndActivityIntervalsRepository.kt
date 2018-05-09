package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbBlacklistContactItemWithPhonesAndIntervalsMapper
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItemWithPhonesAndIntervals
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemWithPhonesAndIntervalsMapper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 21.04.2018.
 */
@Singleton
class BlacklistContactItemWithPhonesAndActivityIntervalsRepository
@Inject
constructor(private val databaseSource: IDatabaseSource,
            private val blacklistContactItemWithPhonesAndIntervalsMapper: BlacklistContactItemWithPhonesAndIntervalsMapper,
            private val dbBlacklistContactItemWithPhonesAndIntervalsMapper: DbBlacklistContactItemWithPhonesAndIntervalsMapper) : IBlacklistContactItemWithPhonesAndActivityIntervalsRepository{

    override fun getAllWithChanges(): Flowable<List<BlacklistContactItemWithPhonesAndIntervals>> {
        return databaseSource.getBlacklistContactItemWithPhonesAndActivityIntervalsWithChanges()
                .map { dbBlacklistContactItemWithPhonesAndIntervalsMapper.toBlacklistContactItemWithPhonesAndIntervalsList(it) }
    }

    override fun put(itemWithPhonesAndIntervals: BlacklistContactItemWithPhonesAndIntervals): Completable {
        return Single.fromCallable {
            blacklistContactItemWithPhonesAndIntervalsMapper.toDbBlacklistContactItemWithPhonesAndIntervals(itemWithPhonesAndIntervals)
        }
                .flatMapCompletable { databaseSource.putBlacklistContactItemWithPhonesAndActivityIntervals(it) }
    }
}