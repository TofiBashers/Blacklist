package com.gmail.tofibashers.blacklist.data.db.dao

import android.arch.persistence.room.*
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactItem
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistContactItemTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistContactPhoneItemTable
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

    fun deleteBlacklistContactItemsThatNonAssociatedWithAnyPhonesdAsCompletable() : Completable =
            Completable.fromAction { deleteBlacklistContactItemsThatNonAssociatedWithAnyPhones() }

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

    @Query("DELETE FROM ${BlacklistContactItemTable.TABLE_NAME} WHERE "
            + "(SELECT COUNT(*) FROM ${BlacklistContactPhoneItemTable.TABLE_NAME}"
            + " WHERE ${BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID_WITH_TABLE_PREFIX} = ${BlacklistContactItemTable._ID_WITH_TABLE_PREFIX})"
            + " = 0")
    protected abstract fun deleteBlacklistContactItemsThatNonAssociatedWithAnyPhones()

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