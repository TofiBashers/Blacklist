package com.gmail.tofibashers.blacklist.di.app

import android.arch.persistence.room.Room
import android.content.Context
import com.gmail.tofibashers.blacklist.BuildConfig
import com.gmail.tofibashers.blacklist.data.datasource.DatabaseSource
import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.db.BlacklistDatabase
import com.gmail.tofibashers.blacklist.data.db.dao.IActivityIntervalDao
import com.gmail.tofibashers.blacklist.data.db.dao.IBlackListItemDao
import com.gmail.tofibashers.blacklist.data.db.dao.IJoinBlacklistItemActivityIntervalDao
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItemWithActivityIntervalsFactory
import com.gmail.tofibashers.blacklist.data.db.entity.DbJoinBlacklistItemActivityIntervalFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by TofiBashers on 22.01.2018.
 */

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(appContext: Context) : BlacklistDatabase {
        return if(BuildConfig.DEBUG) Room.inMemoryDatabaseBuilder(appContext,
                BlacklistDatabase::class.java)
                .build()
        else Room.databaseBuilder(appContext,
                    BlacklistDatabase::class.java, BlacklistDatabase.DB_NAME)
                    .build()
    }

    @Singleton
    @Provides
    fun provideActivityIntervalDao(database: BlacklistDatabase) : IActivityIntervalDao =
            database.activityIntervalDao()

    @Singleton
    @Provides
    fun provideBlacklistItemDao(database: BlacklistDatabase) : IBlackListItemDao =
            database.blackListItemDao()

    @Singleton
    @Provides
    fun provideJoinBlacklistItemActivityIntervalDao(database: BlacklistDatabase) : IJoinBlacklistItemActivityIntervalDao =
            database.joinBlacklistItemActivityIntervalDao()

    @Singleton
    @Provides
    fun provideDatabaseSource(blacklistDatabase: BlacklistDatabase,
                              activityIntervalDao: IActivityIntervalDao,
                              blacklistItemDao: IBlackListItemDao,
                              joinBlacklistItemActivityIntervalDao: IJoinBlacklistItemActivityIntervalDao,
                              dbJoinBlacklistItemActivityIntervalFactory: DbJoinBlacklistItemActivityIntervalFactory,
                              dbBlacklistItemWithActivityIntervalsFactory: DbBlacklistItemWithActivityIntervalsFactory) : IDatabaseSource =
            DatabaseSource(blacklistDatabase,
                    activityIntervalDao,
                    blacklistItemDao,
                    joinBlacklistItemActivityIntervalDao,
                    dbJoinBlacklistItemActivityIntervalFactory,
                    dbBlacklistItemWithActivityIntervalsFactory)
}