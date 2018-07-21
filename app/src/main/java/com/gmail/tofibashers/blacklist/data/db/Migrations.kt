package com.gmail.tofibashers.blacklist.data.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import com.gmail.tofibashers.blacklist.data.db.table_constants.*


/**
 * Created by TofiBashers on 29.04.2018.
 */
object Migrations {

    @JvmField
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE ${BlacklistContactItemTable.TABLE_NAME} (" +
                    "${BlacklistContactItemTable._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${BlacklistContactItemTable.DEVICE_DB_ID} INTEGER, " +
                    "${BlacklistContactItemTable.DEVICE_LOOKUP_KEY} TEXT, " +
                    "${BlacklistContactItemTable.NAME} TEXT NOT NULL, " +
                    "${BlacklistContactItemTable.PHOTO_URL} TEXT)")

            database.execSQL("CREATE TABLE ${BlacklistContactPhoneItemTable.TABLE_NAME} (" +
                    "${BlacklistContactPhoneItemTable._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID} INTEGER, " +
                    "${BlacklistContactPhoneItemTable.DEVICE_DB_ID} INTEGER, " +
                    "${BlacklistContactPhoneItemTable.NUMBER} TEXT NOT NULL, " +
                    "${BlacklistContactPhoneItemTable.IGNORE_CALLS} INTEGER NOT NULL, " +
                    "${BlacklistContactPhoneItemTable.IGNORE_SMS} INTEGER NOT NULL, " +
                    "FOREIGN KEY(${BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID}) REFERENCES ${BlacklistContactItemTable.TABLE_NAME}(${BlacklistContactItemTable._ID}) ON UPDATE NO ACTION ON DELETE NO ACTION )")

            database.execSQL("CREATE INDEX ${BlacklistContactPhoneItemTable.INDEX_BLACKLIST_CONTACT_PHONE_ITEM_BLACKLIST_CONTACT_ID} " +
                    "ON ${BlacklistContactPhoneItemTable.TABLE_NAME} (${BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID})")

            database.execSQL("CREATE TABLE ${JoinBlacklistContactPhoneItemActivityIntervalTable.TABLE_NAME} (" +
                    "${JoinBlacklistContactPhoneItemActivityIntervalTable.BLACKLIST_CONTACT_PHONE_ITEM_ID} INTEGER NOT NULL, " +
                    "${JoinBlacklistContactPhoneItemActivityIntervalTable.ACTIVITY_INTERVAL_ID} INTEGER NOT NULL, " +
                    "PRIMARY KEY(${JoinBlacklistContactPhoneItemActivityIntervalTable.BLACKLIST_CONTACT_PHONE_ITEM_ID}, ${JoinBlacklistContactPhoneItemActivityIntervalTable.ACTIVITY_INTERVAL_ID}), " +
                    "FOREIGN KEY(${JoinBlacklistContactPhoneItemActivityIntervalTable.ACTIVITY_INTERVAL_ID}) REFERENCES ${ActivityIntervalTable.TABLE_NAME}(${ActivityIntervalTable._ID}) ON UPDATE NO ACTION ON DELETE NO ACTION , " +
                    "FOREIGN KEY(${JoinBlacklistContactPhoneItemActivityIntervalTable.BLACKLIST_CONTACT_PHONE_ITEM_ID}) REFERENCES ${BlacklistContactPhoneItemTable.TABLE_NAME}(${BlacklistContactPhoneItemTable._ID}) ON UPDATE NO ACTION ON DELETE NO ACTION )")
        }
    }
}