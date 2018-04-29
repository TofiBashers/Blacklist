package com.gmail.tofibashers.blacklist.data.db.table_constants


/**
 * Created by TofiBashers on 28.04.2018.
 */
object BlacklistContactPhoneItemTable {
    const val TABLE_NAME = "blacklist_contact_phone_item"

    const val _ID = "_id"
    const val BLACKLIST_CONTACT_ID = "blacklist_contact_id"
    const val DEVICE_DB_ID = "device_db_id"
    const val NUMBER = "number"
    const val IGNORE_SMS = "ignore_sms"
    const val IGNORE_CALLS = "ignore_calls"

    const val _ID_WITH_TABLE_PREFIX = TABLE_NAME + "." + _ID
    const val INDEX_BLACKLIST_CONTACT_PHONE_ITEM_BLACKLIST_CONTACT_ID = "index_blacklist_contact_phone_item_blacklist_contact_id"
}