package com.gmail.tofibashers.blacklist.data.db.dao

import android.arch.persistence.room.*
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactPhoneItem
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistContactItemTable
import com.gmail.tofibashers.blacklist.data.db.table_constants.BlacklistContactPhoneItemTable
import io.reactivex.Completable
import io.reactivex.Single


/**
 * Created by TofiBashers on 06.05.2018.
 */
@Dao
abstract class IBlacklistContactPhoneItemDao {

    fun deleteByIdsAsCompletable(vararg items: DbBlacklistContactPhoneItem) : Completable =
            Completable.fromAction { deleteByIds(*items) }

    fun insertWithGetInsertedIdAsSingle(phoneItem: DbBlacklistContactPhoneItem) : Single<Long> =
            Single.fromCallable { insertWithGetInsertedId(phoneItem) }

    fun updateByIdWithGetUpdatedCountAsSingle(phoneItem: DbBlacklistContactPhoneItem) : Single<Int> =
            Single.fromCallable { updateByIdWithGetUpdatedCount(phoneItem) }

    @Query("SELECT * FROM ${BlacklistContactPhoneItemTable.TABLE_NAME}")
    abstract fun getAll(): Single<List<DbBlacklistContactPhoneItem>>

    @Query("SELECT ${BlacklistContactPhoneItemTable._ID_WITH_TABLE_PREFIX} AS ${BlacklistContactPhoneItemTable._ID},"
            + " ${BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID_WITH_TABLE_PREFIX} AS ${BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID},"
            + " ${BlacklistContactPhoneItemTable.DEVICE_DB_ID_WITH_TABLE_PREFIX} AS ${BlacklistContactPhoneItemTable.DEVICE_DB_ID},"
            + " ${BlacklistContactPhoneItemTable.NUMBER_WITH_TABLE_PREFIX} AS ${BlacklistContactPhoneItemTable.NUMBER},"
            + " ${BlacklistContactPhoneItemTable.IGNORE_CALLS_WITH_TABLE_PREFIX} AS ${BlacklistContactPhoneItemTable.IGNORE_CALLS},"
            + " ${BlacklistContactPhoneItemTable.IGNORE_SMS_WITH_TABLE_PREFIX} AS ${BlacklistContactPhoneItemTable.IGNORE_SMS}"
            + " FROM ${BlacklistContactPhoneItemTable.TABLE_NAME}"
            + " INNER JOIN ${BlacklistContactItemTable.TABLE_NAME}"
            + " ON ${BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID_WITH_TABLE_PREFIX} = ${BlacklistContactItemTable._ID_WITH_TABLE_PREFIX}"
            + " WHERE ${BlacklistContactItemTable.DEVICE_LOOKUP_KEY_WITH_TABLE_PREFIX} = :contactDeviceLookupKey"
            + " AND ${BlacklistContactItemTable.DEVICE_DB_ID_WITH_TABLE_PREFIX} = :contactDeviceDbId")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    abstract fun getByBlacklistContactDeviceDbIdAndLookupKey(contactDeviceDbId: Long?,
                                                             contactDeviceLookupKey: String?): Single<List<DbBlacklistContactPhoneItem>>

    @Query("SELECT * FROM ${BlacklistContactPhoneItemTable.TABLE_NAME}"
            + " WHERE ${BlacklistContactPhoneItemTable.BLACKLIST_CONTACT_ID} = :blacklistContactId")
    abstract fun getByBlacklistContactId(blacklistContactId: Long): Single<List<DbBlacklistContactPhoneItem>>

    @Query("SELECT * FROM " + BlacklistContactPhoneItemTable.TABLE_NAME +
            " WHERE " + BlacklistContactPhoneItemTable._ID + " = :id")
    abstract fun getByIdOrException(id: Long?): Single<DbBlacklistContactPhoneItem>

    @Delete
    protected abstract fun deleteByIds(vararg items: DbBlacklistContactPhoneItem)

    @Insert
    protected abstract fun insertWithGetInsertedId(phoneItem: DbBlacklistContactPhoneItem) : Long

    /**
     * Return updated rows count
     */
    @Update
    protected abstract fun updateByIdWithGetUpdatedCount(phoneItem: DbBlacklistContactPhoneItem) : Int
}