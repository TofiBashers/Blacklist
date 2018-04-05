package com.gmail.tofibashers.blacklist.di.app

import com.gmail.tofibashers.blacklist.data.repo.*
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.01.2018.
 */

@Module
abstract class RepositoryAndPreferenceDataModule {

    @Binds
    @Singleton
    abstract fun provideIBlackListElementRepository(repository: BlacklistItemRepository)
            : IBlacklistItemRepository

    @Binds
    @Singleton
    abstract fun provideIActivityIntervalRepository(repository: ActivityIntervalRepository)
            : IActivityIntervalRepository

    @Binds
    @Singleton
    abstract fun provideIModeRepository(repository: InteractionModeRepository) : IInteractionModeRepository

    @Binds
    @Singleton
    abstract fun provideIActivityIntervalWithBlacklistItemsRepository(repository: BlacklistItemWithActivityIntervalsRepository)
            : IBlacklistItemWithActivityIntervalsRepository

    @Binds
    @Singleton
    abstract fun provideIPreferencesData(preferencesData: PreferencesData) : IPreferencesData

    @Binds
    @Singleton
    abstract fun provideIDeviceData(deviceData: DeviceData) : IDeviceData
}