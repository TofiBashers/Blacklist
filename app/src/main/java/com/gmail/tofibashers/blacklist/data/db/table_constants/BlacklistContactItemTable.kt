package com.gmail.tofibashers.blacklist.data.db.table_constants


/**
 * Created by TofiBashers on 28.04.2018.
 */
object BlacklistContactItemTable {
    const val TABLE_NAME = "blacklist_contact_item"

    const val _ID = "_id"
    const val DEVICE_DB_ID = "device_db_id"
    const val DEVICE_LOOKUP_KEY = "device_lookup_key"
    const val NAME = "name"
    const val PHOTO_URL = "photo_url"

    const val _ID_WITH_TABLE_PREFIX = TABLE_NAME + "." + _ID
    const val DEVICE_DB_ID_WITH_TABLE_PREFIX = TABLE_NAME + "." + DEVICE_DB_ID
    const val DEVICE_LOOKUP_KEY_WITH_TABLE_PREFIX = TABLE_NAME + "." + DEVICE_LOOKUP_KEY
}