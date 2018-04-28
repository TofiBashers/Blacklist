package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IBlacklistItemRepository
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 22.01.2018.
 */
@Singleton
class DeletePhoneNumberItemUseCase
@Inject
constructor(
        private val blacklistElementRepository: IBlacklistItemRepository
) : IDeletePhoneNumberItemUseCase {

    override fun build(blacklistPhoneNumberElement: BlacklistPhoneNumberItem): Completable {
        return blacklistElementRepository.deleteBlacklistItem(blacklistPhoneNumberElement)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}