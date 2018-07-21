package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.*
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemWithNonIgnoredNumbersFlagMapper
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.04.2018.
 */
@Singleton
class SelectEditModeAndBlacklistContactItemWithPhonesAndIntervalsUseCase
@Inject
constructor(
        private val blacklistContactRepository: IBlacklistContactItemRepository,
        private val interactionModeRepository: IInteractionModeRepository,
        private val blacklistContactPhoneWithActivityIntervalsRepository: IBlacklistContactPhoneWithActivityIntervalsRepository,
        private val whitelistContactPhoneRepository: IWhitelistContactPhoneRepository,
        private val blacklistContactItemWithNonIgnoredNumbersFlagMapper: BlacklistContactItemWithNonIgnoredNumbersFlagMapper,
        private val blacklistContactItemMapper: BlacklistContactItemMapper
) : ISelectEditModeAndBlacklistContactItemWithPhonesAndIntervalsUseCase {

    override fun build(item: BlacklistContactItemWithNonIgnoredNumbersFlag): Completable {
        return Single.fromCallable { blacklistContactItemWithNonIgnoredNumbersFlagMapper.toBlacklistContactItem(item) }
                .flatMapCompletable { contactItem: BlacklistContactItem ->
                    blacklistContactPhoneWithActivityIntervalsRepository.getAllAssociatedWithBlacklistContact(contactItem)
                            .flatMapCompletable { phonesAndIntervals: List<BlacklistContactPhoneWithActivityIntervals> ->
                                interactionModeRepository.putSelectedMode(InteractionMode.EDIT)
                                        .andThen(blacklistContactRepository.putSelected(contactItem))
                                        .andThen(blacklistContactPhoneWithActivityIntervalsRepository.putSelectedList(phonesAndIntervals))
                            }
                            .andThen(getWhitelistPhonesByBlacklistContactItem(contactItem)
                                    .flatMapCompletable { whitelistContactPhoneRepository.putSelectedList(it) })
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getWhitelistPhonesByBlacklistContactItem(contactItem: BlacklistContactItem) : Single<List<WhitelistContactPhone>> {
        return Single.fromCallable { blacklistContactItemMapper.toWhitelistContactItem(contactItem) }
                .flatMap { whitelistContactPhoneRepository.getAllAssociatedWithContact(it) }
    }
}