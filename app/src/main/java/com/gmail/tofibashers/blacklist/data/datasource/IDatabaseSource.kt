package com.gmail.tofibashers.blacklist.data.datasource

import com.gmail.tofibashers.blacklist.data.db.entity.*
import io.reactivex.*


/**
 * Created by TofiBashers on 22.01.2018.
 */
interface IDatabaseSource {

    /**
     * Get blacklistItems firstly and after all changes. Not modify any Backpressure policy.
     * Result [Flowable] doesn't modify backpressure strategies and schedulers, never calls onComplete().
     */
    fun getAllBlackListItemsWithChanges(): Flowable<List<DbBlacklistItem>>

    /**
     * Get blacklistItems firstly and after all changes. Not modify any Backpressure policy.
     * Result [Flowable] doesn't modify backpressure strategies and schedulers, never calls onComplete().
     */
    fun getAllBlacklistItemsWithIntervalsWithChanges(): Flowable<List<DbBlacklistItemWithActivityIntervals>>

    /**
     * @return [Maybe] with phoneNumberItem, if exists, otherwise empty. Result [Maybe] doesn't specify schedulers.
     */
    fun getBlacklistItemByNumber(number: String): Maybe<DbBlacklistItem>

    /**
     * Delete blacklistItems and clear orphaned [DbActivityInterval]'s formed in operation.
     * Operations execute in transaction.
     * Result [Completable] doesn't specify schedulers.
     */
    fun deleteBlackListItem(dbBlacklistItem: DbBlacklistItem): Completable

    /**
     * Insert or update [DbBlacklistItem], then insert activity intervals if it non persist,
     * and update association with [DbBlacklistItem].
     * if not associated. Comparsion with existent performed by concatenation of [DbActivityInterval.beginTime],
     * [DbActivityInterval.endTime] and [DbActivityInterval.dayOfWeek].
     * Also removes orphaned [DbActivityInterval]'s formed in this operation.
     * Operations execute in transaction.
     * Result [Completable] doesn't specify schedulers.
     * @param itemWithIntervals - contains [DbBlacklistItem] and list of [DbActivityInterval]'s
     */
    fun putBlacklistItemWithActivityIntervals(itemWithIntervals: DbBlacklistItemWithActivityIntervals): Completable

    /**
     * @return [Maybe] with list of phoneNumberItems, if exists, otherwise empty. Result [Maybe] doesn't specify schedulers.
     */
    fun getActivityIntervalsAssociatedWithBlacklistItem(blacklistItem: DbBlacklistItem): Maybe<List<DbActivityInterval>>

    /**
     * Get all [DbBlacklistContactItem]'s
     * Result [Single] doesn't modify any schedulers.
     */
    fun getAllBlacklistContactItems(): Single<List<DbBlacklistContactItem>>

    /**
     * @return list of [DbBlacklistContactItem] firstly and after all changes, sorted by [DbBlacklistContactItem.name] asc.
     * Result [Flowable] doesn't modify backpressure strategies and schedulers, never calls onComplete().
     */
    fun getAllBlacklistContactItemsSortedByNameAscWithChanges(): Flowable<List<DbBlacklistContactItem>>

    /**
     * Get blacklistContactItems firstly and after all changes. Not modify any Backpressure policy.
     * Result [Flowable] doesn't modify backpressure strategies and schedulers, never calls onComplete().
     */
    fun getBlacklistContactItemWithPhonesAndActivityIntervalsWithChanges(): Flowable<List<DbBlacklistContactItemWithPhonesAndIntervals>>

    /**
     * @return [Maybe] with blacklistContactItem with [id], if exists, otherwise empty. Result [Maybe] doesn't specify schedulers.
     */
    fun getBlacklistContactItemById(id: Long?): Maybe<DbBlacklistContactItem>

    /**
     * Delete [DbBlacklistContactItem] and clear orphaned [DbBlacklistContactPhoneItem]'s and [DbActivityInterval]'s formed in operation.
     * Operations execute in transaction.
     * @return [Completable] when operation completes, regardless of really entity delete.
     * Result [Completable] doesn't specify schedulers.
     */
    fun deleteBlacklistContactItem(blacklistContactItem: DbBlacklistContactItem): Completable

