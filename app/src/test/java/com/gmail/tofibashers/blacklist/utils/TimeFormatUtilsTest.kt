package com.gmail.tofibashers.blacklist.utils

import com.gmail.tofibashers.blacklist.TestApplication
import com.nhaarman.mockito_kotlin.*
import junit.framework.Assert
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config


/**
 * Created by TofiBashers on 18.03.2018.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class TimeFormatUtilsTest {

    @Rule
    @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var mockSystemDateTimeProvider: ISystemDateTimeProvider

    lateinit var timeFormatUtils: TimeFormatUtils

    @Before
    fun setUp(){
        timeFormatUtils = TimeFormatUtils(RuntimeEnvironment.application, mockSystemDateTimeProvider)
    }

    @Test
    fun testGetLocalTimesBetweenEqualsMidnight_Empty(){
        val testStartTime = LocalTime(0,0)
        val testEndTime = LocalTime(0,0)
        val resList = timeFormatUtils.getLocalTimesBetween(testStartTime, testEndTime)
        Assert.assertTrue(resList.isEmpty())
    }

    @Test
    fun testGetLocalTimesBetweenEqualsTimes_Empty(){
        val testStartTime = LocalTime(1, 0)
        val testEndTime = LocalTime(1,0)
        val resList = timeFormatUtils.getLocalTimesBetween(testStartTime, testEndTime)
        Assert.assertTrue(resList.isEmpty())
    }

    @Test
    fun testGetLocalTimesBetweenStartHigherThanEnd_Empty(){
        val testStartTime = LocalTime(2, 0)
        val testEndTime = LocalTime(1,0)
        val resList = timeFormatUtils.getLocalTimesBetween(testStartTime, testEndTime)
        Assert.assertTrue(resList.isEmpty())
    }

    @Test
    fun testGetLocalTimesBetweenEndIsMidnight_AllFromStartBeforeMidnight(){
        val testStartTime = LocalTime(23, 40)
        val testEndTime = LocalTime.MIDNIGHT
        val resList = timeFormatUtils.getLocalTimesBetween(testStartTime, testEndTime)
        Assert.assertTrue(resList.containsAll(listOf(
                testStartTime,
                LocalTime(23, 45),
                LocalTime(23, 50),
                LocalTime(23, 55))))
        Assert.assertEquals(4, resList.size)
    }

    @Test
    fun testGetLocalTimesBetweenBeginIsMidnight_AllFromMidnightIncludeEnd(){
        val testStartTime = LocalTime.MIDNIGHT
        val testEndTime = LocalTime(0, 10)
        val resList = timeFormatUtils.getLocalTimesBetween(testStartTime, testEndTime)
        Assert.assertTrue(resList.containsAll(listOf(
                testStartTime,
                LocalTime(0, 5),
                LocalTime(0, 10))))
        Assert.assertEquals(3, resList.size)
    }

    @Test
    fun testIs24hourFormatForView_Two(){
        val testStartTime = LocalTime(1, 5)
        val testEndTime = testStartTime.plusMinutes(TimeFormatUtils.MIN_TIME_INTERVAL_IN_MINUTES)
        val resList = timeFormatUtils.getLocalTimesBetween(testStartTime, testEndTime)
        Assert.assertTrue(resList.containsAll(listOf(testStartTime, testEndTime)))
        Assert.assertEquals(2, resList.size)
    }

    @Test
    fun testGetLocalTimesBetweenTwoNeighbours_Two(){
        val testStartTime = LocalTime(1, 5)
        val testEndTime = testStartTime.plusMinutes(TimeFormatUtils.MIN_TIME_INTERVAL_IN_MINUTES)
        val resList = timeFormatUtils.getLocalTimesBetween(testStartTime, testEndTime)
        Assert.assertTrue(resList.containsAll(listOf(testStartTime, testEndTime)))
        Assert.assertEquals(2, resList.size)
    }

    @Config(qualifiers = "+ru-rBY")
    @Test
    fun testIs24hourFormatForViewQualif_rurBY_True(){
        Assert.assertTrue(timeFormatUtils.is24hourFormatForView())
    }

    @Config(qualifiers = "+en-rAU")
    @Test
    fun testIs24hourFormatForViewQualif_enrAU_False(){
        Assert.assertFalse(timeFormatUtils.is24hourFormatForView())
    }

    @Config(qualifiers = "+ru-rBY")
    @Test
    fun testGetWeekdayIdsInLocalizedOrder_rurBY_DefaultOrder(){
        Assert.assertEquals(TimeFormatUtils.DEFAULT_WEEKDAYS_ORDER, timeFormatUtils.getWeekdayIdsInLocalizedOrder())
    }

    @Config(qualifiers = "+en-rAU")
    @Test
    fun testGetWeekdayIdsInLocalizedOrder_enrAU_BeginSundayOrder(){
        Assert.assertEquals(TimeFormatUtils.BEGIN_SUNDAY_WEEKDAYS_ORDER, timeFormatUtils.getWeekdayIdsInLocalizedOrder())
    }

    @Config(qualifiers = "+ru-rBY")
    @Test
    fun testLocalTimeToLocalizedViewTime_Midnight_MayNonTrivial_OnRu_24FormatMidnight(){
        val res = timeFormatUtils.localTimeToLocalizedViewTime(LocalTime.MIDNIGHT, true)
        Assert.assertEquals("24:00", res)
    }

    @Config(qualifiers = "+en-rAU")
    @Test
    fun testLocalTimeToLocalizedViewTime_Midnight_NotMayNonTrivial_OnEn_24FormatMidnight(){
        val res = timeFormatUtils.localTimeToLocalizedViewTime(LocalTime.MIDNIGHT, false)
        Assert.assertEquals("12:00 AM", res)
    }

    @Config(qualifiers = "+en-rAU")
    @Test
    fun testLocalTimeToLocalizedViewTime_Midnight_MayNonTrivial_OnEn_24FormatMidnight(){
        val res = timeFormatUtils.localTimeToLocalizedViewTime(LocalTime.MIDNIGHT, true)
        Assert.assertEquals("12:00 AM", res)
    }

    @Config(qualifiers = "+en-rAU")
    @Test
    fun testLocalTimeToLocalizedViewTime_Midnight_NonMayNonTrivial_OnEn_24FormatMidnight(){
        val res = timeFormatUtils.localTimeToLocalizedViewTime(LocalTime.MIDNIGHT, false)
        Assert.assertEquals("12:00 AM", res)
    }

    @Test
    fun testIsCurrentTimeInInterval_StartMidnight_EndMidnight_CurrentMidnight_True(){
        val testBeginTime = LocalTime(0, 0)
        val testEndTime = LocalTime(0, 0)
        val testDateTime = DateTime(2018, 3, 18, 0, 0)
        whenever(mockSystemDateTimeProvider.now()).thenReturn(testDateTime)
        val res = timeFormatUtils.isCurrentTimeInInterval(testBeginTime, testEndTime)
        Assert.assertTrue(res)
    }

    @Test
    fun testIsCurrentTimeInInterval_StartAfterMidnight_EndMidnight_CurrentMidnight_False(){
        val testBeginTime = LocalTime(0, 5)
        val testEndTime = LocalTime(0, 0)
        val testDateTime = DateTime(2018, 3, 18, 0, 0)
        whenever(mockSystemDateTimeProvider.now()).thenReturn(testDateTime)
        val res = timeFormatUtils.isCurrentTimeInInterval(testBeginTime, testEndTime)
        Assert.assertFalse(res)
    }

    @Test
    fun testIsCurrentTimeInInterval_MinIntervalBetweenStartAndEnd_CurrentInInterval_True(){
        val testBeginTime = LocalTime(0, 5)
        val testEndTime = LocalTime(0, 10)
        val testDateTime = DateTime(2018, 3, 18, 0, 8)
        whenever(mockSystemDateTimeProvider.now()).thenReturn(testDateTime)
        val res = timeFormatUtils.isCurrentTimeInInterval(testBeginTime, testEndTime)
        Assert.assertTrue(res)
    }

    @Test
    fun testIsCurrentTimeInInterval_SomeInterval_CurrentInIntervalEnd_False(){
        val testBeginTime = LocalTime(10, 5)
        val testEndTime = LocalTime(12, 10)
        val testDateTime = DateTime(2018, 3, 18, 12, 10)
        whenever(mockSystemDateTimeProvider.now()).thenReturn(testDateTime)
        val res = timeFormatUtils.isCurrentTimeInInterval(testBeginTime, testEndTime)
        Assert.assertFalse(res)
    }
}