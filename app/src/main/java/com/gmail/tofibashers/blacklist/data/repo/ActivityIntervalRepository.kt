package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.datasource.IMemoryDatasource
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbActivityIntervalMapper
import com.gmail.tofibashers.blacklist.data.memory.mapper.MemoryActivityIntervalMapper
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.mapper.ActivityIntervalMapper
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistPhoneNumberItemMapper
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 22.01.2018.
 */

@Singleton
class ActivityIntervalRepository
@Inject
constructor(
        private val databaseSource: IDatabaseSource,
        private val memoryDatasource: IMemoryDatasource,
        private val dbActivityIntervalMapper: DbActivityIntervalMapper,
        private val memoryActivityIntervalMapper: MemoryActivityIntervalMapper,
        private val activityIntervalMapper: ActivityIntervalMapper,
        private val blacklistPhoneNumberItemMapper: BlacklistPhoneNumberItemMapper
): IActivityIntervalRepository {

    override fun getActivityIntervalsAssociatedWithBlacklistPhoneNumberItem(phoneNumberItem: BlacklistPhoneNumberItem): Single<List<ActivityInterval>> {
        return Single.fromCallable { blacklistPhoneNumberItemMapper.toDbBlacklistPhoneNumberItem(phoneNumberItem) }
                .flatMap(databaseSource::getActivityIntervalsAssociatedWithBlacklistPhoneNumberItem)
                .map(dbActivityIntervalMapper::toActivityIntervalsList)
    }

    override fun getSelectedActivityIntervals(): Maybe<List<ActivityInterval>> {
        return memoryDatasource.getSelectedActivityIntervals()
                .map(memoryActivityIntervalMapper::toActivityIntervalsList)
    }


    override fun putSelectedActivityIntervals(activityIntervals: List<ActivityInterval>): Completable {
        return Single.fromCallable { activityIntervalMapper.toMemoryActivityIntervalsList(activityIntervals) }
                .flatMapCompletable(memoryDatasource::putSelectedActivityIntervals)
    }

    override fun removeSelectedActivityIntervals(): Completable =
            memoryDatasource.removeSelectedActivityIntervals()
}