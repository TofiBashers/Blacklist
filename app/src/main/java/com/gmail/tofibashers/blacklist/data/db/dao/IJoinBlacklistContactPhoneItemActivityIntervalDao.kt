package com.gmail.tofibashers.blacklist.data.db.dao

import android.arch.persistence.room.*
import com.gmail.tofibashers.blacklist.data.db.entity.*
import com.gmail.tofibashers.blacklist.data.db.table_constants.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single


/**
 * Created by TofiBashers on 06.05.2018.
 */
@Dao
abstract class IJoinBlacklistContactPhoneItemActivityIntervalDao {

    fun removeAllJoinsWithBlacklistContactPhoneItemIdsAsCompletable(vararg blacklistContactPhoneItemIds: Long?) : Completable =
            Completable.fromCallable {
                removeAllJoinsWithBlacklistContactPhoneItemId(*blacklistContactPhoneItemIds)
            }

    fun insertWithIgnoreConflictsAsCompletable(joinBlacklistContactPhoneActivityInterval: DbJoinBlacklistContactPhoneItemActivityInterval) : Completable =
            Completable.fromAction { insertWithIgnoreConflicts(joinBlacklistContactPhoneActivityInterval) }

    @Query("SELECT * FROM ${BlacklistContactItemTable.TABLE_NAME}")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    abstract fun getAllBlacklistContactItemsWithPhonesAndActivityIntervalIdsWithChanges() :
            Flowable<List<DbBlacklistContactWithPhonesWithJoinBlacklistContactPhoneItemActivityInterval>>

    @Query("SELECT * FROM ${BlacklistContactPhoneItemTable.TABLE_NAME}"
            + " WHERE ${BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID} = :blacklistContactId")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    abstract fun getBlacklistContactPhonesAndActivityIntervalIdsByBlacklistContactId(blacklistContactId: Long?) :
            Single<List<DbBlacklistContactPhoneWithJoinBlacklistContactPhoneItemActivityInterval>>

    @Query("SELECT ${ActivityIntervalTable._ID_WITH_TABLE_PREFIX} AS ${ActivityIntervalTable._ID},"
            + " ${ActivityIntervalTable.WEEKDAY_ID_WITH_TABLE_PREFIX} AS ${ActivityIntervalTable.WEEKDAY_ID},"
            + " ${ActivityIntervalTable.BEGIN_TIME_WITH_TABLE_PREFIX} AS ${ActivityIntervalTable.BEGIN_TIME},"
            + " ${ActivityIntervalTable.END_TIME_WITH_TABLE_PREFIX} AS ${ActivityIntervalTable.END_TIME}"
            + " FROM ${BlacklistContactPhoneItemTable.TABLE_NAME}"
            + " INNER JOIN ${JoinBlacklistContactPhoneItemActivityIntervalTable.TABLE_NAME}"
            + " ON ${BlacklistContactPhoneItemTable._ID_WITH_TABLE_PREFIX} = ${JoinBlacklistContactPhoneItemActivityIntervalTable.BLACKLIST_CONTACT_PHONE_ITEM_ID_TABLE_PREFIX}"
            + " INNER JOIN ${ActivityIntervalTable.TABLE_NAME}"
            + " ON ${JoinBlacklistContactPhoneItemActivityIntervalTable.ACTIVITY_INTERVAL_ID_TABLE_PREFIX} = ${ActivityIntervalTable._ID_WITH_TABLE_PREFIX}"
            + " WHERE ${BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID_WITH_TABLE_PREFIX} = :blacklistContactItemId")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    abstract fun getActvitiyIntervalsForBlacklistContactItemIdAsSingle(blacklistContactItemId: Long?) : Single<List<DbActivityInterval>>

    @Query("DELETE FROM ${JoinBlacklistContactPhoneItemActivityIntervalTable.TABLE_NAME}"
            + " WHERE ${JoinBlacklistContactPhoneItemActivityIntervalTable.BLACKLIST_CONTACT_PHONE_ITEM_ID} IN (:blacklistContactPhoneItemIds)")
    protected abstract fun removeAllJoinsWithBlacklistContactPhoneItemId(vararg blacklistContactPhoneItemIds: Long?)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertWithIgnoreConflicts(joinBlacklistContactPhoneActivityInterval: DbJoinBlacklistContactPhoneItemActivityInterval)
}