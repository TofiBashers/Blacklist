package com.gmail.tofibashers.blacklist.ui

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.*
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import com.gmail.tofibashers.blacklist.ui.time_settings.*
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.joda.time.LocalTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


/**
 * Created by TofiBashers on 01.03.2018.
 */
@RunWith(MockitoJUnitRunner::class)
class TimeSettingsViewModelTest {

    @Mock
    lateinit var mockGetSelectedActivityIntervalsUseCase: IGetSelectedOrNewActivityIntervalsUseCase

    @Mock
    lateinit var mockSelectActivityIntervalsUseCase: ISelectOnlyEnabledActivityIntervalsUseCase

    @Mock
    lateinit var mockSetDisableActivityIntervalUseCase: ISetDisableActivityIntervalAndValidateToSaveSyncUseCase

    @Mock
    lateinit var mockSetEnableActivityIntervalUseCase: ISetEnableActivityIntervalAndValidateToSaveSyncUseCase

    @Mock
    lateinit var mockCreateTimeChangeInitDataUseCase: ICreateTimeChangeInitDataUseCase

    @Mock
    lateinit var mockLoadingViewStateFactory: TimeSettingsViewState_LoadingViewStateFactory

    @Mock
    lateinit var mockDataViewStateFactory: TimeSettingsViewState_DataViewStateFactory

    @Mock
    lateinit var mockItemDetailsRouteFactory: TimeSettingsNavData_ItemDetailsRouteFactory

    @Mock
    lateinit var mockTimeChangeRouteFactory: TimeSettingsNavData_TimeChangeRouteFactory

    @Mock
    lateinit var mockViewStateData: MutableLiveData<TimeSettingsViewState>

    @Mock
    lateinit var mockNavigationData: SingleLiveEvent<TimeSettingsNavData>


    lateinit var testViewModel: TimeSettingsViewModel

    @Rule
    @JvmField
    var expectedExceptionRule = ExpectedException.none()

    @Before
    fun setUp() {
        testViewModel = TimeSettingsViewModel(mockGetSelectedActivityIntervalsUseCase,
                mockSelectActivityIntervalsUseCase,
                mockSetDisableActivityIntervalUseCase,
                mockSetEnableActivityIntervalUseCase,
                mockCreateTimeChangeInitDataUseCase,
                mockLoadingViewStateFactory,
                mockDataViewStateFactory,
                mockItemDetailsRouteFactory,
                mockTimeChangeRouteFactory,
                mockViewStateData,
                mockNavigationData)
    }

    @Test
    fun testOnInitGetItemValueThenOnInitChangeEnableStateToEnabledSuccess_setUpdatedValue(){

        val testBeginTimeInCurrentDay = LocalTime(4, 0)
        val testEndTimeInCurrentDay = LocalTime(5, 0)
        val testIndex = 0
        val testInitIsValidToSave = false
        val testChangedIsValidToSave = true
        val testInitEnabled = false
        val testChangedEnabled = true
        val testInitListIntervalsWithEnabled = mutableListOf(
                Pair(testInitEnabled, ActivityInterval(weekDayId = 1,
                        beginTime = testBeginTimeInCurrentDay,
                        endTime = testEndTimeInCurrentDay))
        )

        val testChangedListIntervalsWithEnabled = mutableListOf(
                Pair(testChangedEnabled, ActivityInterval(weekDayId = 1,
                        beginTime = testBeginTimeInCurrentDay,
                        endTime = testEndTimeInCurrentDay))
        )

        val testInitOrderedIntervalsWithStates = MutableActivityIntervalsWithEnableAndValidState(testInitIsValidToSave,
                testInitListIntervalsWithEnabled)
        val testOrderedIntervalsWithChangedValidState = MutableActivityIntervalsWithEnableAndValidState(testChangedIsValidToSave,
                testChangedListIntervalsWithEnabled)

        val mockLoadingViewState : TimeSettingsViewState.LoadingViewState = mock()

        val testSaveScheduler = TestScheduler()

        whenever(mockGetSelectedActivityIntervalsUseCase.build())
                .thenReturn(Single.just(deepCopy(testInitOrderedIntervalsWithStates)))
        whenever(mockSetEnableActivityIntervalUseCase.build(testIndex, testInitOrderedIntervalsWithStates))
                .thenReturn(Single.just(deepCopy(testOrderedIntervalsWithChangedValidState))
                        .subscribeOn(testSaveScheduler))

        doAnswer { invocationOnMock ->
            TimeSettingsViewState.DataViewState(invocationOnMock.arguments[0] as MutableActivityIntervalsWithEnableAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitGetItem()
        testViewModel.onInitChangeEnableState(testIndex, testChangedEnabled)

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(0)).value = TimeSettingsViewState.DataViewState(testOrderedIntervalsWithChangedValidState)

        testSaveScheduler.triggerActions()

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = TimeSettingsViewState.DataViewState(testOrderedIntervalsWithChangedValidState)
    }

    @Test
    fun testOnInitGetItemValueThenOnBeginTimeChangedSuccess_setUpdatedValue(){

        val testBeginTimeInCurrentDay = LocalTime(4, 0)
        val testEndTimeInCurrentDay = LocalTime(5, 0)
        val testBeginTimeChanged = LocalTime(3, 0)
        val testIndex = 0
        val testInitListIntervalsWithEnabled = mutableListOf(
                Pair(true, ActivityInterval(weekDayId = 1,
                        beginTime = testBeginTimeInCurrentDay,
                        endTime = testEndTimeInCurrentDay))
        )

        val testChangedListIntervalsWithEnabled = mutableListOf(
                Pair(true, ActivityInterval(weekDayId = 1,
                        beginTime = testBeginTimeChanged,
                        endTime = testEndTimeInCurrentDay))
        )

        val testInitOrderedIntervalsWithStates = MutableActivityIntervalsWithEnableAndValidState(true,
                testInitListIntervalsWithEnabled)
        val testOrderedIntervalsWithChangedValidState = MutableActivityIntervalsWithEnableAndValidState(true,
                testChangedListIntervalsWithEnabled)

        val resValWithSettings = TimeSettingsViewState.DataViewState(testOrderedIntervalsWithChangedValidState)

        val mockLoadingViewState : TimeSettingsViewState.LoadingViewState = mock()

        whenever(mockGetSelectedActivityIntervalsUseCase.build())
                .thenReturn(Single.just(deepCopy(testInitOrderedIntervalsWithStates)))

        doAnswer { invocationOnMock ->
            TimeSettingsViewState.DataViewState(invocationOnMock.arguments[0] as MutableActivityIntervalsWithEnableAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitGetItem()
        testViewModel.onTimeChanged(testBeginTimeChanged, true, testIndex)

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(2)).value = TimeSettingsViewState.DataViewState(testOrderedIntervalsWithChangedValidState)
    }

    private fun deepCopy(state: MutableActivityIntervalsWithEnableAndValidState) : MutableActivityIntervalsWithEnableAndValidState{
        val modifList = state.map { it ->
            Pair(it.first, ActivityInterval(it.second.dbId,
                it.second.weekDayId,
                it.second.beginTime,
                it.second.endTime)) }
        return MutableActivityIntervalsWithEnableAndValidState(state.isValidToSave, modifList.toMutableList())
    }
}