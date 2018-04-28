package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IMemoryDatasource
import com.gmail.tofibashers.blacklist.data.datasource.MemoryDatasource
import com.gmail.tofibashers.blacklist.data.memory.mapper.MemoryBlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItem
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
            private val memoryBlacklistContactItemMapper: MemoryBlacklistContactItemMapper,
            private val blacklistContactItemMapper: BlacklistContactItemMapper): IBlacklistContactItemRepository {

    //TODO: not implemented
    override fun getByDeviceIdAndDeviceKey(deviceDbId: Long?, deviceKey: String?): Maybe<BlacklistContactItem> = Maybe.empty()

    //TODO: not implemented
    override fun getAllSortedByNameAscWithChanges(): Flowable<List<BlacklistContactItem>> = Flowable.concat(Flowable.just(listOf()), Flowable.never())

    //TODO: not implemented
    override fun delete(item: BlacklistContactItem): Completable = Completable.complete()

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