package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbBlacklistContactPhoneItemMapper
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactPhoneMapper
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 21.04.2018.
 */
@Singleton
class BlacklistContactPhoneRepository
@Inject
constructor(private val databaseSource: IDatabaseSource,
            private val blacklistContactItemMapper: BlacklistContactItemMapper,
            private val blacklistContactPhoneMapper: BlacklistContactPhoneMapper,
            private val dbBlacklistContactPhoneItemMapper: DbBlacklistContactPhoneItemMapper) : IBlacklistContactPhoneRepository{

    override fun getAllAssociatedWithBlacklistContact(item: BlacklistContactItem): Single<List<BlacklistContactPhoneNumberItem>> {
        return Single.fromCallable { blacklistContactItemMapper.toDbBlacklistContactItem(item) }
                .flatMap { databaseSource.getBlacklistContactPhonesAssociatedWithBlacklistContactItem(it) }
                .map { dbBlacklistContactPhoneItemMapper.toBlacklistContactPhoneNumberItemList(it) }
    }

    override fun removeAssociatedWithBlacklistContact(vararg items: BlacklistContactPhoneNumberItem,
                                             blacklistContact: BlacklistContactItem): Completable {
        return Single.fromCallable {
            blacklistContactPhoneMapper.toDbBlacklistContactPhoneList(items.toList(), blacklistContact.dbId)
        }
                .flatMapCompletable { databaseSource.deleteBlacklistContactPhoneItems(it) }
    }
}