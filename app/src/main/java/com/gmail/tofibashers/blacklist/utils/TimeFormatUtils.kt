package com.gmail.tofibashers.blacklist.utils

import android.content.Context
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.wdullaer.materialdatetimepicker.time.Timepoint
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utils for formatting and calculating date-time setttings,
 * depends on selected device locale and other params. All compares and conversions executes only
 * with clean time parameters (hours, minutes etc.), WITHOUT conversions their timezones.
 * Created by TofixXx on 04.09.2015.
 */
@Singleton
class TimeFormatUtils
@Inject
constructor(private val appContext: Context,
            private val systemDateTimeProvider: ISystemDateTimeProvider){

    /**
     * @param mayNonTrivialMidnightFormat true, if midnight time in some languages must return as 24:00
     * @return string with hours and minutes in format, depends on device current_locale settings.
     * Midnight in 24h format with [mayNonTrivialMidnightFormat] true, returns as 24:00, but not as 0:00.
     */
    @Suppress("DEPRECATION")
    fun localTimeToLocalizedViewTime(localTime: LocalTime, mayNonTrivialMidnightFormat: Boolean) : String {
        val isoUnzonedLocalTime = LocalTime(localTime.hourOfDay, localTime.minuteOfHour, localTime.secondOfMinute, localTime.millisOfSecond)
        val isRuDeviceLanguage = isRuDeviceLanguage()
        if(mayNonTrivialMidnightFormat && isoUnzonedLocalTime.equals(MIDNIGHT_ISO_UNZONED_TIME) && isRuDeviceLanguage){
            return MIDNIGHT_END_TIME_24h_HOUR_MINUTE_FORMAT
        }
        val pattern =
                if(isRuDeviceLanguage) ISODateTimeFormat.hourMinute()
                        .withLocale(LOCALE_RUSSIAN)
                else DateTimeFormat.forPattern("hh:mm a")
                        .withLocale(LOCALE_ENGLISH)
        return localTime.toString(pattern)
    }

    fun intValuesViewToLocalTime(hour: Int, minutes: Int) : LocalTime = LocalTime(hour, minutes)

    fun is24hourFormatForView() : Boolean = isRuDeviceLanguage()

    /**
     * @return array of 2 elements, first is a hour value in a 24h format,
     * second is a minutes value (from 0 to 59)
     */
    fun localTimeToViewIntArray(time: LocalTime): IntArray =
            intArrayOf(time.hourOfDay, time.minuteOfHour)

    /**
     * @return [Timepoint] with a hours and minutes values
     */
    fun localTimesToViewTimepoints(vararg times: LocalTime): Array<Timepoint> {
        return times.map {
            time: LocalTime -> Timepoint(time.hourOfDay, time.minuteOfHour)
        }.toTypedArray()
    }

    /**
     * @return id of weekday in [ActivityInterval] format, where 1 id of Sunday, 7 for Saturday
     */
    fun currentWeekdayAsActivityIntervalFormat() : Int =
            Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    /**
     * @return weekday ids in order, depend on device language settings
     */
    @Suppress("DEPRECATION")
    fun getWeekdayIdsInLocalizedOrder() : List<Int>{
        return if(isRuDeviceLanguage()) DEFAULT_WEEKDAYS_ORDER
        else BEGIN_SUNDAY_WEEKDAYS_ORDER
    }

    /**
     * @return weekday ids in asc order (from 1 to 7)
     */
    fun getWeekdayIdsInNonLocalizedOrder() : List<Int> = BEGIN_SUNDAY_WEEKDAYS_ORDER

    fun weekdayIdToLocalizedWeekdayName(@android.support.annotation.IntRange(from = 1, to = 7) weekdayId: Int) = appContext.resources.getStringArray(R.array.timesettings_weekday_titles)[weekdayId - 1]

    /**
     * Checks if current device time (without date, timezone etc.) between [startTime] and [endTime].
     * If [endTime] before than [startTime], [endTime] used as on next day.
     */
    fun isCurrentTimeInInterval(startTime: LocalTime, endTime: LocalTime) : Boolean {
        val currentDateTime = systemDateTimeProvider.now()
        val startInCurrentDateTime = DateTime(currentDateTime.year,  //because start and end time can have interval Chronology and DateTimeZone settings
                currentDateTime.monthOfYear,
                currentDateTime.dayOfMonth,
                startTime.hourOfDay,
                startTime.minuteOfHour)
        val endInCurrentDateTime = DateTime(currentDateTime.year,
                currentDateTime.monthOfYear,
                currentDateTime.dayOfMonth,
                endTime.hourOfDay,
                endTime.minuteOfHour)
        val endDateTime =
                if(endInCurrentDateTime.compareTo(startInCurrentDateTime) != 1) endInCurrentDateTime.plusDays(1)
                else endInCurrentDateTime
        return Interval(startInCurrentDateTime, endDateTime).contains(currentDateTime)
    }

    /**
     * @return list of [LocalTime], in UTC timezone, includes [startTime] and excludes [endTime] ignoring their input timezones,
     * provided with default min time interval value.
     * If [startTime] is equal than [endTime] or higher, returns empty list.
     * Chronology and other time settings are identical to [startTime].
     */
    fun getLocalTimesBetween(startTime: LocalTime,
                             endTime: LocalTime) : List<LocalTime>{
        return mutableListOf<LocalTime>().apply {
            val isoUnzonedStartTime = LocalTime(startTime.hourOfDay,
                    startTime.minuteOfHour,
                    startTime.secondOfMinute,
                    startTime.millisOfSecond)
            val isoUnzonedEndTime = LocalTime(endTime.hourOfDay,
                    endTime.minuteOfHour,
                    endTime.secondOfMinute,
                    endTime.millisOfSecond)
            if(isoUnzonedStartTime != isoUnzonedEndTime){
                var currentTime: LocalTime = isoUnzonedStartTime
                val correctEndTime =
                        if(isoUnzonedEndTime == TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME) DAY_END_ISO_UNZONED_TIME
                        else isoUnzonedEndTime
                while (currentTime.compareTo(correctEndTime) != 1
                        && (isEmpty() || isNotEmpty() && currentTime.compareTo(last()) != -1)){
                    add(currentTime)
                    currentTime = currentTime.plusMinutes(MIN_TIME_INTERVAL_IN_MINUTES)
                }
            }
        }
    }

    /**
     * @return first [LocalTime] before [time], with default min time interval.
     */
    fun getNearestTimeValueBefore(time: LocalTime) : LocalTime = time.minusMinutes(MIN_TIME_INTERVAL_IN_MINUTES)

    /**
     * @return first [LocalTime] after [time], with default min time interval.
     */
    fun getNearestTimeValueAfter(time: LocalTime) : LocalTime = time.plusMinutes(MIN_TIME_INTERVAL_IN_MINUTES)

    private fun isRuDeviceLanguage() : Boolean = appContext.getString(R.string.current_locale).equals(LANGUAGE_RUSSIAN)

    companion object {

        @JvmField
        val MIDNIGHT_ISO_UNZONED_TIME = LocalTime.MIDNIGHT

        @JvmField
        val DAY_END_ISO_UNZONED_TIME = LocalTime(23, 59, 59, 999)

        const val MIN_TIME_INTERVAL_IN_MINUTES = 5

        @JvmField
        val BEGIN_SUNDAY_WEEKDAYS_ORDER = IntRange(1, 7).toList()

        @JvmField
        val DEFAULT_WEEKDAYS_ORDER = listOf(*(IntRange(2, 7).toList().toTypedArray()), 1)

        private const val LANGUAGE_RUSSIAN = "ru"
        private val LOCALE_RUSSIAN = Locale(LANGUAGE_RUSSIAN,"")
        private val LOCALE_ENGLISH = Locale.ENGLISH
        private val MIDNIGHT_END_TIME_24h_HOUR_MINUTE_FORMAT = "24:00"
    }
}
