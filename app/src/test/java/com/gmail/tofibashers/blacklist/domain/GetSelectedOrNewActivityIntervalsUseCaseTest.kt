package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.RxSchedulersOverrideRule
import com.gmail.tofibashers.blacklist.data.repo.IActivityIntervalRepository
import com.gmail.tofibashers.blacklist.data.repo.IInteractionModeRepository
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.utils.TimeFormatUtils
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Maybe
import junit.framework.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


/**
 * Created by TofiBashers on 22.02.2018.
 */
@RunWith(MockitoJUnitRunner::class)
class GetSelectedOrNewActivityIntervalsUseCaseTest {

    @Mock
    lateinit var mockActivityIntervalRepository: IActivityIntervalRepository

    @Mock
    lateinit var mockInteractionModeRepository: IInteractionModeRepository

    @Mock
    lateinit var mockActivityIntervalFactory: ActivityIntervalFactory

    @Mock
    lateinit var mockIntervalsWithStateFactory: MutableActivityIntervalsWithEnableAndValidStateFactory

    @Mock
    lateinit var timeFormatUtils: TimeFormatUtils

    @InjectMocks
    lateinit var testUseCase: GetSelectedOrNewActivityIntervalsUseCase

    @Rule
    @JvmField
    val schedulersRule = RxSchedulersOverrideRule()

    @Test
    fun testNonSelectedInteractionMode_RuntimeException(){

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(Maybe.empty())

        val testObserver = testUseCase.build().test()

        testObserver.assertError(RuntimeException::class.java)
    }

    @Test
    fun testEditInteractionModeEmptySelectedIntervals_RuntimeException(){

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(Maybe.just(InteractionMode.EDIT))
        whenever(mockActivityIntervalRepository.getSelectedActivityIntervals())
                .thenReturn(Maybe.empty())

        val testObserver = testUseCase.build().test()

        testObserver.assertError(RuntimeException::class.java)
    }

    @Test
    fun testOnNotAllDaysActivityIntervalsEnabled_IntervalsWithAddedDefaultsAndValidToSave(){

        val testDataWithExpectedRes = generateEditModeWithNotAllSelectedDaysEnabled()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(testDataWithExpectedRes.testInteractionModeMaybe)
        whenever(mockActivityIntervalRepository.getSelectedActivityIntervals())
                .thenReturn(testDataWithExpectedRes.testSelectedActivityIntervalsMaybe)
        whenever(timeFormatUtils.getWeekdayIdsInLocalizedOrder())
                .thenReturn(testDataWithExpectedRes.testLocalizedOrderWeekdayIds)

        doAnswer { ActivityInterval(it.arguments[0] as Int)}
                .whenever(mockActivityIntervalFactory).create(any())

        doAnswer { MutableActivityIntervalsWithEnableAndValidState(it.arguments[0] as Boolean,
                it.arguments[1] as MutableList<Pair<Boolean, ActivityInterval>>)}
                .whenever(mockIntervalsWithStateFactory).create(any(), any())

        val testObserver = testUseCase.build().test()

        testObserver.assertComplete()
        val resIntervals = testObserver.events[0][0] as MutableActivityIntervalsWithEnableAndValidState
        Assert.assertEquals(testDataWithExpectedRes.expResIntervals, resIntervals)
    }

    @Test
    fun testOnAllDaysActivityIntervalsEnabled_IntervalsEnabledAndValidToSave(){

        val testDataWithExpectedRes = generateEditModeWithAllSelectedDaysEnabled()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(testDataWithExpectedRes.testInteractionModeMaybe)
        whenever(mockActivityIntervalRepository.getSelectedActivityIntervals())
                .thenReturn(testDataWithExpectedRes.testSelectedActivityIntervalsMaybe)
        whenever(timeFormatUtils.getWeekdayIdsInLocalizedOrder())
                .thenReturn(testDataWithExpectedRes.testLocalizedOrderWeekdayIds)

        doAnswer { MutableActivityIntervalsWithEnableAndValidState(it.arguments[0] as Boolean,
                it.arguments[1] as MutableList<Pair<Boolean, ActivityInterval>>)}
                .whenever(mockIntervalsWithStateFactory).create(any(), any())

        val testObserver = testUseCase.build().test()

        testObserver.assertComplete()
        val resIntervals = testObserver.events[0][0] as MutableActivityIntervalsWithEnableAndValidState
        Assert.assertEquals(testDataWithExpectedRes.expResIntervals, resIntervals)
    }

