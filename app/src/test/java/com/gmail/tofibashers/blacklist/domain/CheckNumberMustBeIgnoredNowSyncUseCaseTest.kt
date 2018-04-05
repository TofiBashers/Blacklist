package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.TimeAndIgnoreSettingsByWeekdayId
import com.gmail.tofibashers.blacklist.entity.ActivityTimeIntervalWithIgnoreSettings
import com.gmail.tofibashers.blacklist.utils.TimeFormatUtils
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.joda.time.LocalTime
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


/**
 * Created by TofiBashers on 24.02.2018.
 */
@RunWith(MockitoJUnitRunner::class)
class CheckNumberMustBeIgnoredNowSyncUseCaseTest {

    @Mock
    lateinit var mockTimeFormatUtils: TimeFormatUtils

    @InjectMocks
    lateinit var testUseCase: CheckNumberMustBeIgnoredNowSyncUseCase

    @Test
    fun testOnSmsFromNullNumberWithIgnoreHidden_TRUE(){

        val expResult = true
        val testNumber = null
        val testIgnoreHiddenNumbers = true
        val testIsSms = true
        val mockIgnoreSettingsByNumbers = mock<HashMap<String, TimeAndIgnoreSettingsByWeekdayId>>()

        val testObserver = testUseCase.build(testNumber, testIsSms, mockIgnoreSettingsByNumbers, testIgnoreHiddenNumbers)
                .test()

        verifyZeroInteractions(mockIgnoreSettingsByNumbers)
        verifyZeroInteractions(mockTimeFormatUtils)

        testObserver.assertComplete()
        val result = testObserver.events[0][0] as Boolean
        Assert.assertEquals(expResult, result)
    }

    @Test
    fun testOnSmsFromNumberIgnoredInCurrentDayAndAnotherTime_FALSE(){

        val testDataWithExpResult = genereateSmsFromNumberWithIgnoredOnSmsInAnotherTimeAndIgnoreHidden()
        whenever(mockTimeFormatUtils.currentWeekdayAsActivityIntervalFormat())
                .thenReturn(testDataWithExpResult.testCurrentWeekday)
        whenever(mockTimeFormatUtils.isCurrentTimeInInterval(testDataWithExpResult.testBeginTimeForCheckInterval, testDataWithExpResult.testEndTimeForCheckInterval))
                .thenReturn(testDataWithExpResult.testIsCurrentTimeInInterval)

        val testObserver = testUseCase.build(testDataWithExpResult.testNumber,
                testDataWithExpResult.testIsSms,
                testDataWithExpResult.testIgnoredSettingsByNumbers,
                testDataWithExpResult.testIgnoreHiddenNumbers)
                .test()

        testObserver.assertComplete()
        val result = testObserver.events[0][0] as Boolean
        Assert.assertEquals(testDataWithExpResult.expectedResult, result)
    }

    @Test
    fun testOnSmsFromNumberIgnoredInCurrentDayAndCurrentTime_TRUE(){

        val testDataWithExpResult = genereateSmsFromNumberWithIgnoredOnSmsInCurrentTimeAndIgnoreHidden()
        whenever(mockTimeFormatUtils.currentWeekdayAsActivityIntervalFormat())
                .thenReturn(testDataWithExpResult.testCurrentWeekday)
        whenever(mockTimeFormatUtils.isCurrentTimeInInterval(testDataWithExpResult.testBeginTimeForCheckInterval, testDataWithExpResult.testEndTimeForCheckInterval))
                .thenReturn(testDataWithExpResult.testIsCurrentTimeInInterval)

        val testObserver = testUseCase.build(testDataWithExpResult.testNumber,
                testDataWithExpResult.testIsSms,
                testDataWithExpResult.testIgnoredSettingsByNumbers,
                testDataWithExpResult.testIgnoreHiddenNumbers)
                .test()

        testObserver.assertComplete()
        val result = testObserver.events[0][0] as Boolean
        Assert.assertEquals(testDataWithExpResult.expectedResult, result)
    }

