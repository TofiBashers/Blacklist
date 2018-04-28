package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IWhitelistContactItemRepository
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItemWithHasPhones
import com.gmail.tofibashers.blacklist.entity.mapper.WhitelistContactItemWithHasPhonesMapper
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 14.04.2018.
 */
@Singleton
class SelectContactItemUseCase
@Inject
constructor(private val contactItemRepository: IWhitelistContactItemRepository,
            private val contactItemWithHasPhonesMapper: WhitelistContactItemWithHasPhonesMapper): ISelectContactItemUseCase {

    override fun build(whitelistContactItem: WhitelistContactItemWithHasPhones): Completable {
        return Single.just(whitelistContactItem)
                .map { contactItemWithHasPhonesMapper.toWhitelistContactItem(it) }
                .flatMapCompletable { contactItemRepository.putSelected(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}