    /**
     * Delete [DbBlacklistContactItem]'s and clear orphaned [DbBlacklistContactPhoneItem]'s and [DbActivityInterval]'s formed in operation.
     * Operations execute in transaction.
     * @return [Completable] when operation completes, regardless of count of really deleted entities.
     * Result [Completable] doesn't specify schedulers.
     */
    fun deleteBlacklistContactItems(items: List<DbBlacklistContactItem>): Completable

    /**
     * Delete [DbBlacklistContactItem]'s that not have at least one associated [DbBlacklistContactPhoneItem].
     * Operations execute in transaction.
     * @return [Completable] when operation completes, regardless of count of really deleted entities.
     * Result [Completable] doesn't specify schedulers.
     */
    fun deleteBlacklistContactItemsThatNonAssociatedWithAnyPhones(): Completable

    /**
     * Insert or update [DbBlacklistContactItem]'s, depends of their ids is exists. If exists
     * - updates entity, else - inserts.
     * Operations execute in transaction.
     * @param items list of items to update/insert.
     * @return [Completable] when operation completes, regardless of count of really deleted entities.
     * Result [Completable] doesn't specify schedulers.
     */
    fun putBlacklistContactItems(items: List<DbBlacklistContactItem>): Completable

    /**
     * Insert or update [DbBlacklistContactItem], then insert their phones and activity intervals if it non persist,
     * and update association with [DbBlacklistContactPhoneItem] and [DbActivityInterval].
     * if [DbActivityInterval] not associated, comparsion with existent performed by concatenation of [DbActivityInterval.beginTime],
     * [DbActivityInterval.endTime] and [DbActivityInterval.dayOfWeek].
     * Also removes orphaned [DbActivityInterval]'s, formed in this operation.
     * Operations execute in transaction.
     * Result [Completable] doesn't specify schedulers.
     * @param items - contains [DbBlacklistItem] and list of [DbBlacklistContactPhoneItem] and [DbActivityInterval]'s
     */
    fun putBlacklistContactItemWithPhonesAndActivityIntervals(items: DbBlacklistContactItemWithPhonesAndIntervals): Completable

    /**
     * Get all blacklistContactPhoneItems
     * Result [Single] doesn't modify any schedulers, never calls onComplete().
     */
    fun getAllBlacklistContactPhones(): Single<List<DbBlacklistContactPhoneItem>>

    /**
     * Get all blacklistContactPhoneItems, associated with [item]
     * Result [Single] doesn't modify any schedulers, never calls onComplete().
     */
    fun getBlacklistContactPhonesAssociatedWithBlacklistContactItem(item: DbBlacklistContactItem): Single<List<DbBlacklistContactPhoneItem>>

    /**
     * Get all blacklistContactPhones and their intervals, associated with [item]
     * Result [Single] doesn't modify any schedulers, never calls onComplete().
     */
    fun getBlacklistContactPhonesWithIntervalsAssociatedWithBlacklistContactItem(item: DbBlacklistContactItem): Single<List<DbBlacklistContactPhoneWithActivityIntervals>>

    /**
     * Get all blacklistContactPhoneItems, associated with [DbBlacklistContactItem] by its
     * [DbBlacklistContactItem.deviceDbId] and [DbBlacklistContactItem.deviceLookupKey]
     * Result [Single] doesn't modify any schedulers, never calls onComplete().
     */
    fun getBlacklistContactPhonesByBlacklistContactItemByDeviceDbIdAndLookupKey(item: DbBlacklistContactItem): Single<List<DbBlacklistContactPhoneItem>>

    /**
     * Delete [DbBlacklistContactPhoneItem] and clear orphaned [DbActivityInterval]'s formed in operation.
     * Operations execute in transaction.
     * @return [Completable] when operation completes, regardless of count of really deleted entities.
     * Result [Completable] doesn't specify schedulers.
     */
    fun deleteBlacklistContactPhoneItems(items: List<DbBlacklistContactPhoneItem>): Completable

    /**
     * @return [CompletableTransformer], that can transform completable to execute in transaction.
     */
    fun inTransactionCompletable(): CompletableTransformer
}