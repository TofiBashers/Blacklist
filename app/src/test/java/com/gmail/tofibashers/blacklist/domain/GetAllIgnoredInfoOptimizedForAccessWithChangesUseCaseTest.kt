package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.RxSchedulersOverrideRule
import com.gmail.tofibashers.blacklist.TimeAndIgnoreSettingsByWeekdayId
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistContactItemWithPhonesAndActivityIntervalsRepository
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistPhoneNumberItemWithActivityIntervalsRepository
import com.gmail.tofibashers.blacklist.data.repo.IDeviceData
import com.gmail.tofibashers.blacklist.data.repo.IPreferencesData
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.utils.PhoneNumberFormatUtils
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Flowable
import org.joda.time.LocalTime
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*


/**
 * Created by TofiBashers on 12.02.2018.
 */
@RunWith(MockitoJUnitRunner::class)
class GetAllIgnoredInfoOptimizedForAccessWithChangesUseCaseTest {

    @Mock
    lateinit var mockBlacklistPhoneNumberItemWithActivityIntervalsRepository: IBlacklistPhoneNumberItemWithActivityIntervalsRepository

    @Mock
    lateinit var mockBlacklistContactItemWithPhonesAndActivityIntervalsRepository: IBlacklistContactItemWithPhonesAndActivityIntervalsRepository

    @Mock
    lateinit var mockPreferencesData: IPreferencesData

    @Mock
    lateinit var mockDeviceData: IDeviceData

    @Mock
    lateinit var mockPhoneNumberFormatUtils: PhoneNumberFormatUtils

    @Mock
    lateinit var mockTimeIntervalWithIgnoreSettingsFactory: ActivityTimeIntervalWithIgnoreSettingsFactory

    @InjectMocks
    lateinit var testUseCase: GetAllIgnoredInfoOptimizedForAccessWithChangesUseCase

    @Rule
    @JvmField
    val schedulersRule = RxSchedulersOverrideRule()

    @Test
    fun testOnEmptyIntervalsAndIgnoreHiddenFalse_PairWithFalseAndEmpty(){

        val testAndResData = generateEmptyIntervalsWithNotIgnoreHidden()

        whenever(mockBlacklistPhoneNumberItemWithActivityIntervalsRepository.getAllWithChanges())
                .thenReturn(Flowable.fromIterable(testAndResData.testBlacklistItemsWithIntervalsWithChanges)
                        .compose { asNeverComplete(it) } )
        whenever(mockBlacklistContactItemWithPhonesAndActivityIntervalsRepository.getAllWithChanges())
                .thenReturn(Flowable.fromIterable(testAndResData.testContactsWithPhonesAndIntervals)
                        .compose { asNeverComplete(it) } )
        whenever(mockPreferencesData.getIgnoreHiddenNumbersWithChanges())
                .thenReturn(Flowable.fromIterable(testAndResData.testIgnoreHiddenWithChanges)
                        .compose { asNeverComplete(it) } )
        whenever(mockDeviceData.getCelluarNetworkOrLocalCountryCodeWithChanges())
                .thenReturn(testAndResData.testFlowableCelluarNetworkOrCountryCodes
                        .compose { asNeverComplete(it)})

        val testObserver = testUseCase.build().test()

        testObserver.assertNoErrors()

        assertSingleEventsResult(testAndResData,
                testObserver.events[0] as List<Triple<Boolean, String, HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>>>)
    }

