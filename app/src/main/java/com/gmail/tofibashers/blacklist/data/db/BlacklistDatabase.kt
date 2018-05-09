package com.gmail.tofibashers.blacklist.data.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
import com.gmail.tofibashers.blacklist.data.db.converters.LocalTimeISO8601StringConverter
import com.gmail.tofibashers.blacklist.data.db.dao.*
import com.gmail.tofibashers.blacklist.data.db.entity.*
import com.gmail.tofibashers.blacklist.data.db.table_constants.ActivityIntervalTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistContactItemTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistContactPhoneItemTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.JoinBlacklistContactPhoneItemActivityIntervalTable


/**
 * Created by TofiBashers on 18.01.2018.
 */

@Database(
        version = BlacklistDatabase.DB_CURRENT_VERSION,
        entities = arrayOf(
                DbBlacklistItem::class,
                DbActivityInterval::class,
                DbBlacklistContactItem::class,
                DbBlacklistContactPhoneItem::class,
                DbJoinBlacklistItemActivityInterval::class,
                DbJoinBlacklistContactPhoneItemActivityInterval::class
        ))
@TypeConverters(LocalTimeISO8601StringConverter::class)
abstract class BlacklistDatabase : RoomDatabase() {

    abstract fun blackListItemDao() : IBlackListItemDao
    abstract fun activityIntervalDao() : IActivityIntervalDao
    abstract fun joinBlacklistItemActivityIntervalDao() : IJoinBlacklistItemActivityIntervalDao
    abstract fun blacklistContactItemDao() : IBlacklistContactItemDao
    abstract fun blacklistContactPhoneItemDao() : IBlacklistContactPhoneItemDao
    abstract fun joinBlacklistContactPhoneItemActivityIntervalDao() : IJoinBlacklistContactPhoneItemActivityIntervalDao

    companion object {
        const val DB_CURRENT_VERSION = 2
        const val DB_NAME = "db_blacklist"
    }
}