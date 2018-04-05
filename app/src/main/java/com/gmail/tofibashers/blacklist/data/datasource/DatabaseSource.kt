package com.gmail.tofibashers.blacklist.data.datasource

import android.util.Log
import com.gmail.tofibashers.blacklist.data.db.BlacklistDatabase
import com.gmail.tofibashers.blacklist.data.db.dao.IActivityIntervalDao
import com.gmail.tofibashers.blacklist.data.db.dao.IBlackListItemDao
import com.gmail.tofibashers.blacklist.data.db.dao.IJoinBlacklistItemActivityIntervalDao
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
        private val blackListItemDao: IBlackListItemDao,
        private val joinBlacklistItemActivityIntervalDao: IJoinBlacklistItemActivityIntervalDao,
        private val dbJoinBlacklistItemActivityIntervalFactory: DbJoinBlacklistItemActivityIntervalFactory,
        private val dbBlacklistItemWithActivityIntervalsFactory: DbBlacklistItemWithActivityIntervalsFactory
) : IDatabaseSource {

    override fun getAllBlacklistItemsWithIntervalsWithChanges(): Flowable<List<DbBlacklistItemWithActivityIntervals>> {
        return joinBlacklistItemActivityIntervalDao.getAllBlacklistItemsWithActivityIntervalIdsWithChanges()
                .switchMap { itemsWithJoins : List<DbBlacklistItemWithJoinsBlacklistItemActivityInterval> ->
                    return@switchMap Flowable.fromIterable(itemsWithJoins)
                            .map { item : DbBlacklistItemWithJoinsBlacklistItemActivityInterval ->
                                getFlowableDbActivityIntervalsByItemWithChanges(item)
                            }
                            .toList()
                            .flatMapPublisher { resultFlowables: List<Flowable<DbBlacklistItemWithActivityIntervals>> ->
                                return@flatMapPublisher if(resultFlowables.isEmpty()) Flowable.just(emptyList())
                                else Flowable.combineLatest(resultFlowables.toMutableList(),
                                        { resultItems: Array<Any> ->
                                            resultItems.map {
                                                resultItemObj: Any -> resultItemObj as DbBlacklistItemWithActivityIntervals
                                            }
                                        })
                            }
                }
    }

    override fun getBlacklistItemByNumber(number: String): Maybe<DbBlacklistItem> = blackListItemDao.getByNumber(number)

    override fun putBlacklistItemWithActivityIntervals(itemWithIntervals: DbBlacklistItemWithActivityIntervals): Completable {
        return putBlacklistItemWithGetId(itemWithIntervals.dbBlacklistItem)
                .flatMapCompletable { id:Long ->
                    putActivityIntervalsWithAddToBlacklistItem(id, itemWithIntervals.dbActivityIntervals)
                }
                .compose(inTransactionCompletable())
    }

    override fun getAllBlackListItemsWithChanges(): Flowable<List<DbBlacklistItem>> =
            blackListItemDao.getAllWithChanges()

    override fun deleteBlackListItem(dbBlacklistItem: DbBlacklistItem) : Completable {
        return deleteAllActivityIntervalsAssociationsAndClearOrphans(dbBlacklistItem.id)
                .andThen(blackListItemDao.deleteBlackListItemsByIdsAsCompletable(dbBlacklistItem))
                .compose(inTransactionCompletable())
    }

    private fun putBlacklistItemWithGetId(dbBlacklistItem: DbBlacklistItem): Single<Long> {
        return blackListItemDao.updateBlackListItemWithGetUpdateCountAsSingle(dbBlacklistItem)
                .flatMap {
                    if(it == 1) {
                        blackListItemDao.getByIdOrException(dbBlacklistItem.id)
                                .map { it.id }
                    }
                    else blackListItemDao.insertBlackListItemAsSingle(dbBlacklistItem)
                }
    }

    override fun getActivityIntervalsAssociatedWithBlacklistItem(blacklistItem: DbBlacklistItem): Maybe<List<DbActivityInterval>> =
            joinBlacklistItemActivityIntervalDao.getActvitiyIntervalsForBlacklistItemIdAsMaybe(blacklistItem.id)

    private fun putActivityIntervalsWithAddToBlacklistItem(blacklistItemId: Long,
                                                           dbActivityIntervals: List<DbActivityInterval>): Completable {
        return deleteAllActivityIntervalsAssociationsAndClearOrphans(blacklistItemId)
                .andThen(Observable.fromIterable(dbActivityIntervals))
                .flatMapCompletable { insertActivityIntervalWithAddToBlacklistItem(blacklistItemId, it) }
                .compose(inTransactionCompletable())
    }

    private fun deleteAllActivityIntervalsAssociationsAndClearOrphans(blacklistItemId: Long?) : Completable {
        return joinBlacklistItemActivityIntervalDao.removeAllJoinsWithBlacklistItemIdAsCompletable(blacklistItemId)
                .andThen(joinBlacklistItemActivityIntervalDao.removeActivityIntervalsNonAssociatedWithBlacklistItemsAsCompletable())
    }

    private fun insertActivityIntervalWithAddToBlacklistItem(blacklistItemId: Long,
                                                             dbActivityInterval: DbActivityInterval): Completable {
        return activityIntervalDao.getActivityIntervalByTimesAndWeekday(dbActivityInterval.dayOfWeek,
                dbActivityInterval.beginTime,
                dbActivityInterval.endTime)
                .map ({ it.id })
                .switchIfEmpty(activityIntervalDao.insertActivityIntervalWithGetIdAsSingle(dbActivityInterval))
                .map({ dbJoinBlacklistItemActivityIntervalFactory.create(blacklistItemId, it) })
                .flatMapCompletable { joinBlacklistItemActivityIntervalDao.insertWithIgnoreConflictsAsCompletable(it) }

    }

    private fun getFlowableDbActivityIntervalsByItemWithChanges(
            item : DbBlacklistItemWithJoinsBlacklistItemActivityInterval) : Flowable<DbBlacklistItemWithActivityIntervals>{
        return Flowable.fromIterable(item.listOfJoins)
                .map {joinItem: DbJoinBlacklistItemActivityInterval ->
                    activityIntervalDao.getActivityIntervalByIdWithChanges(joinItem.activityIntervalId)
                }
                .toList()
                .flatMapPublisher {intervalsFlowable: List<Flowable<DbActivityInterval>> ->
                    Flowable.combineLatest(intervalsFlowable.toMutableList(),
                            {intervalChanges: Array<Any> ->
                                dbBlacklistItemWithActivityIntervalsFactory.create(
                                        item.blacklistItem,
                                        intervalChanges.toList()
                                                .map {intervalObj: Any -> intervalObj as DbActivityInterval })
                            })
                }
    }

    private fun inTransactionCompletable(): CompletableTransformer {
        return CompletableTransformer { upstream: Completable ->
            return@CompletableTransformer Completable.fromAction { database.beginTransaction() }
                    .andThen(upstream)
                    .doOnComplete { database.setTransactionSuccessful() }
                    .doFinally { database.endTransaction() }
        }
    }

}