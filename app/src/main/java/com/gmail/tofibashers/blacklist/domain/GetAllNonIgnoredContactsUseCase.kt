package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IWhitelistContactItemRepository
import com.gmail.tofibashers.blacklist.data.repo.IWhitelistContactPhoneRepository
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItem
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItemWithHasPhones
import com.gmail.tofibashers.blacklist.entity.mapper.WhitelistContactItemMapper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 14.04.2018.
 */
@Singleton
class GetAllNonIgnoredContactsUseCase
@Inject
constructor(
        private val contactItemRepository: IWhitelistContactItemRepository,
        private val contactPhoneRepository: IWhitelistContactPhoneRepository,
        private val contactItemMapper: WhitelistContactItemMapper
) : IGetAllNonIgnoredContactsWithChangesUseCase {

    override fun build(): Observable<List<WhitelistContactItemWithHasPhones>> {
        return contactItemRepository.getAllSortedByNameAscWithChanges()
                .concatMap { items: List<WhitelistContactItem> ->
                    return@concatMap Observable.fromIterable(items)
                            .concatMap { item: WhitelistContactItem ->
                                contactPhoneRepository.getCountOfAssociatedWithContact(item)
                                        .map { count: Int ->
                                            contactItemMapper.toWhitelistContactItemWithHashPhones(item, hasPhonesFlag(count))
                                        }
                                        .toObservable()
                            }
                            .toList()
                            .toFlowable()
                }
                .subscribeOn(Schedulers.io())
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
    }

    private fun hasPhonesFlag(count: Int) : Boolean = count > 0
}