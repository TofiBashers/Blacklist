package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.datasource.IMemoryDatasource
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbBlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.data.memory.mapper.MemoryBlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemMapper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 21.04.2018.
 */
@Singleton
class BlacklistContactItemRepository
@Inject
constructor(private val memoryDatasource: IMemoryDatasource,
            private val databaseSource: IDatabaseSource,
            private val memoryBlacklistContactItemMapper: MemoryBlacklistContactItemMapper,
            private val dbBlacklistContactItemMapper: DbBlacklistContactItemMapper,
            private val blacklistContactItemMapper: BlacklistContactItemMapper): IBlacklistContactItemRepository {

    override fun getByDbId(dbId: Long?): Maybe<BlacklistContactItem> {
        return databaseSource.getBlacklistContactItemById(dbId)
                .map { dbBlacklistContactItemMapper.toBlacklistContactItem(it) }
    }

    override fun getAllSortedByNameAscWithChanges(): Flowable<List<BlacklistContactItem>> {
        return databaseSource.getAllBlacklistContactItemsSortedByNameAscWithChanges()
                .map { dbBlacklistContactItemMapper.toBlacklistContactItemsList(it) }
    }

    override fun delete(item: BlacklistContactItem): Completable {
        return Single.fromCallable { blacklistContactItemMapper.toDbBlacklistContactItem(item) }
                .flatMapCompletable { databaseSource.deleteBlacklistContactItem(it) }
    }

    override fun removeSelected(): Completable = memoryDatasource.removeSelectedBlacklistContactItem()

    override fun getSelected(): Maybe<BlacklistContactItem> {
        return memoryDatasource.getSelectedBlacklistContactItem()
                .map(memoryBlacklistContactItemMapper::toBlacklistContactItem)
    }

    override fun putSelected(item: BlacklistContactItem): Completable {
        return Single.fromCallable { blacklistContactItemMapper.toMemoryBlacklistContactItem(item) }
                .flatMapCompletable(memoryDatasource::putSelectedBlacklistContactItem)
    }
}