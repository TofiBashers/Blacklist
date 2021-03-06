package com.gmail.tofibashers.blacklist.di.app

import com.gmail.tofibashers.blacklist.domain.*
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.01.2018.
 */

@Module
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun provideIGetBlacklistItemsUseCase(useCase: GetBlacklistItemsSortByNumberWithIgnoreHiddenUseCase)
            : IGetBlacklistItemsSortByNumberWithIgnoreHiddenUseCase

    @Binds
    @Singleton
    abstract fun provideIGetModeWithSelectedBlacklistItemUseCase(useCase: GetInteractionModeWithSelectedBlacklistItemUseCase)
            : IGetInteractionModeWithSelectedBlacklistItemUseCase

    @Binds
    @Singleton
    abstract fun provideISaveBlacklistItemUseCase(useCase: SaveBlacklistPhoneItemWithDeleteSelectionsUseCase)
            : ISaveBlacklistPhoneItemWithDeleteSelectionsUseCase

    @Binds
    @Singleton
    abstract fun provideISelectBlacklisItemElementAndEditModeUseCase(useCase: SelectEditModeAndPhoneNumberItemUseCase)
            : ISelectEditModeAndPhoneNumberItemUseCase

    @Binds
    @Singleton
    abstract fun provideISelectCreateModeUseCase(useCase: SelectCreateModeUseCase)
            : ISelectCreateModeUseCase

    @Binds
    @Singleton
    abstract fun provideIDeleteBlacklistPhoneNumberItemUseCase(useCase: DeletePhoneNumberItemUseCase)
            : IDeletePhoneNumberItemUseCase

    @Binds
    @Singleton
    abstract fun provideIDeleteAllSelectionsUseCase(useCase: DeleteAllSelectionsUseCase)
            : IDeleteAllSelectionsUseCase

    @Binds
    @Singleton
    abstract fun provideIDeleteAllSelectionsSyncUseCase(useCase: DeleteAllSelectionsSyncUseCase)
            : IDeleteAllSelectionsSyncUseCase

    @Binds
    @Singleton
    abstract fun provideIGetSelectedActivityIntervalsUseCase(useCase: GetSelectedOrNewActivityIntervalsUseCase)
            : IGetSelectedOrNewActivityIntervalsUseCase

    @Binds
    @Singleton
    abstract fun provideISelectActivityIntervalsUseCase(useCase: SelectOnlyEnabledActivityIntervalsUseCase)
            : ISelectOnlyEnabledActivityIntervalsUseCase

    @Binds
    @Singleton
    abstract fun provideICheckNumberMustBeIgnoredNowSyncUseCase(useCase: CheckNumberMustBeIgnoredNowSyncUseCase)
            : ICheckNumberMustBeIgnoredNowSyncUseCase

    @Binds
    @Singleton
    abstract fun provideIGetIgnoredNumbersWithTimeAndSettingsUseCase(useCase: GetAllIgnoredInfoOptimizedForAccessWithChangesUseCase)
            : IGetAllIgnoredInfoOptimizedForAccessWithChangesUseCase

    @Binds
    @Singleton
    abstract fun provideISetEnableActivityIntervalAndValidateToSaveUseCase(useCase: SetEnableActivityIntervalAndValidateToSaveSyncUseCase)
            : ISetEnableActivityIntervalAndValidateToSaveSyncUseCase

    @Binds
    @Singleton
    abstract fun provideIValidateBlacklistPhoneNumberItemForSaveUseCase(useCase: ValidateBlacklistPhoneNumberItemForSaveSyncUseCase)
            : IValidateBlacklistPhoneNumberItemForSaveSyncUseCase

    @Binds
    @Singleton
    abstract fun provideISetDisableActivityIntervalAndValidateToSaveUseCase(useCase: SetDisableActivityIntervalAndValidateToSaveSyncUseCase)
            : ISetDisableActivityIntervalAndValidateToSaveSyncUseCase

    @Binds
    @Singleton
    abstract fun provideISaveIgnoreHiddenNumbersSyncUseCase(useCase: SaveIgnoreHiddenNumbersSyncUseCase)
            : ISaveIgnoreHiddenNumbersSyncUseCase

    @Binds
    @Singleton
    abstract fun provideICreateTimeChangeInitDataSyncUseCase(useCase: CreateTimeChangeInitDataUseCase)
            : ICreateTimeChangeInitDataUseCase

    @Binds
    @Singleton
    abstract fun provideIGetAllNonIgnoredContactsUseCase(useCase: GetAllNonIgnoredContactsUseCase)
            : IGetAllNonIgnoredContactsWithChangesUseCase

    @Binds
    @Singleton
    abstract fun provideISelectContactItemUseCase(useCase: SelectWhitelistContactItemWithPhonesUseCase)
            : ISelectWhitelistContactItemWithPhonesUseCase

    @Binds
    @Singleton
    abstract fun provideIValidateBlacklistContactPhoneNumbersForSaveSyncUseCase(useCase: ValidateBlacklistContactPhoneNumbersForSaveSyncUseCase)
            : IValidateBlacklistContactPhoneNumbersForSaveSyncUseCase

    @Binds
    @Singleton
    abstract fun provideIValidateBlacklistContactPhoneNumberForSaveSyncUseCase(useCase: ValidateBaseBlacklistPhoneForSaveSyncUseCase)
            : IValidateBaseBlacklistPhoneForSaveSyncUseCase

    @Binds
    @Singleton
    abstract fun provideIGetInteractionModeWithSelectedBlacklistContactItemUseCase(useCase: GetInteractionModeWithSelectedBlacklistContactItemUseCase)
            : IGetInteractionModeWithSelectedBlacklistContactItemUseCase

    @Binds
    @Singleton
    abstract fun provideISelectForEditActivityIntervalsOfBlacklistContactPhoneUseCase(useCase: SelectForEditActivityIntervalsOfBlacklistContactPhoneUseCase)
            : ISelectForEditActivityIntervalsOfBlacklistContactPhoneUseCase

    @Binds
    @Singleton
    abstract fun provideISaveSelectedActivityIntervalsToAllBlacklistContactPhonesIntervals(useCase: SaveSelectedForEditActivityIntervalsToAllBlacklistContactPhonesIntervals)
            : ISaveSelectedForEditActivityIntervalsToAllBlacklistContactPhonesIntervalsUseCase

    @Binds
    @Singleton
    abstract fun provideISaveBlacklistContactItemWithDeleteSelectionsUseCase(useCase: SaveBlacklistContactItemWithOnlyBlacklistPhonesWithDeleteSelectionsUseCase)
            : ISaveBlacklistContactItemWithOnlyBlacklistPhonesWithDeleteSelectionsUseCase

    @Binds
    @Singleton
    abstract fun provideISelectEditModeAndContactItemUseCase(useCase: SelectEditModeAndBlacklistContactItemWithPhonesAndIntervalsUseCase)
            : ISelectEditModeAndBlacklistContactItemWithPhonesAndIntervalsUseCase

    @Binds
    @Singleton
    abstract fun provideISelectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase(useCase: SelectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase)
            : ISelectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase

    @Binds
    @Singleton
    abstract fun provideIDeleteBlacklistContactItemUseCase(useCase: DeleteBlacklistContactItemUseCase)
            : IDeleteBlacklistContactItemUseCase

    @Binds
    @Singleton
    abstract fun provideICreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase(useCase: CreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase)
            : ICreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase
}