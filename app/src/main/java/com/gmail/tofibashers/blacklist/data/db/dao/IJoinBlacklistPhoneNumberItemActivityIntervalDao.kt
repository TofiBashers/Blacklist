package com.gmail.tofibashers.blacklist.data.db.dao

import android.arch.persistence.room.*
import com.gmail.tofibashers.blacklist.data.db.entity.DbActivityInterval
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistPhoneNumberItemWithJoinsBlacklistPhoneNumberItemActivityInterval
import com.gmail.tofibashers.blacklist.data.db.entity.DbJoinBlacklistPhoneNumberItemActivityInterval
import com.gmail.tofibashers.blacklist.data.db.table_constants.ActivityIntervalTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistItemTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.JoinBlacklistContactPhoneItemActivityIntervalTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.JoinBlacklistItemActivityIntervalTable
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by TofiBashers on 26.01.2018.
 */

@Dao
abstract class IJoinBlacklistPhoneNumberItemActivityIntervalDao {

    fun insertWithIgnoreConflictsAsCompletable(joinBlacklistPhoneNumberItemActivityInterval:
                                               DbJoinBlacklistPhoneNumberItemActivityInterval) : Completable =
            Completable.fromCallable { insertWithIgnoreConflicts(joinBlacklistPhoneNumberItemActivityInterval) }

    fun removeAllJoinsWithBlacklistPhoneNumberItemIdAsCompletable(blacklistItemId: Long?) : Completable =
            Completable.fromCallable { removeAllJoinsWithBlacklistPhoneNumberItemId(blacklistItemId) }

    fun removeActivityIntervalsNonAssociatedWithBlacklistPhoneNumberItemsAndBlacklistContactPhoneItemsAsCompletable() : Completable =
            Completable.fromCallable { removeActivityIntervalsNonAssociatedWithBlacklistPhoneNumberItemsAndBlacklistContactPhoneItems() }

    @Query("SELECT * FROM " + JoinBlacklistItemActivityIntervalTable.TABLE_NAME
            + " INNER JOIN " + ActivityIntervalTable.TABLE_NAME
            + " ON " + JoinBlacklistItemActivityIntervalTable.ACTIVITY_INTERVAL_ID_TABLE_PREFIX + " = "
            + ActivityIntervalTable._ID_WITH_TABLE_PREFIX
            + " WHERE " + JoinBlacklistItemActivityIntervalTable.BLACKLIST_ITEM_ID_TABLE_PREFIX + " = :blacklistItemId")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    abstract fun getActvitiyIntervalsForBlacklistPhoneNumberItemId(blacklistItemId: Long?) : Single<List<DbActivityInterval>>


    @Query("SELECT * FROM " + BlacklistItemTable.TABLE_NAME)
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    abstract fun getAllBlacklistPhoneNumberItemsWithActivityIntervalIdsWithChanges() : Flowable<List<DbBlacklistPhoneNumberItemWithJoinsBlacklistPhoneNumberItemActivityInterval>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertWithIgnoreConflicts(joinBlacklistPhoneNumberItemActivityInterval: DbJoinBlacklistPhoneNumberItemActivityInterval)

    @Query("DELETE FROM " + JoinBlacklistItemActivityIntervalTable.TABLE_NAME
            + " WHERE " + JoinBlacklistItemActivityIntervalTable.BLACKLIST_ITEM_ID + " = :blacklistItemId")
    protected abstract fun removeAllJoinsWithBlacklistPhoneNumberItemId(blacklistItemId: Long?)

    @Query("DELETE FROM " + ActivityIntervalTable.TABLE_NAME
            + " WHERE " + ActivityIntervalTable._ID + " NOT IN"
            + "(SELECT DISTINCT " + JoinBlacklistItemActivityIntervalTable.ACTIVITY_INTERVAL_ID
            + " FROM " + JoinBlacklistItemActivityIntervalTable.TABLE_NAME + ")"
            + " AND " + ActivityIntervalTable._ID + " NOT IN"
            + "(SELECT DISTINCT " + JoinBlacklistContactPhoneItemActivityIntervalTable.ACTIVITY_INTERVAL_ID
            + " FROM " + JoinBlacklistContactPhoneItemActivityIntervalTable.TABLE_NAME + ")")
    protected abstract fun removeActivityIntervalsNonAssociatedWithBlacklistPhoneNumberItemsAndBlacklistContactPhoneItems()
}