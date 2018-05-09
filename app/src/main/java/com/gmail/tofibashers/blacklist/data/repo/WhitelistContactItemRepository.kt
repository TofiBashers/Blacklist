package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.datasource.IDeviceDatasource
import com.gmail.tofibashers.blacklist.data.datasource.IMemoryDatasource
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbBlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.data.device.mapper.DeviceContactItemMapper
import com.gmail.tofibashers.blacklist.data.memory.mapper.MemoryWhitelistContactItemMapper
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItem
import com.gmail.tofibashers.blacklist.entity.mapper.WhitelistContactItemMapper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 14.04.2018.
 */
@Singleton
class WhitelistContactItemRepository
@Inject
constructor(private val memoryDatasource: IMemoryDatasource,
            private val databaseSource: IDatabaseSource,
            private val deviceDatasource: IDeviceDatasource,
            private val dbBlacklistContactItemMapper: DbBlacklistContactItemMapper,
            private val whitelistContactItemMapper: WhitelistContactItemMapper,
            private val deviceContactItemMapper: DeviceContactItemMapper,
            private val memoryWhitelistContactItemMapper: MemoryWhitelistContactItemMapper): IWhitelistContactItemRepository {

    override fun getAllSortedByNameAscWithChanges(): Flowable<List<WhitelistContactItem>> {
        return databaseSource.getAllBlacklistContactItemsSortedByNameAscWithChanges()
                .map { dbBlacklistContactItemMapper.toDeviceContactItemsList(it) }
                .switchMap { deviceDatasource.getAllContactsExcludeSortedByNameAscWithChanges(it) }
                .map { deviceContactItemMapper.toWhitelistContactList(it) }
    }

    override fun removeSelected(): Completable = memoryDatasource.removeSelectedContactItem()

    override fun getSelected(): Maybe<WhitelistContactItem> {
        return memoryDatasource.getSelectedContactItem()
                .map { memoryWhitelistContactItemMapper.toWhitelistContactItem(it) }
    }

    override fun putSelected(item: WhitelistContactItem): Completable {
        return Single.just(item)
                .map { whitelistContactItemMapper.toMemoryWhitelistContactItem(item) }
                .flatMapCompletable { memoryDatasource.putSelectedContactItem(it) }
    }
}