package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbBlacklistPhoneNumberItemWithActivityIntervalsMapper
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItemWithActivityIntervals
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistPhoneNumberItemWithActivityIntervalsMapper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 30.01.2018.
 */

@Singleton
class BlacklistPhoneNumberItemWithActivityIntervalsRepository
@Inject
constructor(private val databaseSource: IDatabaseSource,
            private val dbBlacklistPhoneNumberItemWithIntervalsMapper: DbBlacklistPhoneNumberItemWithActivityIntervalsMapper,
            private val blacklistPhoneNumberItemWithIntervalsMapper: BlacklistPhoneNumberItemWithActivityIntervalsMapper)
    : IBlacklistPhoneNumberItemWithActivityIntervalsRepository {

    override fun getAllWithChanges(): Flowable<List<BlacklistPhoneNumberItemWithActivityIntervals>> {
        return databaseSource.getAllBlacklistPhoneNumberItemsWithIntervalsWithChanges()
                .map { dbBlacklistPhoneNumberItemWithIntervalsMapper.toBlacklistPhoneNumberItemWithActivityIntervalsList(it) }
    }

    override fun put(itemWithIntervals: BlacklistPhoneNumberItemWithActivityIntervals): Completable {
        return Single.fromCallable {
            blacklistPhoneNumberItemWithIntervalsMapper.toDbBlacklistPhoneNumberItemWithActivityIntervals(itemWithIntervals)
        }
                .flatMapCompletable { databaseSource.putBlacklistPhoneNumberItemWithActivityIntervals(it) }
    }
}