    @Test
    fun testOnNonEmptyIntervalsAndIgnoreHiddenTrue_ValidPair(){

        val testAndResData = generateNonEmptyIntervalsWithIgnoreHidden()
        whenever(mockBlacklistPhoneNumberItemWithActivityIntervalsRepository.getAllWithChanges())
                .thenReturn(Flowable.fromIterable(testAndResData.testBlacklistItemsWithIntervalsWithChanges)
                        .compose { asNeverComplete(it) } )
        whenever(mockBlacklistContactItemWithPhonesAndActivityIntervalsRepository.getAllWithChanges())
                .thenReturn(Flowable.fromIterable(testAndResData.testContactsWithPhonesAndIntervals)
                        .compose { asNeverComplete(it) } )
        whenever(mockPreferencesData.getIgnoreHiddenNumbersWithChanges())
                .thenReturn(Flowable.fromIterable(testAndResData.testIgnoreHiddenWithChanges)
                        .compose{ asNeverComplete(it) })
        whenever(mockDeviceData.getCelluarNetworkOrLocalCountryCodeWithChanges())
                .thenReturn(testAndResData.testFlowableCelluarNetworkOrCountryCodes)
        doAnswer { invocationOnMock ->
            val numberStr = invocationOnMock.getArgument<String>(0)
            val pairNumberStrWithPhoneNumerType = testAndResData.testPhoneNumberTypeWithValues.find {
                it.first == numberStr
            }
            return@doAnswer pairNumberStrWithPhoneNumerType!!.second
        }
                .whenever(mockPhoneNumberFormatUtils).toPhoneNumberTypeWithValue(any(), any())
        doAnswer { ActivityTimeIntervalWithIgnoreSettings(it.arguments[0] as Boolean,
                it.arguments[1] as Boolean,
                it.arguments[2] as LocalTime,
                it.arguments[3] as LocalTime)}
                .whenever(mockTimeIntervalWithIgnoreSettingsFactory).create(any(), any(), any(), any())

        val testObserver = testUseCase.build().test()

        testObserver.assertNoErrors()
        assertSingleEventsResult(testAndResData,
                testObserver.events[0] as List<Triple<Boolean, String, HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>>>)
    }

    private fun assertSingleEventsResult(expResData: TestDataWithExpectedRes,
                                         resTripleEvents: List<Triple<Boolean, String, HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>>>){
        Assert.assertEquals(expResData.resTripleEvents.size, resTripleEvents.size)
        val expResultTriple = expResData.resTripleEvents[0]
        val resultTriple = resTripleEvents[0]
        Assert.assertEquals(expResultTriple, resultTriple)
    }

    private fun generateEmptyIntervalsWithNotIgnoreHidden() : TestDataWithExpectedRes {
        return TestDataWithExpectedRes(
                testIgnoreHiddenWithChanges = listOf(false),
                testBlacklistItemsWithIntervalsWithChanges = listOf(emptyList()),
                testContactsWithPhonesAndIntervals = listOf(emptyList()),
                testPhoneNumberTypeWithValues = emptyList(),
                testFlowableCelluarNetworkOrCountryCodes = Flowable.just("Ru"),
                resTripleEvents = listOf(Triple(false, "Ru", HashMap())))
    }

