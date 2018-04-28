package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IBlacklistItemRepository
import com.gmail.tofibashers.blacklist.data.repo.IInteractionModeRepository
import com.gmail.tofibashers.blacklist.entity.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.01.2018.
 */
@Singleton
class GetInteractionModeWithSelectedBlackListItemUseCase
@Inject
constructor(
        private val blacklistElementRepository: IBlacklistItemRepository,
        private val interactionModeRepository: IInteractionModeRepository,
        private val blacklistPhoneNumberItemFactory: BlacklistPhoneNumberItemFactory,
        private val interactionModeWithBlacklistItemAndValidStateFactory: InteractionModeWithBlacklistPhoneNumberItemAndValidStateFactory
) : IGetInteractionModeWithSelectedBlackListItemUseCase {

    override fun build(): Single<InteractionModeWithBlacklistPhoneNumberItemAndValidState> {
        return interactionModeRepository.getSelectedMode()
                .switchIfEmpty(Single.error(RuntimeException("InteractionMode not selected!")))
                .flatMap { mode: InteractionMode ->
                    Single.defer {
                        if(mode == InteractionMode.CREATE) {
                            Single.fromCallable { blacklistPhoneNumberItemFactory.create() }
                                    .map { phoneNumberItem: BlacklistPhoneNumberItem ->
                                        interactionModeWithBlacklistItemAndValidStateFactory.create(mode, phoneNumberItem, false)
                                    }
                        }
                        else{
                            blacklistElementRepository.getSelectedBlacklistPhoneNumberItem()
                                    .switchIfEmpty(Single.error(RuntimeException("Item not selected when InteractionMode.EDIT")))
                                    .map { phoneNumberItem: BlacklistPhoneNumberItem ->
                                        interactionModeWithBlacklistItemAndValidStateFactory.create(mode, phoneNumberItem, true)
                                    }
                        }
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    }
}