    @Test
    fun testActvitiyIntervalsNotSelected_CreatedDefaultIntervalsWithEnableAndValidToSave(){

        val testDataWithExpectedRes = generateCreatedDefaultIntervals()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(testDataWithExpectedRes.testInteractionModeMaybe)
        whenever(mockActivityIntervalRepository.getSelectedActivityIntervals())
                .thenReturn(testDataWithExpectedRes.testSelectedActivityIntervalsMaybe)
        whenever(timeFormatUtils.getWeekdayIdsInLocalizedOrder())
                .thenReturn(testDataWithExpectedRes.testLocalizedOrderWeekdayIds)

        doAnswer { ActivityInterval(it.arguments[0] as Int)}
                .whenever(mockActivityIntervalFactory).create(any())

        doAnswer { MutableActivityIntervalsWithEnableAndValidState(it.arguments[0] as Boolean,
                it.arguments[1] as MutableList<Pair<Boolean, ActivityInterval>>)}
                .whenever(mockIntervalsWithStateFactory).create(any(), any())

        val testObserver = testUseCase.build().test()

        testObserver.assertComplete()
        val resIntervals = testObserver.events[0][0] as MutableActivityIntervalsWithEnableAndValidState
        Assert.assertEquals(testDataWithExpectedRes.expResIntervals, resIntervals)
    }

    private fun generateEditModeWithNotAllSelectedDaysEnabled() : TestDataWithExpectedRes {

        val resBeginTime = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME.plusHours(1)
        val resEndTime = TimeFormatUtils.DAY_END_ISO_UNZONED_TIME.minusHours(1)
        val resBeginTimeDefault = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME
        val resEndTimeDefault = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME

        val testLocalizedOrderWeekdayIds = IntRange(1, 7).toList()
        val testSelectedActivityIntervals  = mutableListOf<ActivityInterval>().apply {
            add(ActivityInterval(null, 3, resBeginTime, resEndTime))
            add(ActivityInterval(null, 1, resBeginTime, resEndTime))
            add(ActivityInterval(null, 5, resBeginTime, resEndTime))
        }

        val resIntervalsWithEnabled = mutableListOf<Pair<Boolean, ActivityInterval>>().apply {
            add(Pair(true, ActivityInterval(null, 1, resBeginTime, resEndTime)))
            add(Pair(false, ActivityInterval(null, 2, resBeginTimeDefault, resEndTimeDefault)))
            add(Pair(true, ActivityInterval(null, 3, resBeginTime, resEndTime)))
            add(Pair(false, ActivityInterval(null, 4, resBeginTimeDefault, resEndTimeDefault)))
            add(Pair(true, ActivityInterval(null, 5, resBeginTime, resEndTime)))
            add(Pair(false, ActivityInterval(null, 6, resBeginTimeDefault, resEndTimeDefault)))
            add(Pair(false, ActivityInterval(null, 7, resBeginTimeDefault, resEndTimeDefault)))
        }
        return TestDataWithExpectedRes(Maybe.just(InteractionMode.EDIT),
                Maybe.just(testSelectedActivityIntervals),
                testLocalizedOrderWeekdayIds,
                MutableActivityIntervalsWithEnableAndValidState(true, resIntervalsWithEnabled))
    }

    private fun generateEditModeWithAllSelectedDaysEnabled() : TestDataWithExpectedRes {

        val resBeginTime = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME.plusHours(1)
        val resEndTime = TimeFormatUtils.DAY_END_ISO_UNZONED_TIME.minusHours(1)

        val testLocalizedOrderWeekdayIds = IntRange(1, 7).toList()
        val testSelectedActivityIntervals  = mutableListOf<ActivityInterval>().apply {
            for(weekdayId in testLocalizedOrderWeekdayIds){
                add(ActivityInterval(null, weekdayId, resBeginTime, resEndTime))
            }
        }

        val resIntervalsWithEnabled = mutableListOf<Pair<Boolean, ActivityInterval>>().apply {
            for(weekdayId in testLocalizedOrderWeekdayIds){
                add(Pair(true, ActivityInterval(null, weekdayId, resBeginTime, resEndTime)))
            }
        }
        return TestDataWithExpectedRes(Maybe.just(InteractionMode.EDIT),
                Maybe.just(testSelectedActivityIntervals),
                testLocalizedOrderWeekdayIds,
                MutableActivityIntervalsWithEnableAndValidState(true, resIntervalsWithEnabled))
    }

    private fun generateCreatedDefaultIntervals() : TestDataWithExpectedRes {

        val resBeginTimeDefault = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME
        val resEndTimeDefault = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME

        val testLocalizedOrderWeekdayIds = IntRange(1, 7).toList()

        val resIntervalsWithEnabled = mutableListOf<Pair<Boolean, ActivityInterval>>().apply {
            for(weekdayId in testLocalizedOrderWeekdayIds){
                add(Pair(true, ActivityInterval(null, weekdayId, resBeginTimeDefault, resEndTimeDefault)))
            }
        }
        return TestDataWithExpectedRes(Maybe.just(InteractionMode.CREATE),
                Maybe.empty(),
                testLocalizedOrderWeekdayIds,
                MutableActivityIntervalsWithEnableAndValidState(true, resIntervalsWithEnabled))
    }

    private data class TestDataWithExpectedRes(val testInteractionModeMaybe: Maybe<InteractionMode>,
                                               val testSelectedActivityIntervalsMaybe : Maybe<List<ActivityInterval>>,
                                               val testLocalizedOrderWeekdayIds : List<Int>,
                                               val expResIntervals: MutableActivityIntervalsWithEnableAndValidState)
}