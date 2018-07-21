package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.BlacklistContactItemRepository
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItemWithNonIgnoredNumbersFlag
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemWithNonIgnoredNumbersFlagMapper
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 21.04.2018.
 */
@Singleton
class DeleteBlacklistContactItemUseCase
@Inject
constructor(
        private val blacklistContactItemRepository: BlacklistContactItemRepository,
        private val itemWithNonIgnoredNumbersFlagMapper: BlacklistContactItemWithNonIgnoredNumbersFlagMapper
) : IDeleteBlacklistContactItemUseCase {

    override fun build(itemWithNumbersFlag: BlacklistContactItemWithNonIgnoredNumbersFlag): Completable {
        return Single.fromCallable { itemWithNonIgnoredNumbersFlagMapper.toBlacklistContactItem(itemWithNumbersFlag) }
                .flatMapCompletable { blacklistContactItemRepository.delete(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}