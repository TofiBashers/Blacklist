package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IInteractionModeRepository
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 21.01.2018.
 */
@Singleton
class SelectCreateModeUseCase
@Inject
constructor(
        private val interactionModeRepository: IInteractionModeRepository
) : ISelectCreateModeUseCase {

    override fun build(): Completable {
        return interactionModeRepository.putSelectedMode(InteractionMode.CREATE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}