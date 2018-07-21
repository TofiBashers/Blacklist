package com.gmail.tofibashers.blacklist.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.gmail.tofibashers.blacklist.data.db.converters.LocalTimeISO8601StringConverter
import com.gmail.tofibashers.blacklist.data.db.dao.*
import com.gmail.tofibashers.blacklist.data.db.entity.*


/**
 * Created by TofiBashers on 18.01.2018.
 */

@Database(
        version = BlacklistDatabase.DB_CURRENT_VERSION,
        entities = arrayOf(
                DbBlacklistPhoneNumberItem::class,
                DbActivityInterval::class,
                DbBlacklistContactItem::class,
                DbBlacklistContactPhoneItem::class,
                DbJoinBlacklistPhoneNumberItemActivityInterval::class,
                DbJoinBlacklistContactPhoneItemActivityInterval::class
        ))
@TypeConverters(LocalTimeISO8601StringConverter::class)
abstract class BlacklistDatabase : RoomDatabase() {

    abstract fun blacklistPhoneNumberItemDao() : IBlacklistPhoneNumberItemDao
    abstract fun activityIntervalDao() : IActivityIntervalDao
    abstract fun joinBlacklistPhoneNumberItemActivityIntervalDao() : IJoinBlacklistPhoneNumberItemActivityIntervalDao
    abstract fun blacklistContactItemDao() : IBlacklistContactItemDao
    abstract fun blacklistContactPhoneItemDao() : IBlacklistContactPhoneItemDao
    abstract fun joinBlacklistContactPhoneItemActivityIntervalDao() : IJoinBlacklistContactPhoneItemActivityIntervalDao

    companion object {
        const val DB_CURRENT_VERSION = 2
        const val DB_NAME = "db_blacklist"
    }
}