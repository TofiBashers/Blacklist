package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.*
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistPhoneItemMapper
import com.gmail.tofibashers.blacklist.utils.TimeFormatUtils
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.01.2018.
 */

@Singleton
class SaveBlacklistPhoneItemWithDeleteSelectionsUseCase
@Inject
constructor(private val activityIntervalRepository: IActivityIntervalRepository,
            private val blacklistItemRepository: IBlacklistItemRepository,
            private val blacklistItemWithActivityIntervalsRepository: IBlacklistItemWithActivityIntervalsRepository,
            private val interactionModeRepository: IInteractionModeRepository,
            private val deleteAllSelectionsSyncUseCase: IDeleteAllSelectionsSyncUseCase,
            private val blacklistPhoneItemMapper: BlacklistPhoneItemMapper,
            private val activityIntervalFactory: ActivityIntervalFactory,
            private val timeFormatUtils: TimeFormatUtils) : ISaveBlacklistPhoneItemWithDeleteSelectionsUseCase {

    override fun build(phoneNumberItem: BlacklistPhoneNumberItem): Completable {
        return interactionModeRepository.getSelectedMode()
                .switchIfEmpty(Single.error(RuntimeException("Mode not selected when saving")))
                .flatMap {mode: InteractionMode ->
                    return@flatMap if(mode == InteractionMode.CREATE){
                        validateNumberNonExists(phoneNumberItem.number)
                                .andThen(activityIntervalRepository.getSelectedActivityIntervals())
                                .switchIfEmpty(createDefaultIntervals())
                    }
                    else {
                        Single.fromCallable { phoneNumberItem.dbId ?: throw RuntimeException("Non saved phoneNumberItem in Edit mode") }
                                .flatMapCompletable { validateIsNotAnotherNumber(it, phoneNumberItem.number) }
                                .andThen(activityIntervalRepository.getSelectedActivityIntervals())
                                .switchIfEmpty(Single.error<List<ActivityInterval>>(RuntimeException("Intervals not selected in Edit mode")))
                    }
                }
                .map { blacklistPhoneItemMapper.toBlacklistItemWithActivityIntervals(phoneNumberItem, it) }
                .flatMapCompletable { blacklistItemWithActivityIntervalsRepository.put(it) }
                .andThen(deleteAllSelectionsSyncUseCase.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun createDefaultIntervals(): Single<List<ActivityInterval>> {
        return Observable.fromIterable(timeFormatUtils.getWeekdayIdsInNonLocalizedOrder())
                .map { activityIntervalFactory.create(it) }
                .toList()
    }

    private fun validateNumberNonExists(number: String): Completable{
        return blacklistItemRepository.getByNumber(number)
                .flatMapCompletable { Completable.error(NumberAlreadyExistsException()) }
    }

    private fun validateIsNotAnotherNumber(dbId: Long, number: String): Completable{
        return blacklistItemRepository.getByNumber(number)
                .map { existentPhoneNumberItem: BlacklistPhoneNumberItem ->
                    val itemId = existentPhoneNumberItem.dbId ?: throw RuntimeException("Saved phoneNumberItem without Id")
                    return@map itemId.equals(dbId)
                }
                .flatMapCompletable { isCurrentItem: Boolean ->
                    return@flatMapCompletable  if(!isCurrentItem){
                        Completable.error(NumberAlreadyExistsException())
                    }
                    else { Completable.complete() }
                }
    }
}