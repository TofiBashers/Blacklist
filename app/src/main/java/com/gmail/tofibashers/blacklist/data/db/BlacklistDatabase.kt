package com.gmail.tofibashers.blacklist.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.gmail.tofibashers.blacklist.data.db.converters.LocalTimeISO8601StringConverter
import com.gmail.tofibashers.blacklist.data.db.dao.IActivityIntervalDao
import com.gmail.tofibashers.blacklist.data.db.dao.IBlackListItemDao
import com.gmail.tofibashers.blacklist.data.db.dao.IJoinBlacklistItemActivityIntervalDao
import com.gmail.tofibashers.blacklist.data.db.entity.DbActivityInterval
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItem
import com.gmail.tofibashers.blacklist.data.db.entity.DbJoinBlacklistItemActivityInterval


/**
 * Created by TofiBashers on 18.01.2018.
 */

@Database(
        version = BlacklistDatabase.DB_VERSION,
        entities = arrayOf(
                DbBlacklistItem::class,
                DbActivityInterval::class,
                DbJoinBlacklistItemActivityInterval::class
        ))
@TypeConverters(LocalTimeISO8601StringConverter::class)
abstract class BlacklistDatabase : RoomDatabase() {

    abstract fun blackListItemDao() : IBlackListItemDao
    abstract fun activityIntervalDao() : IActivityIntervalDao
    abstract fun joinBlacklistItemActivityIntervalDao() : IJoinBlacklistItemActivityIntervalDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "db_blacklist"
    }
}