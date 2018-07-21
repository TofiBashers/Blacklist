package com.gmail.tofibashers.blacklist.data.datasource

import com.gmail.tofibashers.blacklist.data.db.BlacklistDatabase
import com.gmail.tofibashers.blacklist.data.db.dao.*
import com.gmail.tofibashers.blacklist.data.db.entity.*
import io.reactivex.*
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 22.01.2018.
 */

@Singleton
class DatabaseSource
@Inject
constructor(
        private val database: BlacklistDatabase,
        private val activityIntervalDao: IActivityIntervalDao,
        private val blacklistPhoneNumberItemDao: IBlacklistPhoneNumberItemDao,
        private val joinBlacklistPhoneNumberItemActivityIntervalDao: IJoinBlacklistPhoneNumberItemActivityIntervalDao,
        private val blacklistContactItemDao: IBlacklistContactItemDao,
        private val blacklistContactPhoneItemDao: IBlacklistContactPhoneItemDao,
        private val joinBlacklistContactPhoneItemActivityIntervalDao: IJoinBlacklistContactPhoneItemActivityIntervalDao,
        private val dbJoinBlacklistPhoneNumberItemActivityIntervalFactory: DbJoinBlacklistPhoneNumberItemActivityIntervalFactory,
        private val dbBlacklistPhoneNumberItemWithActivityIntervalsFactory: DbBlacklistPhoneNumberItemWithActivityIntervalsFactory,
        private val dbJoinBlacklistContactPhoneItemActivityIntervalFactory: DbJoinBlacklistContactPhoneItemActivityIntervalFactory,
        private val dbBlacklistContactPhoneWithActivityIntervalsFactory: DbBlacklistContactPhoneWithActivityIntervalsFactory,
        private val dbBlacklistContactItemWithPhonesAndIntervalsFactory: DbBlacklistContactItemWithPhonesAndIntervalsFactory
) : IDatabaseSource {

    override fun getAllBlacklistPhoneNumberItemsWithIntervalsWithChanges(): Flowable<List<DbBlacklistPhoneNumberItemWithActivityIntervals>> {
        return joinBlacklistPhoneNumberItemActivityIntervalDao.getAllBlacklistPhoneNumberItemsWithActivityIntervalIdsWithChanges()
                .switchMap { itemsWithJoins : List<DbBlacklistPhoneNumberItemWithJoinsBlacklistPhoneNumberItemActivityInterval> ->
                    return@switchMap Flowable.fromIterable(itemsWithJoins)
                            .map { item : DbBlacklistPhoneNumberItemWithJoinsBlacklistPhoneNumberItemActivityInterval ->
                                getFlowableDbActivityIntervalsByItemWithChanges(item)
                            }
                            .toList()
                            .flatMapPublisher { resultFlowables: List<Flowable<DbBlacklistPhoneNumberItemWithActivityIntervals>> ->
                                return@flatMapPublisher if(resultFlowables.isEmpty()) Flowable.just(emptyList())
                                else Flowable.combineLatest(resultFlowables.toMutableList(),
                                        { resultItems: Array<Any> ->
                                            resultItems.map {
                                                resultItemObj: Any -> resultItemObj as DbBlacklistPhoneNumberItemWithActivityIntervals
                                            }
                                        })
                            }
                }
    }

    override fun getBlacklistPhoneNumberItemByNumber(number: String): Maybe<DbBlacklistPhoneNumberItem> = blacklistPhoneNumberItemDao.getByNumber(number)

    override fun putBlacklistPhoneNumberItemWithActivityIntervals(itemWithIntervals: DbBlacklistPhoneNumberItemWithActivityIntervals): Completable {
        return putBlacklistPhoneNumberItemWithGetId(itemWithIntervals.dbBlacklistPhoneNumberItem)
                .flatMapCompletable { id:Long ->
                    putActivityIntervalsWithAddToBlacklistItem(id, itemWithIntervals.dbActivityIntervals)
                }
                .compose(inTransactionCompletable())
    }

    override fun getAllBlacklistPhoneNumberItemsWithChanges(): Flowable<List<DbBlacklistPhoneNumberItem>> =
            blacklistPhoneNumberItemDao.getAllWithChanges()

    override fun deleteBlacklistPhoneNumberItem(dbBlacklistPhoneNumberItem: DbBlacklistPhoneNumberItem) : Completable {
        return deleteAllBlacklistPhoneNumberItemActivityIntervalsAndClearOrphans(dbBlacklistPhoneNumberItem.id)
                .andThen(blacklistPhoneNumberItemDao.deleteBlacklistPhoneNumberItemsByIdsAsCompletable(dbBlacklistPhoneNumberItem))
                .compose(inTransactionCompletable())
    }

    override fun inTransactionCompletable(): CompletableTransformer {
        return CompletableTransformer { upstream: Completable ->
            return@CompletableTransformer Completable.fromAction { database.beginTransaction() }
                    .andThen(upstream)
                    .doOnComplete { database.setTransactionSuccessful() }
                    .doFinally { database.endTransaction() }
        }
    }

    override fun getAllBlacklistContactItems(): Single<List<DbBlacklistContactItem>> = blacklistContactItemDao.getAll()

    override fun getAllBlacklistContactItemsSortedByNameAscWithChanges(): Flowable<List<DbBlacklistContactItem>> =
            blacklistContactItemDao.getAllSortedByNameAscWithChanges()

    override fun getBlacklistContactItemWithPhonesAndActivityIntervalsWithChanges(): Flowable<List<DbBlacklistContactItemWithPhonesAndIntervals>> {
        return joinBlacklistContactPhoneItemActivityIntervalDao.getAllBlacklistContactItemsWithPhonesAndActivityIntervalIdsWithChanges()
                .switchMap { itemsWithPhonesAndJoins : List<DbBlacklistContactWithPhonesWithJoinBlacklistContactPhoneItemActivityInterval> ->
                    return@switchMap Flowable.fromIterable(itemsWithPhonesAndJoins)
                            .map { item : DbBlacklistContactWithPhonesWithJoinBlacklistContactPhoneItemActivityInterval ->
                                getFlowableDbPhonesWithActivityIntervalsByItemWithChanges(item)
                            }
                            .toList()
                            .flatMapPublisher { resultFlowables: List<Flowable<DbBlacklistContactItemWithPhonesAndIntervals>> ->
                                return@flatMapPublisher if(resultFlowables.isEmpty()) Flowable.just(emptyList())
                                else Flowable.combineLatest(resultFlowables.toMutableList(),
                                        { resultItems: Array<Any> ->
                                            resultItems.map {
                                                resultItemObj: Any -> resultItemObj as DbBlacklistContactItemWithPhonesAndIntervals
                                            }
                                        })
                            }
                }
    }

    override fun getBlacklistContactItemById(id: Long?): Maybe<DbBlacklistContactItem> =
            blacklistContactItemDao.getById(id)

    override fun deleteBlacklistContactItem(blacklistContactItem: DbBlacklistContactItem): Completable {
        return deleteBlacklistContactPhoneItemsByContactIdsWithAssociationsAndClearOrphans(blacklistContactItem.id)
                .andThen(blacklistContactItemDao.deleteByIdsAsCompletable(blacklistContactItem))
                .compose(inTransactionCompletable())
    }

    override fun deleteBlacklistContactItems(items: List<DbBlacklistContactItem>): Completable {
        return Single.fromCallable { items.map { it.id } }
                .flatMapCompletable { deleteBlacklistContactPhoneItemsByContactIdsWithAssociationsAndClearOrphans(*it.toTypedArray()) }
                .andThen(blacklistContactItemDao.deleteByIdsAsCompletable(*items.toTypedArray()))
                .compose(inTransactionCompletable())
    }

    override fun deleteBlacklistContactItemsThatNonAssociatedWithAnyPhones(): Completable =
            blacklistContactItemDao.deleteBlacklistContactItemsThatNonAssociatedWithAnyPhonesdAsCompletable()

    override fun putBlacklistContactItems(items: List<DbBlacklistContactItem>): Completable {
        return Observable.fromIterable(items)
                .flatMapCompletable { item: DbBlacklistContactItem ->
                    putBlacklistContactItem(item)
                }
                .compose(inTransactionCompletable())
    }

    override fun putBlacklistContactItemWithPhonesAndActivityIntervals(items: DbBlacklistContactItemWithPhonesAndIntervals): Completable {
        return putBlacklistContactItemWithGetId(items.contactItem)
                .flatMapCompletable { putId: Long ->
                    Observable.fromIterable(items.phonesWithIntervals)
                            .flatMapCompletable {
                                putBlacklistContactPhoneItemWithActivityIntervalsAndAddToBlacklistContact(putId, it)
                            }
                }
                .compose(inTransactionCompletable())
    }

    override fun getAllBlacklistContactPhones(): Single<List<DbBlacklistContactPhoneItem>> =
            blacklistContactPhoneItemDao.getAll()

    override fun getBlacklistContactPhonesAssociatedWithBlacklistContactItem(item: DbBlacklistContactItem): Single<List<DbBlacklistContactPhoneItem>> {
        return getBlacklistContactPhonesAssociatedWithBlacklistContactItemId(item.id)
    }

    override fun getBlacklistContactPhonesWithIntervalsAssociatedWithBlacklistContactItem(item: DbBlacklistContactItem): Single<List<DbBlacklistContactPhoneWithActivityIntervals>> {
        return joinBlacklistContactPhoneItemActivityIntervalDao.getBlacklistContactPhonesAndActivityIntervalIdsByBlacklistContactId(item.id)
                .flattenAsObservable { it }
                .flatMapSingle { getBlacklistContactPhoneWithIntervalsForBlacklistPhoneWithJoin(it) }
                .toList()
    }

    override fun getBlacklistContactPhonesByBlacklistContactItemByDeviceDbIdAndLookupKey(item: DbBlacklistContactItem): Single<List<DbBlacklistContactPhoneItem>> =
        blacklistContactPhoneItemDao.getByBlacklistContactDeviceDbIdAndLookupKey(item.deviceDbId, item.deviceLookupKey)

    override fun deleteBlacklistContactPhoneItems(items: List<DbBlacklistContactPhoneItem>): Completable =
            deleteBlacklistContactPhoneItemsWithAssociationsAndClearOrphans(*items.toTypedArray())
                    .compose(inTransactionCompletable())

    private fun putBlacklistPhoneNumberItemWithGetId(dbBlacklistPhoneNumberItem: DbBlacklistPhoneNumberItem): Single<Long> {
        return blacklistPhoneNumberItemDao.updateBlacklistPhoneNumberItemWithGetUpdateCountAsSingle(dbBlacklistPhoneNumberItem)
                .flatMap {
                    if(it == 1) {
                        blacklistPhoneNumberItemDao.getByIdOrException(dbBlacklistPhoneNumberItem.id)
                                .map { it.id }
                    }
                    else blacklistPhoneNumberItemDao.insertBlacklistPhoneNumberItemAsSingle(dbBlacklistPhoneNumberItem)
                }
    }

    override fun getActivityIntervalsAssociatedWithBlacklistPhoneNumberItem(blacklistPhoneNumberItem: DbBlacklistPhoneNumberItem): Single<List<DbActivityInterval>> =
            joinBlacklistPhoneNumberItemActivityIntervalDao.getActvitiyIntervalsForBlacklistPhoneNumberItemId(blacklistPhoneNumberItem.id)

    private fun putActivityIntervalsWithAddToBlacklistItem(blacklistItemId: Long,
                                                           dbActivityIntervals: List<DbActivityInterval>): Completable {
        return deleteAllBlacklistPhoneNumberItemActivityIntervalsAndClearOrphans(blacklistItemId)
                .andThen(Observable.fromIterable(dbActivityIntervals))
                .flatMapCompletable { insertActivityIntervalWithAddToBlacklistPhoneNumberItem(blacklistItemId, it) }
    }

    private fun putActivityIntervalsWithAddToBlacklistPhoneItem(blacklistPhoneItemId: Long,
                                                                dbActivityIntervals: List<DbActivityInterval>): Completable {
        return deleteAllBlacklistContactPhonesActivityIntervalsAndClearOrphans(blacklistPhoneItemId)
                .andThen(Observable.fromIterable(dbActivityIntervals))
                .flatMapCompletable { insertActivityIntervalWithAddToBlacklistContactPhoneItem(blacklistPhoneItemId, it) }
    }

    private fun deleteAllBlacklistPhoneNumberItemActivityIntervalsAndClearOrphans(blacklistItemId: Long?) : Completable {
        return joinBlacklistPhoneNumberItemActivityIntervalDao.removeAllJoinsWithBlacklistPhoneNumberItemIdAsCompletable(blacklistItemId)
                .andThen(joinBlacklistPhoneNumberItemActivityIntervalDao.removeActivityIntervalsNonAssociatedWithBlacklistPhoneNumberItemsAndBlacklistContactPhoneItemsAsCompletable())
    }

    private fun deleteAllBlacklistContactPhonesActivityIntervalsAndClearOrphans(vararg blacklistContactPhoneItemIds: Long?) : Completable {
        return joinBlacklistContactPhoneItemActivityIntervalDao.removeAllJoinsWithBlacklistContactPhoneItemIdsAsCompletable(*blacklistContactPhoneItemIds)
                .andThen(joinBlacklistPhoneNumberItemActivityIntervalDao.removeActivityIntervalsNonAssociatedWithBlacklistPhoneNumberItemsAndBlacklistContactPhoneItemsAsCompletable())
    }

    private fun insertActivityIntervalWithAddToBlacklistPhoneNumberItem(blacklistItemId: Long,
                                                                        dbActivityInterval: DbActivityInterval): Completable {
        return activityIntervalDao.getActivityIntervalByTimesAndWeekday(dbActivityInterval.dayOfWeek,
                dbActivityInterval.beginTime,
                dbActivityInterval.endTime)
                .map ({ it.id })
                .switchIfEmpty(activityIntervalDao.insertActivityIntervalWithGetIdAsSingle(dbActivityInterval))
                .map({ dbJoinBlacklistPhoneNumberItemActivityIntervalFactory.create(blacklistItemId, it) })
                .flatMapCompletable { joinBlacklistPhoneNumberItemActivityIntervalDao.insertWithIgnoreConflictsAsCompletable(it) }

    }

    private fun insertActivityIntervalWithAddToBlacklistContactPhoneItem(blacklistContactPhoneItemId: Long,
                                                                         dbActivityInterval: DbActivityInterval): Completable {
        return activityIntervalDao.getActivityIntervalByTimesAndWeekday(dbActivityInterval.dayOfWeek,
                dbActivityInterval.beginTime,
                dbActivityInterval.endTime)
                .map ({ it.id })
                .switchIfEmpty(activityIntervalDao.insertActivityIntervalWithGetIdAsSingle(dbActivityInterval))
                .map({ dbJoinBlacklistContactPhoneItemActivityIntervalFactory.create(blacklistContactPhoneItemId, it) })
                .flatMapCompletable { joinBlacklistContactPhoneItemActivityIntervalDao.insertWithIgnoreConflictsAsCompletable(it) }

    }

    private fun getFlowableDbActivityIntervalsByItemWithChanges(
            item : DbBlacklistPhoneNumberItemWithJoinsBlacklistPhoneNumberItemActivityInterval) : Flowable<DbBlacklistPhoneNumberItemWithActivityIntervals>{
        return Flowable.fromIterable(item.listOfJoins)
                .map {joinItem: DbJoinBlacklistPhoneNumberItemActivityInterval ->
                    activityIntervalDao.getActivityIntervalByIdWithChanges(joinItem.activityIntervalId)
                }
                .toList()
                .flatMapPublisher {intervalsFlowable: List<Flowable<DbActivityInterval>> ->
                    Flowable.combineLatest(intervalsFlowable.toMutableList(),
                            {intervalChanges: Array<Any> ->
                                dbBlacklistPhoneNumberItemWithActivityIntervalsFactory.create(
                                        item.blacklistPhoneNumberItem,
                                        intervalChanges.toList()
                                                .map {intervalObj: Any -> intervalObj as DbActivityInterval })
                            })
                }
    }

    private fun getFlowableDbPhonesWithActivityIntervalsByItemWithChanges(
            item: DbBlacklistContactWithPhonesWithJoinBlacklistContactPhoneItemActivityInterval): Flowable<DbBlacklistContactItemWithPhonesAndIntervals>{
        return Flowable.fromIterable(item.listOfPhonesWithJoins)
                .map {phone: DbBlacklistContactPhoneWithJoinBlacklistContactPhoneItemActivityInterval ->
                    getFlowableDbActivityIntervalsByBlacklistPhoneItemWithChanges(phone)
                }
                .toList()
                .flatMapPublisher {intervalsOfPhonesFlowable: List<Flowable<DbBlacklistContactPhoneWithActivityIntervals>> ->
                    Flowable.combineLatest(intervalsOfPhonesFlowable.toMutableList(),
                            {phoneWithChangedIntervals: Array<Any> ->
                                dbBlacklistContactItemWithPhonesAndIntervalsFactory.create(
                                        item.blacklistContactItem,
                                        phoneWithChangedIntervals.toList()
                                                .map { phoneWithIntervalObj: Any ->
                                                    phoneWithIntervalObj as DbBlacklistContactPhoneWithActivityIntervals
                                                })
                            })
                }
    }

    private fun getFlowableDbActivityIntervalsByBlacklistPhoneItemWithChanges(
            item: DbBlacklistContactPhoneWithJoinBlacklistContactPhoneItemActivityInterval): Flowable<DbBlacklistContactPhoneWithActivityIntervals>{
        return Flowable.fromIterable(item.listOfJoins)
                .map {phoneWithInterval: DbJoinBlacklistContactPhoneItemActivityInterval ->
                    activityIntervalDao.getActivityIntervalByIdWithChanges(phoneWithInterval.activityIntervalId)
                }
                .toList()
                .flatMapPublisher {intervalsFlowable: List<Flowable<DbActivityInterval>> ->
                    Flowable.combineLatest(intervalsFlowable.toMutableList(),
                            {intervalChanges: Array<Any> ->
                                dbBlacklistContactPhoneWithActivityIntervalsFactory.create(
                                        item.blacklistContactPhoneItem,
                                        intervalChanges.toList()
                                                .map {intervalObj: Any -> intervalObj as DbActivityInterval })
                            })
                }
    }

    private fun putBlacklistContactItemWithGetId(dbBlacklistContactItem: DbBlacklistContactItem): Single<Long> {
        return blacklistContactItemDao.updateByIdsWithGetUpdatedCountAsSingle(dbBlacklistContactItem)
                .flatMap {
                    if(it == 1) {
                        blacklistContactItemDao.getByIdOrException(dbBlacklistContactItem.id)
                                .map { it.id }
                    }
                    else blacklistContactItemDao.insertWithGetInsertedIdAsSingle(dbBlacklistContactItem)
                }
    }

    private fun putBlacklistContactItem(dbBlacklistContactItem: DbBlacklistContactItem): Completable {
        return blacklistContactItemDao.updateByIdsWithGetUpdatedCountAsSingle(dbBlacklistContactItem)
                .flatMapCompletable {
                    if(it == 1) Completable.complete()
                    else blacklistContactItemDao.insertAsCompletable(dbBlacklistContactItem)
                }
    }

    private fun putBlacklistContactPhoneItemWithActivityIntervalsAndAddToBlacklistContact(contactId: Long,
                                                                                          phoneWithIntervals: DbBlacklistContactPhoneWithActivityIntervals): Completable {
        return Single.fromCallable {
            val phone = phoneWithIntervals.dbBlacklistContactPhone
            phone.blacklistContactId = contactId
            return@fromCallable phone
        }
                .flatMap { phoneWithUpdContactId: DbBlacklistContactPhoneItem ->
                    putBlacklistContactPhoneItemWithGetId(phoneWithUpdContactId)
                }
                .flatMapCompletable { putId: Long ->
                    putActivityIntervalsWithAddToBlacklistPhoneItem(putId, phoneWithIntervals.activityIntervals)
                }
    }

    private fun putBlacklistContactPhoneItemWithGetId(phoneItem: DbBlacklistContactPhoneItem): Single<Long> {
        return blacklistContactPhoneItemDao.updateByIdWithGetUpdatedCountAsSingle(phoneItem)
                .flatMap {
                    if(it == 1) {
                        blacklistContactPhoneItemDao.getByIdOrException(phoneItem.id)
                                .map { it.id }
                    }
                    else blacklistContactPhoneItemDao.insertWithGetInsertedIdAsSingle(phoneItem)
                }
    }

    private fun getBlacklistContactPhonesAssociatedWithBlacklistContactItemId(contactId: Long?): Single<List<DbBlacklistContactPhoneItem>> {
        return Single.defer {
            return@defer if(contactId != null) blacklistContactPhoneItemDao.getByBlacklistContactId(contactId)
            else Single.just(emptyList())
        }
    }

    private fun getBlacklistContactPhoneWithIntervalsForBlacklistPhoneWithJoin(
            item: DbBlacklistContactPhoneWithJoinBlacklistContactPhoneItemActivityInterval): Single<DbBlacklistContactPhoneWithActivityIntervals> {
        return Observable.fromIterable(item.listOfJoins)
                .flatMapSingle { join: DbJoinBlacklistContactPhoneItemActivityInterval ->
                    activityIntervalDao.getActivityIntervalByIdOrException(join.activityIntervalId)
                }
                .toList()
                .map { dbBlacklistContactPhoneWithActivityIntervalsFactory.create(item.blacklistContactPhoneItem, it) }
    }

    private fun deleteBlacklistContactPhoneItemsByContactIdsWithAssociationsAndClearOrphans(vararg ids: Long?): Completable {
        return Observable.fromIterable(ids.toList())
                .flatMapCompletable {
                    getBlacklistContactPhonesAssociatedWithBlacklistContactItemId(it)
                            .flatMapCompletable {
                                deleteBlacklistContactPhoneItemsWithAssociationsAndClearOrphans(*it.toTypedArray())
                            }
                }
    }

    private fun deleteBlacklistContactPhoneItemsWithAssociationsAndClearOrphans(vararg items: DbBlacklistContactPhoneItem): Completable {
        return Single.fromCallable { items.map { it.id } }
                .flatMapCompletable { ids: List<Long?> ->
                    deleteAllBlacklistContactPhonesActivityIntervalsAndClearOrphans(*ids.toTypedArray())
                }
                .andThen(blacklistContactPhoneItemDao.deleteByIdsAsCompletable(*items))
    }
}