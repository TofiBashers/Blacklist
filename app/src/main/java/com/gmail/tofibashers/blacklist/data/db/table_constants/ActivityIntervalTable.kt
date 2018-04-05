package com.gmail.tofibashers.blacklist.data.db.table_constants

/**
 * Created by TofixXx on 28.08.2015.
 */
object ActivityIntervalTable {
    const val TABLE_NAME = "activity_interval"

    const val _ID = "_id"
    const val BLACKLIST_ITEM_ID = "blacklist_item_id"
    const val WEEKDAY_ID = "weekday_id"
    const val BEGIN_TIME = "begin_time"
    const val END_TIME = "end_time"

    const val _ID_WITH_TABLE_PREFIX = TABLE_NAME + "." + _ID
}
