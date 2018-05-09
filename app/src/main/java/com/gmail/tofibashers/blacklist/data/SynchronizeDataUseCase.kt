package com.gmail.tofibashers.blacklist.data

import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.datasource.IDeviceDatasource
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactItem
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactPhoneItem
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbBlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.data.device.DeviceContactItem
import com.gmail.tofibashers.blacklist.data.device.mapper.DeviceContactItemMapper
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observables.GroupedObservable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 01.05.2018.
 */
@Singleton
class SynchronizeDataUseCase
@Inject
constructor(private val databaseSource: IDatabaseSource,
            private val deviceDatasource: IDeviceDatasource,
            private val dbBlacklistContactItemMapper: DbBlacklistContactItemMapper,
            private val deviceContactItemMapper: DeviceContactItemMapper) : ISynchronizeDataUseCase {

    override fun buildSyncOnSubscribeAndAfterAllChanges(): Completable {
        return deviceDatasource.getInitialAndAnyDeviceDataChangesSignal()
                .flatMapCompletable {
                    return@flatMapCompletable syncDbBlacklistContactsWithDeviceContacts()
                            .andThen(syncDbBlacklistContactPhonesWithDeviceContactPhones())
                            .compose(databaseSource.inTransactionCompletable())
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun syncDbBlacklistContactsWithDeviceContacts(): Completable {
        return databaseSource.getAllBlacklistContactItems()
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMap { sourceItem: DbBlacklistContactItem ->
                    getContactItemWithModifTypeAndDbIdIfDeletedOrUpdatedOnDevice(sourceItem)
                            .toObservable()
                }
                .groupBy { pair: Pair<ModifType, DbBlacklistContactItem> -> pair.first }
                .flatMapCompletable { groupedObservable: GroupedObservable<ModifType, Pair<ModifType, DbBlacklistContactItem>> ->
                    return@flatMapCompletable groupedObservable
                            .map { pair: Pair<ModifType, DbBlacklistContactItem> -> pair.second }
                            .toList()
                            .flatMapCompletable { when(groupedObservable.key) {
                                ModifType.UPDATED -> databaseSource.putBlacklistContactItems(it)
                                ModifType.DELETED -> databaseSource.deleteBlacklistContactItems(it)
                                else -> throw RuntimeException("Unresolved ModifType when sync")
                            } }
                }
    }

    private fun syncDbBlacklistContactPhonesWithDeviceContactPhones(): Completable {
        return databaseSource.getAllBlacklistContactPhones()
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMapMaybe { item: DbBlacklistContactPhoneItem ->
                    getContactPhoneItemIfDeletedOrModifiedOnDevice(item)
                }
                .toList()
                .filter { it.isNotEmpty() }
                .flatMapCompletable { databaseSource.deleteBlacklistContactPhoneItems(it) }
    }

    private fun getContactPhoneItemIfDeletedOrModifiedOnDevice(item: DbBlacklistContactPhoneItem): Maybe<DbBlacklistContactPhoneItem> {
        return deviceDatasource.getDeviceContactPhoneWithIdAndNumber(item.deviceDbId, item.number)
                .isEmpty()
                .flatMapMaybe { if(it) Maybe.just(item) else Maybe.empty<DbBlacklistContactPhoneItem>()}
    }

    private fun getContactItemWithModifTypeAndDbIdIfDeletedOrUpdatedOnDevice(sourceContactItem: DbBlacklistContactItem): Maybe<Pair<ModifType, DbBlacklistContactItem>> {
        return Single.fromCallable { dbBlacklistContactItemMapper.toDeviceContactItem(sourceContactItem) }
                .flatMapMaybe { sourceDeviceItem: DeviceContactItem ->
                    deviceDatasource.getDeviceContactByIdAndLookupKey(sourceDeviceItem.id, sourceDeviceItem.lookupKey)
                            .switchIfEmpty(Maybe.error<DeviceContactItem>(NonExistDeviceItemWrapper()))
                            .flatMap { updatedDeviceItem: DeviceContactItem ->
                                return@flatMap if(updatedDeviceItem == sourceDeviceItem) {
                                    Maybe.empty()
                                }
                                else {
                                    Maybe.fromCallable { deviceContactItemMapper.toDbBlacklistContactItem(updatedDeviceItem, sourceContactItem.id) }
                                        .map { Pair(ModifType.UPDATED, it) }
                                }
                            }
                            .onErrorResumeNext { throwable: Throwable ->
                                return@onErrorResumeNext if( throwable is NonExistDeviceItemWrapper){
                                    Maybe.just(Pair(ModifType.DELETED, sourceContactItem))
                                }
                                else Maybe.error(throwable)
                            }
                }
    }

    private class NonExistDeviceItemWrapper : Exception()

    private enum class ModifType {
        UPDATED,
        DELETED
    }
}