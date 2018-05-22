package com.gmail.tofibashers.blacklist.di.app

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.Room
import android.content.Context
import com.gmail.tofibashers.blacklist.BuildConfig
import com.gmail.tofibashers.blacklist.data.datasource.DatabaseSource
import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.db.BlacklistDatabase
import com.gmail.tofibashers.blacklist.data.db.Migrations
import com.gmail.tofibashers.blacklist.data.db.dao.*
import com.gmail.tofibashers.blacklist.data.db.entity.*
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
        /**return if(BuildConfig.DEBUG) Room.inMemoryDatabaseBuilder(appContext,
                BlacklistDatabase::class.java)
                .addMigrations(Migrations.MIGRATION_1_2)
                .build()
        else*/
        return Room.databaseBuilder(appContext,
                BlacklistDatabase::class.java, BlacklistDatabase.DB_NAME)
                .addMigrations(Migrations.MIGRATION_1_2)
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
    fun provideIBlacklistContactItemDao(database: BlacklistDatabase) : IBlacklistContactItemDao =
            database.blacklistContactItemDao()

    @Singleton
    @Provides
    fun provideIBlacklistContactPhoneItemDao(database: BlacklistDatabase) : IBlacklistContactPhoneItemDao =
            database.blacklistContactPhoneItemDao()

    @Singleton
    @Provides
    fun provideIJoinBlacklistContactPhoneItemActivityIntervalDao(database: BlacklistDatabase) : IJoinBlacklistContactPhoneItemActivityIntervalDao =
            database.joinBlacklistContactPhoneItemActivityIntervalDao()

    @Singleton
    @Provides
    fun provideDatabaseSource(blacklistDatabase: BlacklistDatabase,
                              activityIntervalDao: IActivityIntervalDao,
                              blacklistItemDao: IBlackListItemDao,
                              joinBlacklistItemActivityIntervalDao: IJoinBlacklistItemActivityIntervalDao,
                              blacklistContactItemDao: IBlacklistContactItemDao,
                              blacklistContactPhoneItemDao: IBlacklistContactPhoneItemDao,
                              joinBlacklistContactPhoneItemActivityIntervalDao: IJoinBlacklistContactPhoneItemActivityIntervalDao,
                              dbJoinBlacklistItemActivityIntervalFactory: DbJoinBlacklistItemActivityIntervalFactory,
                              dbBlacklistItemWithActivityIntervalsFactory: DbBlacklistItemWithActivityIntervalsFactory,
                              dbJoinBlacklistContactPhoneItemActivityIntervalFactory: DbJoinBlacklistContactPhoneItemActivityIntervalFactory,
                              dbBlacklistContactPhoneWithActivityIntervalsFactory: DbBlacklistContactPhoneWithActivityIntervalsFactory,
                              dbBlacklistContactItemWithPhonesAndIntervalsFactory: DbBlacklistContactItemWithPhonesAndIntervalsFactory) : IDatabaseSource =
            DatabaseSource(blacklistDatabase,
                    activityIntervalDao,
                    blacklistItemDao,
                    joinBlacklistItemActivityIntervalDao,
                    blacklistContactItemDao,
                    blacklistContactPhoneItemDao,
                    joinBlacklistContactPhoneItemActivityIntervalDao,
                    dbJoinBlacklistItemActivityIntervalFactory,
                    dbBlacklistItemWithActivityIntervalsFactory,
                    dbJoinBlacklistContactPhoneItemActivityIntervalFactory,
                    dbBlacklistContactPhoneWithActivityIntervalsFactory,
                    dbBlacklistContactItemWithPhonesAndIntervalsFactory)
}