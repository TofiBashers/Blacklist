package com.gmail.tofibashers.blacklist.data.db.dao

import android.arch.persistence.room.*
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactItem
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistItem
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistContactItemTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistItemTable
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single


/**
 * Created by TofiBashers on 06.05.2018.
 */
@Dao
abstract class IBlacklistContactItemDao {

    fun deleteByIdsAsCompletable(vararg items: DbBlacklistContactItem) : Completable =
            Completable.fromAction { deleteByIds(*items) }

    fun insertWithGetInsertedIdAsSingle(item: DbBlacklistContactItem) : Single<Long> =
            Single.fromCallable { insertWithGetInsertedId(item) }

    fun insertAsCompletable(item: DbBlacklistContactItem) : Completable =
            Completable.fromAction { insert(item) }

    fun updateByIdsWithGetUpdatedCountAsSingle(item: DbBlacklistContactItem) : Single<Int> =
            Single.fromCallable { updateByIdWithGetUpdatedCount(item) }

    @Query("SELECT * FROM ${BlacklistContactItemTable.TABLE_NAME}")
    abstract fun getAll(): Single<List<DbBlacklistContactItem>>

    @Query("SELECT * FROM ${BlacklistContactItemTable.TABLE_NAME}"
            + " ORDER BY ${BlacklistContactItemTable.NAME} ASC")
    abstract fun getAllSortedByNameAscWithChanges(): Flowable<List<DbBlacklistContactItem>>

    @Query("SELECT * FROM ${BlacklistContactItemTable.TABLE_NAME}"
            + " WHERE ${BlacklistContactItemTable._ID} = :id")
    abstract fun getById(id: Long?): Maybe<DbBlacklistContactItem>

    @Query("SELECT * FROM ${BlacklistContactItemTable.TABLE_NAME}" +
            " WHERE ${BlacklistContactItemTable._ID} = :id")
    abstract fun getByIdOrException(id: Long?): Single<DbBlacklistContactItem>

    @Delete
    protected abstract fun deleteByIds(vararg items: DbBlacklistContactItem)

    @Insert
    protected abstract fun insertWithGetInsertedId(dbBlacklistContactItem: DbBlacklistContactItem) : Long

    @Insert
    protected abstract fun insert(dbBlacklistContactItem: DbBlacklistContactItem)

    /**
     * Return updated rows count
     */
    @Update
    protected abstract fun updateByIdWithGetUpdatedCount(dbBlacklistContactItem: DbBlacklistContactItem) : Int
}