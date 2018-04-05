package com.gmail.tofibashers.blacklist.data.db.dao

import android.arch.persistence.room.*
import com.gmail.tofibashers.blacklist.data.db.entity.DbActivityInterval
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItemWithJoinsBlacklistItemActivityInterval
import com.gmail.tofibashers.blacklist.data.db.entity.DbJoinBlacklistItemActivityInterval
import com.gmail.tofibashers.blacklist.data.db.table_constants.ActivityIntervalTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistItemTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.JoinBlacklistItemActivityIntervalTable
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Created by TofiBashers on 26.01.2018.
 */

@Dao
abstract class IJoinBlacklistItemActivityIntervalDao {

    fun insertWithIgnoreConflictsAsCompletable(joinBlacklistItemActivityInterval:
                                               DbJoinBlacklistItemActivityInterval) : Completable =
            Completable.fromCallable { insertWithIgnoreConflicts(joinBlacklistItemActivityInterval) }

    fun removeAllJoinsWithBlacklistItemIdAsCompletable(blacklistItemId: Long?) : Completable =
            Completable.fromCallable { removeAllJoinsWithBlacklistItemId(blacklistItemId) }

    fun removeActivityIntervalsNonAssociatedWithBlacklistItemsAsCompletable() : Completable =
            Completable.fromCallable { removeActivityIntervalsNonAssociatedWithBlacklistItems() }

    @Query("SELECT * FROM " + JoinBlacklistItemActivityIntervalTable.TABLE_NAME
            + " INNER JOIN " + ActivityIntervalTable.TABLE_NAME
            + " ON " + JoinBlacklistItemActivityIntervalTable.ACTIVITY_INTERVAL_ID_TABLE_PREFIX + " = "
            + ActivityIntervalTable._ID_WITH_TABLE_PREFIX
            + " WHERE " + JoinBlacklistItemActivityIntervalTable.BLACKLIST_ITEM_ID_TABLE_PREFIX + " = :blacklistItemId")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    abstract fun getActvitiyIntervalsForBlacklistItemId(blacklistItemId: Long?) : Single<List<DbActivityInterval>>

    @Query("SELECT * FROM " + JoinBlacklistItemActivityIntervalTable.TABLE_NAME
            + " INNER JOIN " + ActivityIntervalTable.TABLE_NAME
            + " ON " + JoinBlacklistItemActivityIntervalTable.ACTIVITY_INTERVAL_ID_TABLE_PREFIX + " = "
            + ActivityIntervalTable._ID_WITH_TABLE_PREFIX
            + " WHERE " + JoinBlacklistItemActivityIntervalTable.BLACKLIST_ITEM_ID_TABLE_PREFIX + " = :blacklistItemId")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    abstract fun getActvitiyIntervalsForBlacklistItemIdAsMaybe(blacklistItemId: Long?) : Maybe<List<DbActivityInterval>>


    @Query("SELECT * FROM " + BlacklistItemTable.TABLE_NAME)
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    abstract fun getAllBlacklistItemsWithActivityIntervalIdsWithChanges() : Flowable<List<DbBlacklistItemWithJoinsBlacklistItemActivityInterval>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertWithIgnoreConflicts(joinBlacklistItemActivityInterval: DbJoinBlacklistItemActivityInterval)

    @Query("DELETE FROM " + JoinBlacklistItemActivityIntervalTable.TABLE_NAME
            + " WHERE " + JoinBlacklistItemActivityIntervalTable.BLACKLIST_ITEM_ID + " = :blacklistItemId")
    protected abstract fun removeAllJoinsWithBlacklistItemId(blacklistItemId: Long?)

    @Query("DELETE FROM " + ActivityIntervalTable.TABLE_NAME
            + " WHERE " + ActivityIntervalTable._ID + " NOT IN"
            + "(SELECT DISTINCT " + JoinBlacklistItemActivityIntervalTable.ACTIVITY_INTERVAL_ID
            + " FROM " + JoinBlacklistItemActivityIntervalTable.TABLE_NAME + ")")
    protected abstract fun removeActivityIntervalsNonAssociatedWithBlacklistItems()
}