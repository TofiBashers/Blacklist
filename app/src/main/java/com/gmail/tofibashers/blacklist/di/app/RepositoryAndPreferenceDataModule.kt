package com.gmail.tofibashers.blacklist.di.app

import com.gmail.tofibashers.blacklist.data.ISynchronizeDataUseCase
import com.gmail.tofibashers.blacklist.data.SynchronizeDataUseCase
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
    abstract fun provideIBlacklistPhoneNumberItemRepository(repository: BlacklistPhoneNumberItemRepository)
            : IBlacklistPhoneNumberItemRepository

    @Binds
    @Singleton
    abstract fun provideIActivityIntervalRepository(repository: ActivityIntervalRepository)
            : IActivityIntervalRepository

    @Binds
    @Singleton
    abstract fun provideIContactItemRepository(repository: WhitelistContactItemRepository)
            : IWhitelistContactItemRepository

    @Binds
    @Singleton
    abstract fun provideIBlacklistContactItemRepository(repository: BlacklistContactItemRepository)
            : IBlacklistContactItemRepository

    @Binds
    @Singleton
    abstract fun provideIBlacklistContactItemWithPhonesAndActivityIntervalsRepository(repository: BlacklistContactItemWithPhonesAndActivityIntervalsRepository)
            : IBlacklistContactItemWithPhonesAndActivityIntervalsRepository

    @Binds
    @Singleton
    abstract fun provideIBlacklistContactPhoneRepository(repository: BlacklistContactPhoneRepository)
            : IBlacklistContactPhoneRepository

    @Binds
    @Singleton
    abstract fun provideIContactPhoneRepository(repository: WhitelistContactPhoneRepository)
            : IWhitelistContactPhoneRepository

    @Binds
    @Singleton
    abstract fun provideIModeRepository(repository: InteractionModeRepository) : IInteractionModeRepository

    @Binds
    @Singleton
    abstract fun provideIActivityIntervalWithBlacklistPhoneNumberItemsRepository(repository: BlacklistPhoneNumberItemWithActivityIntervalsRepository)
            : IBlacklistPhoneNumberItemWithActivityIntervalsRepository

    @Binds
    @Singleton
    abstract fun provideIBlacklistContactPhoneWithActivityIntervalsRepository(repository: BlacklistContactPhoneWithActivityIntervalsRepository)
            : IBlacklistContactPhoneWithActivityIntervalsRepository

    @Binds
    @Singleton
    abstract fun provideIPreferencesData(preferencesData: PreferencesData) : IPreferencesData

    @Binds
    @Singleton
    abstract fun provideIDeviceData(deviceData: DeviceData) : IDeviceData

    @Binds
    @Singleton
    abstract fun provideISynchronizeDataUseCase(synchronizeDataUseCase: SynchronizeDataUseCase) : ISynchronizeDataUseCase
}