package com.gmail.tofibashers.blacklist.data.db.dao

import android.arch.persistence.room.*
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItem
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistItemTable
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single


/**
 * Created by TofiBashers on 20.01.2018.
 */
@Dao
abstract class IBlackListItemDao {

    fun deleteBlackListItemsByIdsAsCompletable(vararg dbBlacklistIts: DbBlacklistItem) : Completable =
            Completable.fromCallable { deleteBlackListItemsByIds(*dbBlacklistIts) }

    fun insertBlackListItemAsSingle(dbBlacklistItem: DbBlacklistItem) : Single<Long> =
            Single.fromCallable { insertBlackListItem(dbBlacklistItem) }

    /**
     * Return [Single] updated rows count
     */
    fun updateBlackListItemWithGetUpdateCountAsSingle(dbBlacklistItem: DbBlacklistItem) : Single<Int> =
            Single.fromCallable { updateBlackListItemWithGetUpdaterCount(dbBlacklistItem) }

    @Query("SELECT * FROM " + BlacklistItemTable.TABLE_NAME)
    abstract fun getAllWithChanges(): Flowable<List<DbBlacklistItem>>

    @Query("SELECT * FROM " + BlacklistItemTable.TABLE_NAME +
            " WHERE " + BlacklistItemTable._ID + "=:id")
    abstract fun getByIdOrException(id: Long?): Single<DbBlacklistItem>

    @Query("SELECT * FROM " + BlacklistItemTable.TABLE_NAME +
            " WHERE " + BlacklistItemTable.NUMBER + "=:number")
    abstract fun getByNumber(number: String): Maybe<DbBlacklistItem>

    @Delete
    abstract fun deleteBlackListItemsByIds(vararg dbBlacklistItems: DbBlacklistItem)

    @Insert
    abstract fun insertBlackListItem(dbBlacklistItem: DbBlacklistItem) : Long

    /**
     * Return updated rows count
     */
    @Update
    abstract fun updateBlackListItemWithGetUpdaterCount(dbBlacklistItem: DbBlacklistItem) : Int
}