    @Test
    fun testOnSmsFromNumberNotIgnored_FALSE(){

        val testDataWithExpResult = genereateSmsFromNumberNotIgnoredAndIgnoreHidden()
        whenever(mockTimeFormatUtils.currentWeekdayAsActivityIntervalFormat())
                .thenReturn(testDataWithExpResult.testCurrentWeekday)

        val testObserver = testUseCase.build(testDataWithExpResult.testNumber,
                testDataWithExpResult.testIsSms,
                testDataWithExpResult.testIgnoredSettingsByNumbers,
                testDataWithExpResult.testIgnoreHiddenNumbers)
                .test()

        testObserver.assertComplete()
        val result = testObserver.events[0][0] as Boolean
        Assert.assertEquals(testDataWithExpResult.expectedResult, result)
    }

    @Test
    fun testOnSmsFromNumberIgnoredOnlyCall_FALSE(){

        val testDataWithExpResult = genereateSmsFromNumberWithIgnoredOnlyOnCallInCurrentTimeAndIgnoreHidden()
        whenever(mockTimeFormatUtils.currentWeekdayAsActivityIntervalFormat())
                .thenReturn(testDataWithExpResult.testCurrentWeekday)

        val testObserver = testUseCase.build(testDataWithExpResult.testNumber,
                testDataWithExpResult.testIsSms,
                testDataWithExpResult.testIgnoredSettingsByNumbers,
                testDataWithExpResult.testIgnoreHiddenNumbers)
                .test()

        testObserver.assertComplete()
        val result = testObserver.events[0][0] as Boolean
        Assert.assertEquals(testDataWithExpResult.expectedResult, result)
    }

    private fun genereateSmsFromNumberWithIgnoredOnSmsInAnotherTimeAndIgnoreHidden() : TestDataWithExpectedRes{
        val testNumber = "123"
        val testIsSms = true
        val testCurrentWeekdayId = 2
        val testBeginTimeInCurrentDay = LocalTime(4, 0)
        val testEndTimeInCurrentDay = LocalTime(5, 0)
        val testIgnoreSettingsByNumbers = HashMap<String, TimeAndIgnoreSettingsByWeekdayId>().apply {
            val timeAndIgnoreSettingsByWeekdayId = TimeAndIgnoreSettingsByWeekdayId().apply {
                put(testCurrentWeekdayId, ActivityTimeIntervalWithIgnoreSettings(false,
                        true,
                        testBeginTimeInCurrentDay,
                        testEndTimeInCurrentDay))
            }
            put(testNumber, timeAndIgnoreSettingsByWeekdayId)
        }
        return TestDataWithExpectedRes(testNumber = testNumber,
                testIsSms = testIsSms,
                testIgnoredSettingsByNumbers = testIgnoreSettingsByNumbers,
                testIgnoreHiddenNumbers = true,
                testCurrentWeekday = testCurrentWeekdayId,
                testBeginTimeForCheckInterval = testBeginTimeInCurrentDay,
                testEndTimeForCheckInterval = testEndTimeInCurrentDay,
                testIsCurrentTimeInInterval = false,
                expectedResult = false)
    }

    private fun genereateSmsFromNumberWithIgnoredOnSmsInCurrentTimeAndIgnoreHidden() : TestDataWithExpectedRes{
        val testNumber = "123"
        val testIsSms = true
        val testCurrentWeekdayId = 2
        val testBeginTimeInCurrentDay = LocalTime(4, 0)
        val testEndTimeInCurrentDay = LocalTime(5, 0)
        val testIgnoreSettingsByNumbers = HashMap<String, TimeAndIgnoreSettingsByWeekdayId>().apply {
            val timeAndIgnoreSettingsByWeekdayId = TimeAndIgnoreSettingsByWeekdayId().apply {
                put(testCurrentWeekdayId, ActivityTimeIntervalWithIgnoreSettings(false,
                        true,
                        testBeginTimeInCurrentDay,
                        testEndTimeInCurrentDay))
            }
            put(testNumber, timeAndIgnoreSettingsByWeekdayId)
        }
        return TestDataWithExpectedRes(testNumber = testNumber,
                testIsSms = testIsSms,
                testIgnoredSettingsByNumbers = testIgnoreSettingsByNumbers,
                testIgnoreHiddenNumbers = true,
                testCurrentWeekday = testCurrentWeekdayId,
                testBeginTimeForCheckInterval = testBeginTimeInCurrentDay,
                testEndTimeForCheckInterval = testEndTimeInCurrentDay,
                testIsCurrentTimeInInterval = true,
                expectedResult = true)
    }

