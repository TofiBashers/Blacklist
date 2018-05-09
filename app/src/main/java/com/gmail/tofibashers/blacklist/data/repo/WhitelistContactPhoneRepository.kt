package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.datasource.IDeviceDatasource
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactPhoneItem
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbBlacklistContactPhoneItemMapper
import com.gmail.tofibashers.blacklist.data.device.DeviceContactItem
import com.gmail.tofibashers.blacklist.data.device.DeviceContactPhoneItem
import com.gmail.tofibashers.blacklist.data.device.mapper.DeviceContactPhoneItemMapper
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItem
import com.gmail.tofibashers.blacklist.entity.WhitelistContactPhone
import com.gmail.tofibashers.blacklist.entity.mapper.WhitelistContactItemMapper
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 21.04.2018.
 */
@Singleton
class WhitelistContactPhoneRepository
@Inject
constructor(private val databaseSource: IDatabaseSource,
            private val deviceDatasource: IDeviceDatasource,
            private val whitelistContactItemMapper: WhitelistContactItemMapper,
            private val deviceContactPhoneMapper: DeviceContactPhoneItemMapper,
            private val dbBlacklistContactPhoneItemMapper: DbBlacklistContactPhoneItemMapper) : IWhitelistContactPhoneRepository {

    override fun getAllAssociatedWithContactSortedByNumberAsc(whitelistContactItem: WhitelistContactItem): Single<List<WhitelistContactPhone>> {
        return getBlacklistPhonesByWhitelistItem(whitelistContactItem)
                .flatMap {
                    deviceDatasource.getContactPhonesByDeviceContactExcludeSortedByNumberAsc(it.first, it.second)
                }
                .map { deviceContactPhoneMapper.toWhitelistContactPhoneList(it) }
    }

    override fun getAllAssociatedWithContact(whitelistContactItem: WhitelistContactItem): Single<List<WhitelistContactPhone>> {
        return getBlacklistPhonesByWhitelistItem(whitelistContactItem)
                .flatMap {
                    deviceDatasource.getAllContactPhonesByDeviceContactExclude(it.first, it.second)
                }
                .map { deviceContactPhoneMapper.toWhitelistContactPhoneList(it) }
    }

    override fun getCountOfAssociatedWithContact(whitelistContactItem: WhitelistContactItem): Single<Int> {
        return getBlacklistPhonesByWhitelistItem(whitelistContactItem)
                .flatMap {
                    deviceDatasource.getContactPhonesCountByDeviceContactExclude(it.first, it.second)
                }
    }

    private fun getBlacklistPhonesByWhitelistItem(whitelistContactItem: WhitelistContactItem) : Single<Pair<DeviceContactItem, List<DeviceContactPhoneItem>>> {
        return Single.fromCallable {
                    whitelistContactItemMapper.toDbBlacklistContact(whitelistContactItem)
                }
                .flatMap { databaseSource.getBlacklistContactPhonesByBlacklistContactItemByDeviceDbIdAndLookupKey(it) }
                .flatMap { dbPhones: List<DbBlacklistContactPhoneItem> ->
                    Single.zip(
                            Single.fromCallable { dbBlacklistContactPhoneItemMapper.toDeviceContactPhoneList(dbPhones) },
                            Single.fromCallable { whitelistContactItemMapper.toDeviceContactItem(whitelistContactItem) },
                            BiFunction { devicePhones: List<DeviceContactPhoneItem>,
                                         deviceContactItem: DeviceContactItem ->
                                Pair(deviceContactItem, devicePhones)
                            })
                }
    }
}