    private fun generateNonEmptyIntervalsWithIgnoreHidden() : TestDataWithExpectedRes {
        val resPhoneNumbers = listOf("123", "456")
        val resConvertedPhoneNumbers = listOf(Pair("123", PhoneNumberTypeWithValue.Invalid("123")),
                Pair("456", PhoneNumberTypeWithValue.Invalid("456")))
        val resContactPhoneNumbers = listOf("5535", "9994")
        val resConvertedContactPhoneNumbers = listOf(Pair("5535", PhoneNumberTypeWithValue.Invalid("5535")),
                Pair("9994", PhoneNumberTypeWithValue.Invalid("9994")))

        val testConvertedAllPhoneNumbers = mutableListOf<Pair<String, PhoneNumberTypeWithValue>>()
        testConvertedAllPhoneNumbers.addAll(resConvertedContactPhoneNumbers)
        testConvertedAllPhoneNumbers.addAll(resConvertedPhoneNumbers)

        val resWeekdayIdsOfPhoneNums = listOf(listOf(1, 3), listOf(2, 5))
        val resWeekdayIdsOfContactPhoneNums = listOf(listOf(2, 4, 7), listOf(1))
        val resContactNames = listOf("contact1", "contact2")
        val resCountryCode = "RU"
        val resBeginTime = LocalTime.MIDNIGHT
        val resEndTime = LocalTime(23, 59, 59, 999)
        val resIsCallsBlocked = true
        val resIsSmsBlocked = true
        val resActivityTimeIntervalWithIgnoreSettings = ActivityTimeIntervalWithIgnoreSettings(resIsCallsBlocked,
                resIsSmsBlocked, resBeginTime, resEndTime)

        val resHashMap = HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>().apply {
            for((resInd, resConvNum) in resConvertedPhoneNumbers.withIndex()){
                val resTimeAndIgnoreSettingsByWeekdayId = TimeAndIgnoreSettingsByWeekdayId().apply {
                    val weekdayIdsForNum = resWeekdayIdsOfPhoneNums[resInd]
                    for(resWeekdayId in weekdayIdsForNum){
                        put(resWeekdayId, resActivityTimeIntervalWithIgnoreSettings)
                    }
                }
                put(resConvNum.second, resTimeAndIgnoreSettingsByWeekdayId)
            }
            for((resInd, resConvNum) in resConvertedContactPhoneNumbers.withIndex()){
                val resTimeAndIgnoreSettingsByWeekdayId = TimeAndIgnoreSettingsByWeekdayId().apply {
                    val weekdayIdsForNum = resWeekdayIdsOfContactPhoneNums[resInd]
                    for(resWeekdayId in weekdayIdsForNum){
                        put(resWeekdayId, resActivityTimeIntervalWithIgnoreSettings)
                    }
                }
                put(resConvNum.second, resTimeAndIgnoreSettingsByWeekdayId)
            }
        }

        val testIntervalsForItem: List<List<ActivityInterval>> = resWeekdayIdsOfPhoneNums.map { listOfNum: List<Int> ->
            listOfNum.map{ weekdayId: Int ->
                ActivityInterval(weekDayId = weekdayId,
                        beginTime = resBeginTime,
                        endTime = resEndTime)
            }
        }

        val testIntervalsForContactItemsWithSinglePhones: List<List<ActivityInterval>> =
                resWeekdayIdsOfContactPhoneNums.map { listOfNum: List<Int> ->
                    listOfNum.map{ weekdayId: Int ->
                        ActivityInterval(weekDayId = weekdayId,
                                beginTime = resBeginTime,
                                endTime = resEndTime)
                    }
        }

        val testPhoneItemsWithIntervals = testIntervalsForItem.mapIndexed { index, list ->
            BlacklistPhoneNumberItemWithActivityIntervals(number = resPhoneNumbers[index],
                    isCallsBlocked = resIsCallsBlocked,
                    isSmsBlocked = resIsSmsBlocked,
                    activityIntervals = list)
        }

        val testPhoneNumbersWithValues = testIntervalsForItem.mapIndexed { index, list ->
            BlacklistPhoneNumberItemWithActivityIntervals(number = resPhoneNumbers[index],
                    isCallsBlocked = resIsCallsBlocked,
                    isSmsBlocked = resIsSmsBlocked,
                    activityIntervals = list)
        }

        val testContactItemsWithIntervals = testIntervalsForContactItemsWithSinglePhones.mapIndexed { index, list ->
            BlacklistContactItemWithPhonesAndIntervals(name = resContactNames[index],
                    photoUrl = "http://",
                    phones = listOf(BlacklistContactPhoneWithActivityIntervals(number = resContactPhoneNumbers[index],
                            isCallsBlocked = resIsCallsBlocked,
                            isSmsBlocked = resIsSmsBlocked,
                            activityIntervals = list)))
        }

        return TestDataWithExpectedRes(
                testIgnoreHiddenWithChanges = listOf(true),
                testBlacklistItemsWithIntervalsWithChanges = listOf(testPhoneItemsWithIntervals),
                testContactsWithPhonesAndIntervals = listOf(testContactItemsWithIntervals),
                testPhoneNumberTypeWithValues = testConvertedAllPhoneNumbers,
                testFlowableCelluarNetworkOrCountryCodes = Flowable.just(resCountryCode),
                resTripleEvents = listOf(Triple(true, resCountryCode, resHashMap)))
    }

    private fun <T> asNeverComplete(source: Flowable<T>) : Flowable<T> = Flowable.concat(source, Flowable.never())

    private data class TestDataWithExpectedRes(val testIgnoreHiddenWithChanges: List<Boolean>,
                                               val testBlacklistItemsWithIntervalsWithChanges: List<List<BlacklistPhoneNumberItemWithActivityIntervals>>,
                                               val testContactsWithPhonesAndIntervals: List<List<BlacklistContactItemWithPhonesAndIntervals>>,
                                               val testPhoneNumberTypeWithValues: List<Pair<String, PhoneNumberTypeWithValue>>,
                                               val testFlowableCelluarNetworkOrCountryCodes: Flowable<String>,
                                               val resTripleEvents: List<Triple<Boolean, String, HashMap<PhoneNumberTypeWithValue, TimeAndIgnoreSettingsByWeekdayId>>>)
}