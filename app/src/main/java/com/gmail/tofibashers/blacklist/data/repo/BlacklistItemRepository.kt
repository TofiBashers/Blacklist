package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.datasource.IMemoryDatasource
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbBlacklistItemMapper
import com.gmail.tofibashers.blacklist.data.memory.mapper.MemoryBlacklistItemMapper
import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistItemMapper
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
        private val blacklistItemMapper: BlacklistItemMapper,
        private val memoryBlacklistItemMapper: MemoryBlacklistItemMapper
) : IBlacklistItemRepository {

    override fun getAllWithChanges(): Flowable<List<BlacklistItem>> {
        return databaseSource.getAllBlackListItemsWithChanges()
                .map { dbBlacklistItemMapper.toBlacklistItemsList(it) }
    }

    override fun getByNumber(number: String): Maybe<BlacklistItem> =
            databaseSource.getBlacklistItemByNumber(number)
                    .map { dbBlacklistItemMapper.toBlacklistItem(it) }

    override fun deleteBlacklistItem(blacklistItem: BlacklistItem): Completable {
        return Single.fromCallable { blacklistItemMapper.toDbBlacklistItem(blacklistItem) }
                .flatMapCompletable { databaseSource.deleteBlackListItem(it) }
    }

    override fun removeSelectedBlackListItem(): Completable =
            memoryDatasource.removeSelectedBlackListItem()

    override fun getSelectedBlackListItem(): Maybe<BlacklistItem> {
        return memoryDatasource.getSelectedBlackListItem()
                .map(memoryBlacklistItemMapper::toBlacklistItem)
    }

    override fun putSelectedBlackListItem(blacklistItem: BlacklistItem): Completable {
        return Single.fromCallable { blacklistItemMapper.toMemoryBlacklistItem(blacklistItem) }
                .flatMapCompletable(memoryDatasource::putSelectedBlackListItem)
    }

}