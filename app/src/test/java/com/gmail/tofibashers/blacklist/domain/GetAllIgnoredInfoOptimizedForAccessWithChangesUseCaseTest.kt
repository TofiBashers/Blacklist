package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.RxSchedulersOverrideRule
import com.gmail.tofibashers.blacklist.TimeAndIgnoreSettingsByWeekdayId
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistItemWithActivityIntervalsRepository
import com.gmail.tofibashers.blacklist.data.repo.IPreferencesData
import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.ActivityTimeIntervalWithIgnoreSettings
import com.gmail.tofibashers.blacklist.entity.ActivityTimeIntervalWithIgnoreSettingsFactory
import com.gmail.tofibashers.blacklist.entity.BlacklistItemWithActivityIntervals
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


/**
 * Created by TofiBashers on 12.02.2018.
 */
@RunWith(MockitoJUnitRunner::class)
class GetAllIgnoredInfoOptimizedForAccessWithChangesUseCaseTest {

    @Mock
    lateinit var mockBlacklistItemWithActivityIntervalsRepository: IBlacklistItemWithActivityIntervalsRepository

    @Mock
    lateinit var mockPreferencesData: IPreferencesData

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

        whenever(mockBlacklistItemWithActivityIntervalsRepository.getAllWithChanges())
                .thenReturn(Flowable.fromIterable(testAndResData.testBlacklistItemsWithIntervalsWithChanges)
                        .compose { asNeverComplete(it) } )
        whenever(mockPreferencesData.getIgnoreHiddenNumbersWithChanges())
                .thenReturn(Flowable.fromIterable(testAndResData.testIgnoreHiddenWithChanges)
                        .compose { asNeverComplete(it) } )

        val testObserver = testUseCase.build().test()

        testObserver.assertNoErrors()

        assertSingleEventsResult(testAndResData,
                testObserver.events[0] as List<Pair<Boolean, HashMap<String, TimeAndIgnoreSettingsByWeekdayId>>>)
    }

    @Test
    fun testOnNonEmptyIntervalsAndIgnoreHiddenTrue_ValidPair(){

        val testAndResData = generateNonEmptyIntervalsWithIgnoreHidden()
        whenever(mockBlacklistItemWithActivityIntervalsRepository.getAllWithChanges())
                .thenReturn(Flowable.fromIterable(testAndResData.testBlacklistItemsWithIntervalsWithChanges)
                        .compose { asNeverComplete(it) } )
        whenever(mockPreferencesData.getIgnoreHiddenNumbersWithChanges())
                .thenReturn(Flowable.fromIterable(testAndResData.testIgnoreHiddenWithChanges)
                        .compose{ asNeverComplete(it) })
        doAnswer { ActivityTimeIntervalWithIgnoreSettings(it.arguments[0] as Boolean,
                it.arguments[1] as Boolean,
                it.arguments[2] as LocalTime,
                it.arguments[3] as LocalTime)}
                .whenever(mockTimeIntervalWithIgnoreSettingsFactory).create(any(), any(), any(), any())

        val testObserver = testUseCase.build().test()

        testObserver.assertNoErrors()
        assertSingleEventsResult(testAndResData,
                testObserver.events[0] as List<Pair<Boolean, HashMap<String, TimeAndIgnoreSettingsByWeekdayId>>>)
    }

    private fun assertSingleEventsResult(expResData: TestDataWithExpectedRes,
                                        resPairEvents: List<Pair<Boolean, HashMap<String, TimeAndIgnoreSettingsByWeekdayId>>>){
        Assert.assertEquals(expResData.resPairEvents.size, resPairEvents.size)
        val expResultPair = expResData.resPairEvents[0]
        val resultPair = resPairEvents[0]
        Assert.assertEquals(expResultPair.first, resultPair.first)
        Assert.assertEquals(expResultPair.second, resultPair.second)
    }

    private fun generateEmptyIntervalsWithNotIgnoreHidden() : TestDataWithExpectedRes {
        return TestDataWithExpectedRes(listOf(false),
                listOf(emptyList()),
                listOf(Pair(false, HashMap())))
    }

    private fun generateNonEmptyIntervalsWithIgnoreHidden() : TestDataWithExpectedRes {
        val resNumbers = listOf("123", "456")
        val resWeekdayIdsOfNums = listOf(listOf(1, 3), listOf(2, 5))
        val resBeginTime = LocalTime.MIDNIGHT
        val resEndTime = LocalTime(23, 59, 59, 999)
        val resIsCallsBlocked = true
        val resIsSmsBlocked = true
        val resActivityTimeIntervalWithIgnoreSettings = ActivityTimeIntervalWithIgnoreSettings(resIsCallsBlocked,
                resIsSmsBlocked, resBeginTime, resEndTime)

        val resHashMap = HashMap<String, TimeAndIgnoreSettingsByWeekdayId>().apply {
            for((resInd, resNum) in resNumbers.withIndex()){
                val resTimeAndIgnoreSettingsByWeekdayId = TimeAndIgnoreSettingsByWeekdayId().apply {
                    val weekdayIdsForNum = resWeekdayIdsOfNums[resInd]
                    for(resWeekdayId in weekdayIdsForNum){
                        put(resWeekdayId, resActivityTimeIntervalWithIgnoreSettings)
                    }
                }
                put(resNum, resTimeAndIgnoreSettingsByWeekdayId)
            }
        }

        val testIntervalsForItem: List<List<ActivityInterval>> = resWeekdayIdsOfNums.map { listOfNum: List<Int> ->
            listOfNum.map{ weekdayId: Int ->
                ActivityInterval(weekDayId = weekdayId,
                        beginTime = resBeginTime,
                        endTime = resEndTime)
            }
        }

        val testItemsWithIntervals = testIntervalsForItem.mapIndexed { index, list ->
            BlacklistItemWithActivityIntervals(number = resNumbers[index],
                    isCallsBlocked = resIsCallsBlocked,
                    isSmsBlocked = resIsSmsBlocked,
                    activityIntervals = list)
        }
        return TestDataWithExpectedRes(listOf(true),
                listOf(testItemsWithIntervals),
                listOf(Pair(true, resHashMap)))
    }

    private fun <T> asNeverComplete(source: Flowable<T>) : Flowable<T> = Flowable.concat(source, Flowable.never())

    private data class TestDataWithExpectedRes(val testIgnoreHiddenWithChanges: List<Boolean>,
                                               val testBlacklistItemsWithIntervalsWithChanges: List<List<BlacklistItemWithActivityIntervals>>,
                                               val resPairEvents: List<Pair<Boolean, HashMap<String, TimeAndIgnoreSettingsByWeekdayId>>>)
}