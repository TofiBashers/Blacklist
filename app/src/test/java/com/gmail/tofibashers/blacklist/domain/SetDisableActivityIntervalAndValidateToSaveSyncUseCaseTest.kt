package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.MutableActivityIntervalsWithEnableAndValidState
import junit.framework.Assert
import org.joda.time.LocalTime
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner


/**
 * Created by TofiBashers on 24.02.2018.
 */
@RunWith(MockitoJUnitRunner::class)
class SetDisableActivityIntervalAndValidateToSaveSyncUseCaseTest {

    @InjectMocks
    lateinit var testUseCase: SetDisableActivityIntervalAndValidateToSaveSyncUseCase

    @Test
    fun testOnSingleEnabledSetDisabled_IntervalsInvalidToSave(){

        val testDataWithExpRes = generateSingleEnabledWithDisabledRes()

        val testObserver = testUseCase.build(testDataWithExpRes.testIndexToDisable, testDataWithExpRes.testInputIntervals)
                .test()

        testObserver.assertComplete()
        val resultIntervals = testObserver.events[0][0] as MutableActivityIntervalsWithEnableAndValidState

        Assert.assertEquals(testDataWithExpRes.expResIntervals, resultIntervals)
    }

    @Test
    fun testOnOneOfMultipleEnabledSetDisabled_IntervalsValidToSave(){

        val testDataWithExpRes = generateMultipleEnabled()

        val testObserver = testUseCase.build(testDataWithExpRes.testIndexToDisable, testDataWithExpRes.testInputIntervals)
                .test()

        testObserver.assertComplete()
        val resultIntervals = testObserver.events[0][0] as MutableActivityIntervalsWithEnableAndValidState

        Assert.assertEquals(testDataWithExpRes.expResIntervals, resultIntervals)
    }

    private fun generateSingleEnabledWithDisabledRes() : TestDataWithExpectedRes{
        val testIndexToDisable = 5
        val testBeginTime = LocalTime.MIDNIGHT
        val testEndTime = LocalTime.MIDNIGHT

        val testIntervalsWithEnabled = mutableListOf<Pair<Boolean, ActivityInterval>>().apply {
            for((index, id) in IntRange(1, 7).withIndex()){
                val isEnabled = index == testIndexToDisable
                add(Pair(isEnabled, ActivityInterval(weekDayId = id, beginTime = testBeginTime, endTime = testEndTime)))
            }
        }

        val expResIntervalsWithEnabled = mutableListOf<Pair<Boolean, ActivityInterval>>().apply {
            for(id in IntRange(1, 7)){
                add(Pair(false, ActivityInterval(weekDayId = id, beginTime = testBeginTime, endTime = testEndTime)))
            }
        }

        return TestDataWithExpectedRes(testIndexToDisable = testIndexToDisable,
                testInputIntervals = MutableActivityIntervalsWithEnableAndValidState(true, testIntervalsWithEnabled),
                expResIntervals = MutableActivityIntervalsWithEnableAndValidState(false, expResIntervalsWithEnabled))
    }

    private fun generateMultipleEnabled() : TestDataWithExpectedRes{
        val enabledInds = listOf(5, 6)
        val testIndexToDisable = 5
        val testBeginTime = LocalTime.MIDNIGHT
        val testEndTime = LocalTime.MIDNIGHT

        val testIntervalsWithEnabled = mutableListOf<Pair<Boolean, ActivityInterval>>().apply {
            for((index, id) in IntRange(1, 7).withIndex()){
                val isEnabled = enabledInds.contains(index)
                add(Pair(isEnabled, ActivityInterval(weekDayId = id, beginTime = testBeginTime, endTime = testEndTime)))
            }
        }

        val expResIntervalsWithEnabled = mutableListOf<Pair<Boolean, ActivityInterval>>().apply {
            for((index, id) in IntRange(1, 7).withIndex()){
                val isEnabled = index == enabledInds[1]
                add(Pair(isEnabled, ActivityInterval(weekDayId = id, beginTime = testBeginTime, endTime = testEndTime)))
            }
        }

        return TestDataWithExpectedRes(testIndexToDisable = testIndexToDisable,
                testInputIntervals = MutableActivityIntervalsWithEnableAndValidState(true, testIntervalsWithEnabled),
                expResIntervals = MutableActivityIntervalsWithEnableAndValidState(true, expResIntervalsWithEnabled))
    }

    private data class TestDataWithExpectedRes(val testIndexToDisable: Int,
                                               val testInputIntervals : MutableActivityIntervalsWithEnableAndValidState,
                                               val expResIntervals: MutableActivityIntervalsWithEnableAndValidState)
}