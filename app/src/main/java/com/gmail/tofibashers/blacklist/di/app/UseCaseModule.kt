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
    abstract fun provideISaveBlacklistElementUseCase(useCase: SaveBlacklistItemWithDeleteSelectionsUseCase)
            : ISaveBlacklistItemWithDeleteSelectionsUseCase

    @Binds
    @Singleton
    abstract fun provideISelectBlacklistElementAndEditModeUseCase(useCase: SelectEditModeAndBlacklistItemUseCase)
            : ISelectEditModeAndBlacklistItemUseCase

    @Binds
    @Singleton
    abstract fun provideISelectCreateModeUseCase(useCase: SelectCreateModeUseCase)
            : ISelectCreateModeUseCase

    @Binds
    @Singleton
    abstract fun provideIDeleteBlacklistItemUseCase(useCase: DeleteBlacklistItemUseCase)
            : IDeleteBlacklistItemUseCase

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
}