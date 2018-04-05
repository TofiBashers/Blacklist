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
        private val blacklistItemFactory: BlacklistItemFactory,
        private val interactionModeWithBlacklistItemAndValidStateFactory: InteractionModeWithBlacklistItemAndValidStateFactory
) : IGetInteractionModeWithSelectedBlackListItemUseCase {

    override fun build(): Single<InteractionModeWithBlacklistItemAndValidState> {
        return interactionModeRepository.getSelectedMode()
                .switchIfEmpty(Single.error(RuntimeException("InteractionMode not selected!")))
                .flatMap { mode: InteractionMode ->
                    Single.defer {
                        if(mode == InteractionMode.CREATE) {
                            Single.fromCallable { blacklistItemFactory.create() }
                                    .map {item: BlacklistItem ->
                                        interactionModeWithBlacklistItemAndValidStateFactory.create(mode, item, false)
                                    }
                        }
                        else{
                            blacklistElementRepository.getSelectedBlackListItem()
                                    .switchIfEmpty(Single.error(RuntimeException("Item not selected when InteractionMode.EDIT")))
                                    .map {item: BlacklistItem ->
                                        interactionModeWithBlacklistItemAndValidStateFactory.create(mode, item, true)
                                    }
                        }
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    }
}