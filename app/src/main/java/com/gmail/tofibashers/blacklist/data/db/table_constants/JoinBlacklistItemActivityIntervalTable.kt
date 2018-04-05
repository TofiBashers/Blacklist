package com.gmail.tofibashers.blacklist.data.db.table_constants


/**
 * Created by TofiBashers on 26.01.2018.
 */
object JoinBlacklistItemActivityIntervalTable {
    const val TABLE_NAME = "blacklist_item_activity_interval"

    const val BLACKLIST_ITEM_ID = "blacklist_item_id"
    const val ACTIVITY_INTERVAL_ID = "activity_interval_id"

    const val ACTIVITY_INTERVAL_ID_TABLE_PREFIX = TABLE_NAME + "." + ACTIVITY_INTERVAL_ID
    const val BLACKLIST_ITEM_ID_TABLE_PREFIX = TABLE_NAME + "." + BLACKLIST_ITEM_ID
}