package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.datasource.IMemoryDatasource
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbBlacklistPhoneNumberItemMapper
import com.gmail.tofibashers.blacklist.data.memory.mapper.MemoryBlacklistPhoneNumberItemMapper
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistPhoneNumberItemMapper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.01.2018.
 */

@Singleton
class BlacklistPhoneNumberItemRepository
@Inject
constructor(
        private val databaseSource: IDatabaseSource,
        private val memoryDatasource: IMemoryDatasource,
        private val dbBlacklistPhoneNumberItemMapper: DbBlacklistPhoneNumberItemMapper,
        private val blacklistPhoneNumberItemMapper: BlacklistPhoneNumberItemMapper,
        private val memoryBlacklistPhoneNumberItemMapper: MemoryBlacklistPhoneNumberItemMapper
) : IBlacklistPhoneNumberItemRepository {

    override fun getAllWithChanges(): Flowable<List<BlacklistPhoneNumberItem>> {
        return databaseSource.getAllBlacklistPhoneNumberItemsWithChanges()
                .map { dbBlacklistPhoneNumberItemMapper.toBlacklistPhoneNumberItemsList(it) }
    }

    override fun getByNumber(number: String): Maybe<BlacklistPhoneNumberItem> =
            databaseSource.getBlacklistPhoneNumberItemByNumber(number)
                    .map { dbBlacklistPhoneNumberItemMapper.toBlacklistPhoneNumberItemItem(it) }

    override fun deleteBlacklistPhoneNumberItem(blacklistPhoneNumberItem: BlacklistPhoneNumberItem): Completable {
        return Single.fromCallable { blacklistPhoneNumberItemMapper.toDbBlacklistPhoneNumberItem(blacklistPhoneNumberItem) }
                .flatMapCompletable { databaseSource.deleteBlacklistPhoneNumberItem(it) }
    }

    override fun removeSelectedBlacklistPhoneNumberItem(): Completable =
            memoryDatasource.removeSelectedBlacklistPhoneNumberItem()

    override fun getSelectedBlacklistPhoneNumberItem(): Maybe<BlacklistPhoneNumberItem> {
        return memoryDatasource.getSelectedBlacklistPhoneNumberItem()
                .map(memoryBlacklistPhoneNumberItemMapper::toBlacklistPhoneNumberItem)
    }

    override fun putSelectedBlacklistPhoneNumberItem(blacklistPhoneNumberItem: BlacklistPhoneNumberItem): Completable {
        return Single.fromCallable { blacklistPhoneNumberItemMapper.toMemoryBlacklistPhoneNumberItem(blacklistPhoneNumberItem) }
                .flatMapCompletable(memoryDatasource::putSelectedBlacklistPhoneNumberItem)
    }

}