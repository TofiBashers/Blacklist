package com.gmail.tofibashers.blacklist.data.db.dao

import android.arch.persistence.room.*
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistItemTable
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single


/**
 * Created by TofiBashers on 20.01.2018.
 */
@Dao
abstract class IBlacklistPhoneNumberItemDao {

    fun deleteBlacklistPhoneNumberItemsByIdsAsCompletable(vararg dbBlacklistPhoneNumberIts: DbBlacklistPhoneNumberItem) : Completable =
            Completable.fromCallable { deleteBlacklistPhoneNumberItemsByIds(*dbBlacklistPhoneNumberIts) }

    fun insertBlacklistPhoneNumberItemAsSingle(dbBlacklistPhoneNumberItem: DbBlacklistPhoneNumberItem) : Single<Long> =
            Single.fromCallable { insertBlacklistPhoneNumberItem(dbBlacklistPhoneNumberItem) }

    /**
     * Return [Single] updated rows count
     */
    fun updateBlacklistPhoneNumberItemWithGetUpdateCountAsSingle(dbBlacklistPhoneNumberItem: DbBlacklistPhoneNumberItem) : Single<Int> =
            Single.fromCallable { updateBlacklistPhoneNumberItemWithGetUpdaterCount(dbBlacklistPhoneNumberItem) }

    @Query("SELECT * FROM " + BlacklistItemTable.TABLE_NAME)
    abstract fun getAllWithChanges(): Flowable<List<DbBlacklistPhoneNumberItem>>

    @Query("SELECT * FROM " + BlacklistItemTable.TABLE_NAME +
            " WHERE " + BlacklistItemTable._ID + "=:id")
    abstract fun getByIdOrException(id: Long?): Single<DbBlacklistPhoneNumberItem>

    @Query("SELECT * FROM " + BlacklistItemTable.TABLE_NAME +
            " WHERE " + BlacklistItemTable.NUMBER + "=:number")
    abstract fun getByNumber(number: String): Maybe<DbBlacklistPhoneNumberItem>

    @Delete
    abstract fun deleteBlacklistPhoneNumberItemsByIds(vararg dbBlacklistPhoneNumberItems: DbBlacklistPhoneNumberItem)

    @Insert
    abstract fun insertBlacklistPhoneNumberItem(dbBlacklistPhoneNumberItem: DbBlacklistPhoneNumberItem) : Long

    /**
     * Return updated rows count
     */
    @Update
    abstract fun updateBlacklistPhoneNumberItemWithGetUpdaterCount(dbBlacklistPhoneNumberItem: DbBlacklistPhoneNumberItem) : Int
}