package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbBlacklistItemWithActivityIntervalsMapper
import com.gmail.tofibashers.blacklist.entity.BlacklistItemWithActivityIntervals
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistItemWithActivityIntervalsMapper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 30.01.2018.
 */

@Singleton
class BlacklistItemWithActivityIntervalsRepository
@Inject
constructor(private val databaseSource: IDatabaseSource,
            private val dbBlacklistItemWithIntervalsMapper: DbBlacklistItemWithActivityIntervalsMapper,
            private val blacklistItemWithIntervalsMapper: BlacklistItemWithActivityIntervalsMapper)
    : IBlacklistItemWithActivityIntervalsRepository {

    override fun getAllWithChanges(): Flowable<List<BlacklistItemWithActivityIntervals>> {
        return databaseSource.getAllBlacklistItemsWithIntervalsWithChanges()
                .map { dbBlacklistItemWithIntervalsMapper.toBlacklistItemWithActivityIntervalsList(it) }
    }

    override fun put(itemWithIntervals: BlacklistItemWithActivityIntervals): Completable {
        return Single.fromCallable {
            blacklistItemWithIntervalsMapper.toDbBlacklistItemWithActivityIntervals(itemWithIntervals)
        }
                .flatMapCompletable { databaseSource.putBlacklistItemWithActivityIntervals(it) }
    }
}