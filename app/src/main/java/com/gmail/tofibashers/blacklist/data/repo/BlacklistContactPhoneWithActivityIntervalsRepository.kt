package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.datasource.IMemoryDatasource
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbBlacklistContactPhoneWithActivityIntervalsMapper
import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistContactPhoneWithActivityIntervals
import com.gmail.tofibashers.blacklist.data.memory.mapper.MemoryBlacklistContactPhoneWithActivityIntervalsMapper
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneWithActivityIntervals
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactPhoneWithActivityIntervalsMapper
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import java.lang.reflect.MalformedParameterizedTypeException
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 10.05.2018.
 */
@Singleton
class BlacklistContactPhoneWithActivityIntervalsRepository
@Inject
constructor(private val databaseSource: IDatabaseSource,
            private val memoryDatasource: IMemoryDatasource,
            private val blacklistContactItemMapper: BlacklistContactItemMapper,
            private val dbBlacklistContactPhoneWithActivityIntervalsMapper: DbBlacklistContactPhoneWithActivityIntervalsMapper,
            private val blacklistContactPhoneWithActivityIntervalsMapper: BlacklistContactPhoneWithActivityIntervalsMapper,
            private val memoryBlacklistContactPhoneWithActivityIntervalsMapper: MemoryBlacklistContactPhoneWithActivityIntervalsMapper) : IBlacklistContactPhoneWithActivityIntervalsRepository {

    override fun removeSelectedList(): Completable =
            memoryDatasource.removeSelectedBlacklistContactPhonesWithActivityIntervals()

    override fun getSelectedList(): Maybe<List<BlacklistContactPhoneWithActivityIntervals>> {
        return memoryDatasource.getSelectedBlacklistContactPhonesWithActivityIntervals()
                .map(memoryBlacklistContactPhoneWithActivityIntervalsMapper::toBlacklistContactPhoneWithActivityIntervalsList)
    }

    override fun putSelectedList(items: List<BlacklistContactPhoneWithActivityIntervals>): Completable {
        return Single.fromCallable { blacklistContactPhoneWithActivityIntervalsMapper.toMemoryBlacklistContactPhoneWithActivityIntervalList(items) }
                .flatMapCompletable(memoryDatasource::putSelectedBlacklistContactPhonesWithActivityIntervals)
    }

    override fun getAllAssociatedWithBlacklistContact(item: BlacklistContactItem): Single<List<BlacklistContactPhoneWithActivityIntervals>> {
        return Single.fromCallable { blacklistContactItemMapper.toDbBlacklistContactItem(item) }
                .flatMap { databaseSource.getBlacklistContactPhonesWithIntervalsAssociatedWithBlacklistContactItem(it) }
                .map { dbBlacklistContactPhoneWithActivityIntervalsMapper.toBlacklistContactPhoneWithActivityIntervalsList(it) }
    }
}