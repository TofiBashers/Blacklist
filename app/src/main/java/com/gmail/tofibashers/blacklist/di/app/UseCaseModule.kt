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
    abstract fun provideIGetModeWithSelectedBlackListElementUseCase(useCase: GetInteractionModeWithSelectedBlackListItemUseCase)
            : IGetInteractionModeWithSelectedBlackListItemUseCase

    @Binds
    @Singleton
    abstract fun provideISaveBlacklistElementUseCase(useCase: SaveBlacklistPhoneItemWithDeleteSelectionsUseCase)
            : ISaveBlacklistPhoneItemWithDeleteSelectionsUseCase

    @Binds
    @Singleton
    abstract fun provideISelectBlacklistElementAndEditModeUseCase(useCase: SelectEditModeAndPhoneNumberItemUseCase)
            : ISelectEditModeAndPhoneNumberItemUseCase

    @Binds
    @Singleton
    abstract fun provideISelectCreateModeUseCase(useCase: SelectCreateModeUseCase)
            : ISelectCreateModeUseCase

    @Binds
    @Singleton
    abstract fun provideIDeleteBlacklistItemUseCase(useCase: DeletePhoneNumberItemUseCase)
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
    abstract fun provideIValidateBlacklistItemForSaveUseCase(useCase: ValidateBlacklistItemForSaveSyncUseCase)
            : IValidateBlacklistItemForSaveSyncUseCase

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
    abstract fun provideISelectContactItemUseCase(useCase: SelectContactItemUseCase)
            : ISelectContactItemUseCase

    @Binds
    @Singleton
    abstract fun provideIValidateBlacklistContactPhoneNumbersForSaveSyncUseCase(useCase: ValidateBlacklistContactPhoneNumbersForSaveSyncUseCase)
            : IValidateBlacklistContactPhoneNumbersForSaveSyncUseCase

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
    abstract fun provideISaveSelectedActivityIntervalsToAllBlacklistContactPhonesIntervals(useCase: SaveSelectedActivityIntervalsToAllBlacklistContactPhonesIntervals)
            : ISaveSelectedActivityIntervalsToAllBlacklistContactPhonesIntervalsUseCase

    @Binds
    @Singleton
    abstract fun provideISaveBlacklistContactItemWithDeleteSelectionsUseCase(useCase: SaveBlacklistContactItemWithDeleteSelectionsUseCase)
            : ISaveBlacklistContactItemWithDeleteSelectionsUseCase

    @Binds
    @Singleton
    abstract fun provideISelectEditModeAndContactItemUseCase(useCase: SelectEditModeAndContactItemUseCase)
            : ISelectEditModeAndContactItemUseCase

    @Binds
    @Singleton
    abstract fun provideIDeleteBlacklistContactItemUseCase(useCase: DeleteBlacklistContactItemUseCase)
            : IDeleteBlacklistContactItemUseCase

    @Binds
    @Singleton
    abstract fun provideICreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase(useCase: CreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase)
            : ICreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase
}