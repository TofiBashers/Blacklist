package com.gmail.tofibashers.blacklist.data.datasource

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.gmail.tofibashers.blacklist.data.device.DeviceContactItem
import com.gmail.tofibashers.blacklist.data.device.DeviceContactPhoneItem
import com.gmail.tofibashers.blacklist.data.device.mapper.CursorMapper
import io.reactivex.*
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 03.05.2018.
 */
@Singleton
class DeviceDatasource
@Inject
constructor (private val contentResolver: ContentResolver,
             private val cursorMapper: CursorMapper) : IDeviceDatasource {

    override fun getDeviceContactByIdAndLookupKey(id: Long?, lookupkey: String?): Maybe<DeviceContactItem> {
        return notNullIdOrEmpty(id)
                .flatMap {
                    val uri = ContactsContract.Contacts.getLookupUri(it, lookupkey)
                    return@flatMap query(uri, CONTACT_PROJECTION)
                }
                .flatMap { cursorMapper.toMaybeDeviceContactIfNonEmpty(it)}
    }

    override fun getAllContactsExcludeSortedByNameAscWithChanges(excludeForSelectionContacts: List<DeviceContactItem>): Flowable<List<DeviceContactItem>> {
        return getInitialAndAnyChangesSignal(Pair(ContactsContract.Contacts.CONTENT_URI, true))
                .switchMap {
                    return@switchMap getNonNullIdsAndLookupKeys(excludeForSelectionContacts)
                            .flatMap { getActualIdsAsString(it) }
                            .flatMap { getAllContactsExcludeWithIdsAndKeysSortedByNameAscWithChanges(it) }
                            .toFlowable()
                }
    }

    override fun getContactPhonesByDeviceContactExcludeSortedByNumberAsc(deviceContactItem: DeviceContactItem,
                                                                         excludeForSelectionContactPhones: List<DeviceContactPhoneItem>): Single<List<DeviceContactPhoneItem>> {
        return getContactPhonesByContactExclude(deviceContactItem, excludeForSelectionContactPhones, true)
                .flatMap {
                    cursorMapper.toSingleDevicePhonesList(it)
                        .toMaybe()
                }
                .switchIfEmpty(Single.fromCallable { emptyList<DeviceContactPhoneItem>() })
    }

    override fun getAllContactPhonesByDeviceContactExclude(deviceContactItem: DeviceContactItem,
                                                           excludeForSelectionContactPhones: List<DeviceContactPhoneItem>): Single<List<DeviceContactPhoneItem>> {
        return getContactPhonesByContactExclude(deviceContactItem, excludeForSelectionContactPhones, false)
                .flatMap {
                    cursorMapper.toSingleDevicePhonesList(it)
                        .toMaybe()
                }
                .switchIfEmpty(Single.fromCallable { emptyList<DeviceContactPhoneItem>() })
    }

    override fun getContactPhonesCountByDeviceContactExclude(deviceContactItem: DeviceContactItem,
                                                             excludeForSelectionContactPhones: List<DeviceContactPhoneItem>): Single<Int> {
        return getContactPhonesByContactExclude(deviceContactItem, excludeForSelectionContactPhones, false)
                .flatMap {
                    cursorMapper.toSingleCountOfData(it)
                        .toMaybe()
                }
                .switchIfEmpty(Single.just(0))
    }

    override fun getDeviceContactPhoneWithIdAndNumber(id: Long?, number: String): Maybe<DeviceContactPhoneItem> {
        return notNullIdOrEmptyAsString(id)
                .flatMap { nonNullPhoneId: String ->
                    Maybe.defer {
                        val selectionArgs = listOf(nonNullPhoneId, number).toTypedArray()
                        query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                PHONE_PROJECTION,
                                "${ContactsContract.Data.CONTACT_ID} = ? " +
                                        "AND ${ContactsContract.CommonDataKinds.Phone.NUMBER} = ? " +
                                        "AND ${ContactsContract.Data.MIMETYPE} = '${ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}'",
                                selectionArgs)
                    }
                }
                .flatMap { cursorMapper.toMaybeDevicePhoneIfNonEmpty(it) }
    }

    override fun getInitialAndAnyDeviceDataChangesSignal(): Flowable<Unit> {
        return getInitialAndAnyChangesSignal(Pair(ContactsContract.Contacts.CONTENT_URI, true),
                Pair(ContactsContract.Data.CONTENT_URI, true))
    }

    private fun getInitialAndAnyChangesSignal(vararg urisWithNotifyDesc: Pair<Uri, Boolean>): Flowable<Unit> {
        return Flowable.create<Unit> ({ emitter: FlowableEmitter<Unit> ->
            val observer = object : ContentObserver(null){
                override fun onChange(selfChange: Boolean) {
                    if(!emitter.isCancelled){
                        emitter.onNext(Unit)
                    }
                }
            }
            emitter.setCancellable( { contentResolver.unregisterContentObserver(observer)})
            urisWithNotifyDesc.forEach { uriWithNotifyDesc: Pair<Uri, Boolean> ->
                contentResolver.registerContentObserver(uriWithNotifyDesc.first, uriWithNotifyDesc.second, observer)
            }
            if(!emitter.isCancelled){
                emitter.onNext(Unit)
            }
        }, BackpressureStrategy.DROP)
    }

    private fun query(uri: Uri,
                      projection: Array<String>? = null,
                      selection: String? = null,
                      selectionArgs: Array<String>? = null,
                      sortOrder: String? = null) : Maybe<Cursor> {
        return Maybe.fromCallable { contentResolver.query(uri, projection, selection, selectionArgs, sortOrder) }
    }

    private fun getContactPhonesByContactExclude(deviceContactItem: DeviceContactItem,
                                                 excludeForSelectionContactPhones: List<DeviceContactPhoneItem>,
                                                 sortByNumberAsc: Boolean) : Maybe<Cursor>{
        return getNonNullIdAndLookupKeyOrEmpty(deviceContactItem)
                .flatMap { getActualIdAsStringOrEmpty(it.first, it.second) }
                .flatMap { actualContactId: String ->
                    getNonNullIdsAsString(excludeForSelectionContactPhones)
                            .flatMapMaybe { nonNullPhoneIds: List<String> ->
                                val selectionsArgs = mutableListOf<String>().apply {
                                    add(actualContactId)
                                    addAll(nonNullPhoneIds)
                                }.toTypedArray()
                                val sortOrder =
                                        if(sortByNumberAsc) "${ContactsContract.CommonDataKinds.Phone.NUMBER} ASC"
                                        else null
                                query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        PHONE_PROJECTION,
                                        "${ContactsContract.Data.CONTACT_ID} = ? " +
                                                "AND ${ContactsContract.Data._ID} NOT IN (${makePlaceholders(nonNullPhoneIds.count())})" +
                                                "AND ${ContactsContract.Data.MIMETYPE} = '${ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}'",
                                        selectionsArgs,
                                        sortOrder) }
                }
    }

    private fun getAllContactsExcludeWithIdsAndKeysSortedByNameAscWithChanges(idsForExclude: List<String>): Single<List<DeviceContactItem>> {
        return Maybe.defer {
            val idsStringArr = idsForExclude.toTypedArray()
            return@defer query(ContactsContract.Contacts.CONTENT_URI,
                    CONTACT_PROJECTION,
                    "${ContactsContract.Contacts._ID} NOT IN (${makePlaceholders(idsForExclude.count())})",
                    idsStringArr,
                    "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC")
        }
                .flatMap {
                    cursorMapper.toSingleDeviceContactList(it)
                            .toMaybe()
                }
                .switchIfEmpty(Single.fromCallable { emptyList<DeviceContactItem>() })
    }

    private fun getActualIdsAsString(oldIdsAndLookupKeys: List<Pair<Long, String?>>): Single<List<String>> {
        return Observable.fromIterable(oldIdsAndLookupKeys)
                .concatMap { getActualIdAsStringOrEmpty(it.first, it.second).toObservable()}
                .toList()
    }

    private fun getActualIdAsStringOrEmpty(oldId: Long, oldLookupKey: String?): Maybe<String> {
        return Maybe.defer {
                    val uri = ContactsContract.Contacts.getLookupUri(oldId, oldLookupKey)
                    return@defer query(uri, arrayOf(ContactsContract.Contacts._ID))
                }
                .flatMap { cursorMapper.toMaybeDeviceContactIdIfNonEmpty(it)}
                .map { it.toString() }
    }

    private fun getNonNullIdsAndLookupKeys(excludeForSelectionContacts: List<DeviceContactItem>) : Single<List<Pair<Long, String?>>> {
        return Single.fromCallable {
            return@fromCallable excludeForSelectionContacts.filter { it.id != null }
                    .map { Pair(it.id!!, it.lookupKey) }

        }
    }

    private fun getNonNullIdAndLookupKeyOrEmpty(contact: DeviceContactItem) : Maybe<Pair<Long, String?>> =
            if(contact.id == null) Maybe.empty() else Maybe.just(Pair(contact.id!!, contact.lookupKey))

    private fun getNonNullIdsAsString(phones: List<DeviceContactPhoneItem>) : Single<List<String>> {
        return Single.fromCallable {
            return@fromCallable phones.mapNotNull { it.id }
                    .map { it.toString() }
        }
    }

    private fun notNullIdOrEmptyAsString(id: Long?) : Maybe<String> =
            Maybe.fromCallable { id?.toString() }

    private fun notNullIdOrEmpty(id: Long?) : Maybe<Long> =
            Maybe.fromCallable { id }

    private fun makePlaceholders(count: Int) : String {
        return if(count == 0) ""
        else{
            StringBuilder().apply {
                append("?")
                for(i in 2.. count){
                    append(", ?")
                }
            }.toString()
        }
    }

    companion object {

        @JvmField
        val CONTACT_PROJECTION = arrayOf(ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.PHOTO_URI)

        @JvmField
        val PHONE_PROJECTION = arrayOf(ContactsContract.Data._ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER)
    }
}