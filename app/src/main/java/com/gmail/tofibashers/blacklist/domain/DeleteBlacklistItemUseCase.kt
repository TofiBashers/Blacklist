package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IBlacklistItemRepository
import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 22.01.2018.
 */
@Singleton
class DeleteBlacklistItemUseCase
@Inject
constructor(
        private val blacklistElementRepository: IBlacklistItemRepository
) : IDeleteBlacklistItemUseCase {

    override fun build(blacklistElement: BlacklistItem): Completable {
        return blacklistElementRepository.deleteBlacklistItem(blacklistElement)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}