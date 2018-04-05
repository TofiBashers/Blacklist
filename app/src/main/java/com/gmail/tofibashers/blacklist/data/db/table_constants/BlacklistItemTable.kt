package com.gmail.tofibashers.blacklist.data.db.table_constants

/**
 * Created by TofixXx on 28.08.2015.
 */
object BlacklistItemTable {

    const val TABLE_NAME = "blacklist_item"

    const val _ID = "_id"
    const val NUMBER = "number"
    const val IGNORE_SMS = "ignore_sms"
    const val IGNORE_CALLS = "ignore_calls"

    const val _ID_WITH_TABLE_PREFIX = TABLE_NAME + "." + _ID
}
