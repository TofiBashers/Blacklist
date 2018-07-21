package com.gmail.tofibashers.blacklist.data.device.mapper

import android.database.Cursor
import android.provider.ContactsContract
import com.gmail.tofibashers.blacklist.data.device.DeviceContactItem
import com.gmail.tofibashers.blacklist.data.device.DeviceContactItemFactory
import com.gmail.tofibashers.blacklist.data.device.DeviceContactPhoneItem
import com.gmail.tofibashers.blacklist.data.device.DeviceContactPhoneItemFactory
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CursorMapper
@Inject
constructor(private val deviceContactItemFactory: DeviceContactItemFactory,
            private val deviceContactPhoneItemFactory: DeviceContactPhoneItemFactory) {

    fun toMaybeDeviceContactIfNonEmpty(cursor: Cursor) : Maybe<DeviceContactItem> {
        return Maybe.defer {
            if(cursor.moveToFirst()){
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val lookupKey = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                val photoUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))
                return@defer Maybe.just(deviceContactItemFactory.create(id, lookupKey, name, photoUri))
            }
            return@defer Maybe.empty<DeviceContactItem>()
        }
    }

    fun toMaybeDeviceContactIdIfNonEmpty(cursor: Cursor) : Maybe<Long> {
        return Maybe.defer {
            if(cursor.moveToFirst()) {
                return@defer Maybe.just(cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)))
            }
            return@defer Maybe.empty<Long>()
        }
    }

    fun toSingleDeviceContactList(cursor: Cursor) : Single<List<DeviceContactItem>> {
        return Single.defer {
            val contacts = mutableListOf<DeviceContactItem>()
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val lookupKey = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                val photoUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))
                contacts.add(deviceContactItemFactory.create(id, lookupKey, name, photoUri))
                cursor.moveToNext()
            }
            return@defer Single.just(contacts)
        }
    }

    fun toMaybeDevicePhoneIfNonEmpty(cursor: Cursor) : Maybe<DeviceContactPhoneItem> {
        return Maybe.defer {
            if(cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Data._ID))
                val number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                return@defer Maybe.just(deviceContactPhoneItemFactory.create(id, number))
            }
            return@defer Maybe.empty<DeviceContactPhoneItem>()
        }
    }

    fun toSingleDevicePhonesList(cursor: Cursor) : Single<List<DeviceContactPhoneItem>> {
        return Single.defer {
            val phones = mutableListOf<DeviceContactPhoneItem>()
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Data._ID))
                val number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                phones.add(deviceContactPhoneItemFactory.create(id, number))
                cursor.moveToNext()
            }
            return@defer Single.just(phones.toList())
        }
    }

    fun toSingleCountOfData(cursor: Cursor) : Single<Int> {
        return Single.fromCallable { cursor.count }
    }
}