    private fun genereateSmsFromNumberNotIgnoredAndIgnoreHidden() : TestDataWithExpectedRes{
        val testInpNumber = "123"
        val testIgnoreSettingNumber = "456"
        val testIsSms = true
        val testCurrentWeekdayId = 2
        val testBeginTimeInCurrentDay = LocalTime(4, 0)
        val testEndTimeInCurrentDay = LocalTime(5, 0)
        val testIgnoreSettingsByNumbers = HashMap<String, TimeAndIgnoreSettingsByWeekdayId>().apply {
            val timeAndIgnoreSettingsByWeekdayId = TimeAndIgnoreSettingsByWeekdayId().apply {
                put(testCurrentWeekdayId, ActivityTimeIntervalWithIgnoreSettings(false,
                        true,
                        testBeginTimeInCurrentDay,
                        testEndTimeInCurrentDay))
            }
            put(testIgnoreSettingNumber, timeAndIgnoreSettingsByWeekdayId)
        }
        return TestDataWithExpectedRes(testNumber = testInpNumber,
                testIsSms = testIsSms,
                testIgnoredSettingsByNumbers = testIgnoreSettingsByNumbers,
                testIgnoreHiddenNumbers = true,
                testCurrentWeekday = testCurrentWeekdayId,
                testBeginTimeForCheckInterval = testBeginTimeInCurrentDay,
                testEndTimeForCheckInterval = testEndTimeInCurrentDay,
                testIsCurrentTimeInInterval = true,
                expectedResult = false)
    }


    private fun genereateSmsFromNumberWithIgnoredOnlyOnCallInCurrentTimeAndIgnoreHidden() : TestDataWithExpectedRes{
        val testNumber = "123"
        val testIsSms = true
        val testCurrentWeekdayId = 2
        val testBeginTimeInCurrentDay = LocalTime(4, 0)
        val testEndTimeInCurrentDay = LocalTime(5, 0)
        val testIgnoreSettingsByNumbers = HashMap<String, TimeAndIgnoreSettingsByWeekdayId>().apply {
            val timeAndIgnoreSettingsByWeekdayId = TimeAndIgnoreSettingsByWeekdayId().apply {
                put(testCurrentWeekdayId, ActivityTimeIntervalWithIgnoreSettings(true,
                        false,
                        testBeginTimeInCurrentDay,
                        testEndTimeInCurrentDay))
            }
            put(testNumber, timeAndIgnoreSettingsByWeekdayId)
        }
        return TestDataWithExpectedRes(testNumber = testNumber,
                testIsSms = testIsSms,
                testIgnoredSettingsByNumbers = testIgnoreSettingsByNumbers,
                testIgnoreHiddenNumbers = true,
                testCurrentWeekday = testCurrentWeekdayId,
                testBeginTimeForCheckInterval = testBeginTimeInCurrentDay,
                testEndTimeForCheckInterval = testEndTimeInCurrentDay,
                testIsCurrentTimeInInterval = true,
                expectedResult = false)
    }

    private data class TestDataWithExpectedRes(val testNumber: String?,
                                               val testIsSms: Boolean,
                                               val testIgnoredSettingsByNumbers: HashMap<String, TimeAndIgnoreSettingsByWeekdayId>,
                                               val testIgnoreHiddenNumbers: Boolean,
                                               val testCurrentWeekday: Int,
                                               val testBeginTimeForCheckInterval: LocalTime,
                                               val testEndTimeForCheckInterval: LocalTime,
                                               val testIsCurrentTimeInInterval: Boolean,
                                               val expectedResult: Boolean)
}