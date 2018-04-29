package com.gmail.tofibashers.blacklist.data.db.table_constants


/**
 * Created by TofiBashers on 29.04.2018.
 */
object JoinBlacklistContactPhoneItemActivityIntervalTable {
    const val TABLE_NAME = "blacklist_contact_phone_item_activity_interval"

    const val BLACKLIST_CONTACT_PHONE_ITEM_ID = "blacklist_contact_phone_item_id"
    const val ACTIVITY_INTERVAL_ID = "activity_interval_id"

    const val ACTIVITY_INTERVAL_ID_TABLE_PREFIX = TABLE_NAME + "." + ACTIVITY_INTERVAL_ID
    const val BLACKLIST_CONTACT_PHONE_ITEM_ID_TABLE_PREFIX = TABLE_NAME + "." + BLACKLIST_CONTACT_PHONE_ITEM_ID
}