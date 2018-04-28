package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.datasource.IMemoryDatasource
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbBlacklistItemMapper
import com.gmail.tofibashers.blacklist.data.memory.mapper.MemoryBlacklistItemMapper
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistPhoneItemMapper
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
class BlacklistItemRepository
@Inject
constructor(
        private val databaseSource: IDatabaseSource,
        private val memoryDatasource: IMemoryDatasource,
        private val dbBlacklistItemMapper: DbBlacklistItemMapper,
        private val blacklistPhoneItemMapper: BlacklistPhoneItemMapper,
        private val memoryBlacklistItemMapper: MemoryBlacklistItemMapper
) : IBlacklistItemRepository {

    override fun getAllWithChanges(): Flowable<List<BlacklistPhoneNumberItem>> {
        return databaseSource.getAllBlackListItemsWithChanges()
                .map { dbBlacklistItemMapper.toBlacklistItemsList(it) }
    }

    override fun getByNumber(number: String): Maybe<BlacklistPhoneNumberItem> =
            databaseSource.getBlacklistItemByNumber(number)
                    .map { dbBlacklistItemMapper.toBlacklistItem(it) }

    override fun deleteBlacklistItem(blacklistPhoneNumberItem: BlacklistPhoneNumberItem): Completable {
        return Single.fromCallable { blacklistPhoneItemMapper.toDbBlacklistItem(blacklistPhoneNumberItem) }
                .flatMapCompletable { databaseSource.deleteBlackListItem(it) }
    }

    override fun removeSelectedBlacklistPhoneNumberItem(): Completable =
            memoryDatasource.removeSelectedBlackListItem()

    override fun getSelectedBlacklistPhoneNumberItem(): Maybe<BlacklistPhoneNumberItem> {
        return memoryDatasource.getSelectedBlackListItem()
                .map(memoryBlacklistItemMapper::toBlacklistItem)
    }

    override fun putSelectedBlacklistPhoneNumberItem(blacklistPhoneNumberItem: BlacklistPhoneNumberItem): Completable {
        return Single.fromCallable { blacklistPhoneItemMapper.toMemoryBlacklistItem(blacklistPhoneNumberItem) }
                .flatMapCompletable(memoryDatasource::putSelectedBlackListItem)
    }

}