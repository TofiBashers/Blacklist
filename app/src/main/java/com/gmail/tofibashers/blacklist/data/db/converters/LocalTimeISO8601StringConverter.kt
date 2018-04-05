package com.gmail.tofibashers.blacklist.data.db.converters

import android.arch.persistence.room.TypeConverter
import org.joda.time.LocalTime
import org.joda.time.format.ISODateTimeFormat


/**
 * Created by TofiBashers on 19.01.2018.
 */
class LocalTimeISO8601StringConverter {

    @TypeConverter
    fun toString(localTime: LocalTime) : String = localTime.toString(ISODateTimeFormat.hourMinute())

    @TypeConverter
    fun toLocalTime(localTimeString: String) : LocalTime = LocalTime.parse(localTimeString, ISODateTimeFormat.hourMinute())
}