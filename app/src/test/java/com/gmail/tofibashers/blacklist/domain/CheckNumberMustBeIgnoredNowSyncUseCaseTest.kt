package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.TimeAndIgnoreSettingsByWeekdayId
import com.gmail.tofibashers.blacklist.entity.ActivityTimeIntervalWithIgnoreSettings
import com.gmail.tofibashers.blacklist.entity.PhoneNumberTypeWithValue
import com.gmail.tofibashers.blacklist.utils.PhoneNumberFormatUtils
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

    @Mock
    lateinit var mockPhoneNumberFormatUtils: PhoneNumberFormatUtils

    @InjectMocks
    lateinit var testUseCase: CheckNumberMustBeIgnoredNowSyncUseCase

    @Test
    fun testOnSmsFromNullNumberWithIgnoreHidden_TRUE(){

        val expResult = true
        val testNumber = null
        val testIgnoreHiddenNumbers = true
        val testIsSms = true
        val testDefaultCountry = "RU"
        val mockIgnoreSettingsByNumbers = mock<HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>>()

        val testObserver = testUseCase.build(testNumber, testIsSms, mockIgnoreSettingsByNumbers, testDefaultCountry, testIgnoreHiddenNumbers)
                .test()

        verifyZeroInteractions(mockIgnoreSettingsByNumbers)
        verifyZeroInteractions(mockTimeFormatUtils)

        testObserver.assertComplete()
        val result = testObserver.events[0][0] as Boolean
        Assert.assertEquals(expResult, result)
    }

    @Test
    fun testOnSmsFromInvalidNumberIgnoredInCurrentDayAndAnotherTime_FALSE(){

        val testDataWithExpResult = genereateSmsFromInvalidNumberWithIgnoredOnSmsInAnotherTimeAndIgnoreHidden()
        whenever(mockPhoneNumberFormatUtils.toPhoneNumberTypeWithValue(testDataWithExpResult.testNumber!!, testDataWithExpResult.testDefaultCountry))
                .thenReturn(testDataWithExpResult.testConvertedNumberWithValue)
        whenever(mockTimeFormatUtils.currentWeekdayAsActivityIntervalFormat())
                .thenReturn(testDataWithExpResult.testCurrentWeekday)
        whenever(mockTimeFormatUtils.isCurrentTimeInInterval(testDataWithExpResult.testBeginTimeForCheckInterval, testDataWithExpResult.testEndTimeForCheckInterval))
                .thenReturn(testDataWithExpResult.testIsCurrentTimeInInterval)

        val testObserver = testUseCase.build(testDataWithExpResult.testNumber,
                testDataWithExpResult.testIsSms,
                testDataWithExpResult.testIgnoredSettingsByNumbers,
                testDataWithExpResult.testDefaultCountry,
                testDataWithExpResult.testIgnoreHiddenNumbers)
                .test()

        testObserver.assertComplete()
        val result = testObserver.events[0][0] as Boolean
        Assert.assertEquals(testDataWithExpResult.expectedResult, result)
    }

    @Test
    fun testOnSmsFromInvalidNumberIgnoredInCurrentDayAndCurrentTime_TRUE(){

        val testDataWithExpResult = genereateSmsFromInvalidNumberWithIgnoredOnSmsInCurrentTimeAndIgnoreHidden()
        whenever(mockPhoneNumberFormatUtils.toPhoneNumberTypeWithValue(testDataWithExpResult.testNumber!!, testDataWithExpResult.testDefaultCountry))
                .thenReturn(testDataWithExpResult.testConvertedNumberWithValue)
        whenever(mockTimeFormatUtils.currentWeekdayAsActivityIntervalFormat())
                .thenReturn(testDataWithExpResult.testCurrentWeekday)
        whenever(mockTimeFormatUtils.isCurrentTimeInInterval(testDataWithExpResult.testBeginTimeForCheckInterval, testDataWithExpResult.testEndTimeForCheckInterval))
                .thenReturn(testDataWithExpResult.testIsCurrentTimeInInterval)

        val testObserver = testUseCase.build(testDataWithExpResult.testNumber,
                testDataWithExpResult.testIsSms,
                testDataWithExpResult.testIgnoredSettingsByNumbers,
                testDataWithExpResult.testDefaultCountry,
                testDataWithExpResult.testIgnoreHiddenNumbers)
                .test()

        testObserver.assertComplete()
        val result = testObserver.events[0][0] as Boolean
        Assert.assertEquals(testDataWithExpResult.expectedResult, result)
    }

    @Test
    fun testOnSmsFromInvalidNumberNotIgnored_FALSE(){

        val testDataWithExpResult = genereateSmsFromInvalidNumberNotIgnoredAndIgnoreHidden()
        whenever(mockPhoneNumberFormatUtils.toPhoneNumberTypeWithValue(testDataWithExpResult.testNumber!!, testDataWithExpResult.testDefaultCountry))
                .thenReturn(testDataWithExpResult.testConvertedNumberWithValue)

        val testObserver = testUseCase.build(testDataWithExpResult.testNumber,
                testDataWithExpResult.testIsSms,
                testDataWithExpResult.testIgnoredSettingsByNumbers,
                testDataWithExpResult.testDefaultCountry,
                testDataWithExpResult.testIgnoreHiddenNumbers)
                .test()

        testObserver.assertComplete()
        val result = testObserver.events[0][0] as Boolean
        Assert.assertEquals(testDataWithExpResult.expectedResult, result)
    }

    @Test
    fun testOnSmsFromInvalidNumberIgnoredOnlyCall_FALSE(){

        val testDataWithExpResult = genereateSmsFromInvalidNumberWithIgnoredOnlyOnCallInCurrentTimeAndIgnoreHidden()
        whenever(mockPhoneNumberFormatUtils.toPhoneNumberTypeWithValue(testDataWithExpResult.testNumber!!, testDataWithExpResult.testDefaultCountry))
                .thenReturn(testDataWithExpResult.testConvertedNumberWithValue)
        whenever(mockTimeFormatUtils.currentWeekdayAsActivityIntervalFormat())
                .thenReturn(testDataWithExpResult.testCurrentWeekday)

        val testObserver = testUseCase.build(testDataWithExpResult.testNumber,
                testDataWithExpResult.testIsSms,
                testDataWithExpResult.testIgnoredSettingsByNumbers,
                testDataWithExpResult.testDefaultCountry,
                testDataWithExpResult.testIgnoreHiddenNumbers)
                .test()

        testObserver.assertComplete()
        val result = testObserver.events[0][0] as Boolean
        Assert.assertEquals(testDataWithExpResult.expectedResult, result)
    }

    private fun genereateSmsFromInvalidNumberWithIgnoredOnSmsInAnotherTimeAndIgnoreHidden() : TestDataWithExpectedRes{
        val testNumber = "123"
        val testIsSms = true
        val testDefaultCountry = "RU"
        val testCurrentWeekdayId = 2
        val testConvertedNumberWithValue = PhoneNumberTypeWithValue.Invalid(testNumber)
        val testBeginTimeInCurrentDay = LocalTime(4, 0)
        val testEndTimeInCurrentDay = LocalTime(5, 0)
        val testIgnoreSettingsByNumbers = HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>().apply {
            val timeAndIgnoreSettingsByWeekdayId = TimeAndIgnoreSettingsByWeekdayId().apply {
                put(testCurrentWeekdayId, ActivityTimeIntervalWithIgnoreSettings(false,
                        true,
                        testBeginTimeInCurrentDay,
                        testEndTimeInCurrentDay))
            }
            put(testConvertedNumberWithValue, timeAndIgnoreSettingsByWeekdayId)
        }
        return TestDataWithExpectedRes(testNumber = testNumber,
                testIsSms = testIsSms,
                testIgnoredSettingsByNumbers = testIgnoreSettingsByNumbers,
                testDefaultCountry = testDefaultCountry,
                testIgnoreHiddenNumbers = true,
                testCurrentWeekday = testCurrentWeekdayId,
                testConvertedNumberWithValue = testConvertedNumberWithValue,
                testBeginTimeForCheckInterval = testBeginTimeInCurrentDay,
                testEndTimeForCheckInterval = testEndTimeInCurrentDay,
                testIsCurrentTimeInInterval = false,
                expectedResult = false)
    }

    private fun genereateSmsFromInvalidNumberWithIgnoredOnSmsInCurrentTimeAndIgnoreHidden() : TestDataWithExpectedRes{
        val testNumber = "123"
        val testIsSms = true
        val testDefaultCountry = "RU"
        val testCurrentWeekdayId = 2
        val testConvertedNumberWithValue = PhoneNumberTypeWithValue.Invalid(testNumber)
        val testBeginTimeInCurrentDay = LocalTime(4, 0)
        val testEndTimeInCurrentDay = LocalTime(5, 0)
        val testIgnoreSettingsByNumbers = HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>().apply {
            val timeAndIgnoreSettingsByWeekdayId = TimeAndIgnoreSettingsByWeekdayId().apply {
                put(testCurrentWeekdayId, ActivityTimeIntervalWithIgnoreSettings(false,
                        true,
                        testBeginTimeInCurrentDay,
                        testEndTimeInCurrentDay))
            }
            put(testConvertedNumberWithValue, timeAndIgnoreSettingsByWeekdayId)
        }
        return TestDataWithExpectedRes(testNumber = testNumber,
                testIsSms = testIsSms,
                testIgnoredSettingsByNumbers = testIgnoreSettingsByNumbers,
                testDefaultCountry = testDefaultCountry,
                testIgnoreHiddenNumbers = true,
                testCurrentWeekday = testCurrentWeekdayId,
                testConvertedNumberWithValue = testConvertedNumberWithValue,
                testBeginTimeForCheckInterval = testBeginTimeInCurrentDay,
                testEndTimeForCheckInterval = testEndTimeInCurrentDay,
                testIsCurrentTimeInInterval = true,
                expectedResult = true)
    }

    private fun genereateSmsFromInvalidNumberNotIgnoredAndIgnoreHidden() : TestDataWithExpectedRes{
        val testInpNumber = "123"
        val testConvertedNumberWithValue = PhoneNumberTypeWithValue.Invalid(testInpNumber)
        val testIgnoreSettingNumber = PhoneNumberTypeWithValue.Invalid("456")
        val testIsSms = true
        val testDefaultCountry = "RU"
        val testCurrentWeekdayId = 2
        val testBeginTimeInCurrentDay = LocalTime(4, 0)
        val testEndTimeInCurrentDay = LocalTime(5, 0)
        val testIgnoreSettingsByNumbers = HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>().apply {
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
                testDefaultCountry = testDefaultCountry,
                testIgnoreHiddenNumbers = true,
                testCurrentWeekday = testCurrentWeekdayId,
                testConvertedNumberWithValue = testConvertedNumberWithValue,
                testBeginTimeForCheckInterval = testBeginTimeInCurrentDay,
                testEndTimeForCheckInterval = testEndTimeInCurrentDay,
                testIsCurrentTimeInInterval = true,
                expectedResult = false)
    }


    private fun genereateSmsFromInvalidNumberWithIgnoredOnlyOnCallInCurrentTimeAndIgnoreHidden() : TestDataWithExpectedRes{
        val testNumber = "123"
        val testIsSms = true
        val testDefaultCountry = "RU"
        val testCurrentWeekdayId = 2
        val testConvertedNumber = PhoneNumberTypeWithValue.Invalid("123")
        val testBeginTimeInCurrentDay = LocalTime(4, 0)
        val testEndTimeInCurrentDay = LocalTime(5, 0)
        val testIgnoreSettingsByNumbers = HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>().apply {
            val timeAndIgnoreSettingsByWeekdayId = TimeAndIgnoreSettingsByWeekdayId().apply {
                put(testCurrentWeekdayId, ActivityTimeIntervalWithIgnoreSettings(true,
                        false,
                        testBeginTimeInCurrentDay,
                        testEndTimeInCurrentDay))
            }
            put(testConvertedNumber, timeAndIgnoreSettingsByWeekdayId)
        }
        return TestDataWithExpectedRes(testNumber = testNumber,
                testIsSms = testIsSms,
                testIgnoredSettingsByNumbers = testIgnoreSettingsByNumbers,
                testDefaultCountry = testDefaultCountry,
                testIgnoreHiddenNumbers = true,
                testCurrentWeekday = testCurrentWeekdayId,
                testConvertedNumberWithValue = testConvertedNumber,
                testBeginTimeForCheckInterval = testBeginTimeInCurrentDay,
                testEndTimeForCheckInterval = testEndTimeInCurrentDay,
                testIsCurrentTimeInInterval = true,
                expectedResult = false)
    }

    private data class TestDataWithExpectedRes(val testNumber: String?,
                                               val testIsSms: Boolean,
                                               val testIgnoredSettingsByNumbers: HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>,
                                               val testDefaultCountry: String,
                                               val testIgnoreHiddenNumbers: Boolean,
                                               val testCurrentWeekday: Int,
                                               val testConvertedNumberWithValue: PhoneNumberTypeWithValue,
                                               val testBeginTimeForCheckInterval: LocalTime,
                                               val testEndTimeForCheckInterval: LocalTime,
                                               val testIsCurrentTimeInInterval: Boolean,
                                               val expectedResult: Boolean)
}