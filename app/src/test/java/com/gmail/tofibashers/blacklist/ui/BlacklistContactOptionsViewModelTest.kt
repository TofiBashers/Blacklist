package com.gmail.tofibashers.blacklist.ui

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.*
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.*
import com.gmail.tofibashers.blacklist.ui.common.SavingResult
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


/**
 * Created by TofiBashers on 22.04.2018.
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class BlacklistContactOptionsViewModelTest {

    @Mock
    lateinit var mockGetInteractionModeWithItemAndValidUseCase: IGetInteractionModeWithSelectedBlacklistContactItemUseCase

    @Mock
    lateinit var mockSaveContactWithDeleteSelectionsUseCase: ISaveBlacklistContactItemWithDeleteSelectionsUseCase

    @Mock
    lateinit var mockSelectForEditActivityIntervalsUseCase: ISelectForEditActivityIntervalsOfBlacklistContactPhoneUseCase

    @Mock
    lateinit var mockSaveSelectedIntervalUseCase: ISaveSelectedActivityIntervalsToAllBlacklistContactPhonesIntervalsUseCase

    @Mock
    lateinit var mockValidatePhoneNumbersForSaveSyncUseCase: IValidateBlacklistContactPhoneNumbersForSaveSyncUseCase

    @Mock
    lateinit var mockDeleteAllSelectionsUseCase: IDeleteAllSelectionsUseCase

    @Mock
    lateinit var mockDataViewStateFactory: BlacklistContactOptionsViewState_DataViewStateFactory

    @Mock
    lateinit var mockLoadingViewStateFactory: BlacklistContactOptionsViewState_LoadingViewStateFactory

    @Mock
    lateinit var mockListRouteFactory: BlacklistContactOptionsNavData_ListRouteFactory

    @Mock
    lateinit var mockActivityIntervalDetailsRouteFactory: BlacklistContactOptionsNavData_ActivityIntervalDetailsRouteFactory

    @Mock
    lateinit var mockListRouteWithCancelAndErrorFactory: BlacklistContactOptionsNavData_ListRouteWithCancelAndChangedOrDeletedErrorFactory

    @Mock
    lateinit var mockViewStateData: MutableLiveData<BlacklistContactOptionsViewState>

    @Mock
    lateinit var mockNavigateSingleData: SingleLiveEvent<BlacklistContactOptionsNavData>

    private lateinit var testViewModel: BlacklistContactOptionsViewModel

    @Before
    fun setUp() {
        testViewModel = BlacklistContactOptionsViewModel(mockGetInteractionModeWithItemAndValidUseCase,
                mockSaveContactWithDeleteSelectionsUseCase,
                mockSelectForEditActivityIntervalsUseCase,
                mockSaveSelectedIntervalUseCase,
                mockValidatePhoneNumbersForSaveSyncUseCase,
                mockDeleteAllSelectionsUseCase,
                mockDataViewStateFactory,
                mockLoadingViewStateFactory,
                mockListRouteFactory,
                mockActivityIntervalDetailsRouteFactory,
                mockListRouteWithCancelAndErrorFactory,
                mockViewStateData,
                mockNavigateSingleData)
    }

    @Test
    fun testOnInitGetContactAndPhonesSuccess_setToView(){

        val testModeWithItemAndState = defaultCreateModeAndContactWithOnePhone()
        val testDataViewState = BlacklistContactOptionsViewState.DataViewState(testModeWithItemAndState)
        val mockLoadingViewState : BlacklistContactOptionsViewState.LoadingViewState = mock()
        val testScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemAndValidUseCase.build())
                .thenReturn(Single.just(testModeWithItemAndState)
                        .observeOn(testScheduler))
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        whenever(mockDataViewStateFactory.create(testModeWithItemAndState)).thenReturn(testDataViewState)

        testViewModel.onInitGetContactAndPhones()

        verifyViewModelsValuesCallsCount(testDataViewState, 0,
                mockLoadingViewState, 1)

        testScheduler.triggerActions()

        verifyViewModelsValuesCallsCount(testDataViewState, 1,
                mockLoadingViewState, 1)
    }

    @Test
    fun testOnInitGetSuccessAndThenInitSaveSuccess_navToListWithSaved(){

        val testPhoneNumbers = defaultContactPhonesWithOnePhone()
        val testContactItem = defaultContactItem()
        val testModeWithItemAndState = InteractionModeWithBlacklistContactItemAndNumbersAndValidState(
                mode = InteractionMode.EDIT,
                contactItem = testContactItem,
                phoneNumbers = testPhoneNumbers,
                isValidToSave = true)
        val testDataViewState = BlacklistContactOptionsViewState.DataViewState(testModeWithItemAndState)
        val testFinalRoute = BlacklistContactOptionsNavData.ListRoute(SavingResult.SAVED)
        val testSaveScheduler = TestScheduler()

        val mockLoadingViewState : BlacklistContactOptionsViewState.LoadingViewState = mock()

        whenever(mockGetInteractionModeWithItemAndValidUseCase.build())
                .thenReturn(Single.just(testModeWithItemAndState))
        whenever(mockSaveContactWithDeleteSelectionsUseCase.build(testContactItem, testPhoneNumbers))
                .thenReturn(Completable.complete()
                        .observeOn(testSaveScheduler))
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        whenever(mockDataViewStateFactory.create(testModeWithItemAndState)).thenReturn(testDataViewState)
        whenever(mockListRouteFactory.create(SavingResult.SAVED)).thenReturn(testFinalRoute)

        testViewModel.onInitGetContactAndPhones()
        testViewModel.onInitSave()

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 2,
                listRoute = testFinalRoute, timesListRouteCalled = 0)

        testSaveScheduler.triggerActions()

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 2,
                listRoute = testFinalRoute, timesListRouteCalled = 1)
    }

    @Test
    fun testOnInitGetSuccessAndThenInitSaveOutdatedError_navToListWithCancelAndError(){

        val testPhoneNumbers = defaultContactPhonesWithOnePhone()
        val testContactItem = defaultContactItem()
        val testModeWithItemAndState = InteractionModeWithBlacklistContactItemAndNumbersAndValidState(
                mode = InteractionMode.CREATE,
                contactItem = testContactItem,
                phoneNumbers = testPhoneNumbers,
                isValidToSave = false)
        val testDataViewState = BlacklistContactOptionsViewState.DataViewState(testModeWithItemAndState)
        val testListRouteWithError = BlacklistContactOptionsNavData.ListRouteWithCancelAndChangedOrDeletedError()
        val mockLoadingViewState : BlacklistContactOptionsViewState.LoadingViewState = mock()
        val testSaveScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemAndValidUseCase.build())
                .thenReturn(Single.just(testModeWithItemAndState))
        whenever(mockSaveContactWithDeleteSelectionsUseCase.build(testContactItem, testPhoneNumbers))
                .thenReturn(Completable.error(OutdatedDataException())
                        .observeOn(testSaveScheduler))
        whenever(mockLoadingViewStateFactory.create())
                .thenReturn(mockLoadingViewState)
        whenever(mockDataViewStateFactory.create(testModeWithItemAndState))
                .thenReturn(testDataViewState)
        whenever(mockListRouteWithCancelAndErrorFactory.create())
                .thenReturn(testListRouteWithError)

        testViewModel.onInitGetContactAndPhones()
        testViewModel.onInitSave()

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 2,
                listWithErrorRoute = testListRouteWithError, timesListWithErrorCalled = 0)

        testSaveScheduler.triggerActions()

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 2,
                listWithErrorRoute = testListRouteWithError, timesListWithErrorCalled = 1)
    }

    @Test
    fun testOnInitGetSuccessAndThenInitCancelSuccess_navToListWithCancelled(){

        val testModeWithItemAndState = defaultCreateModeAndContactWithOnePhone()
        val testDataViewState = BlacklistContactOptionsViewState.DataViewState(testModeWithItemAndState)
        val testListWithCancelledRoute = BlacklistContactOptionsNavData.ListRoute(SavingResult.CANCELED)
        val mockLoadingViewState : BlacklistContactOptionsViewState.LoadingViewState = mock()
        val testDeleteAllScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemAndValidUseCase.build())
                .thenReturn(Single.just(testModeWithItemAndState))
        whenever(mockDeleteAllSelectionsUseCase.build())
                .thenReturn(Completable.complete()
                        .observeOn(testDeleteAllScheduler))
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        whenever(mockDataViewStateFactory.create(testModeWithItemAndState)).thenReturn(testDataViewState)
        whenever(mockListRouteFactory.create(SavingResult.CANCELED)).thenReturn(testListWithCancelledRoute)

        testViewModel.onInitGetContactAndPhones()
        testViewModel.onInitCancel()

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 2,
                listRoute = testListWithCancelledRoute, timesListRouteCalled = 0)

        testDeleteAllScheduler.triggerActions()

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 2,
                listRoute = testListWithCancelledRoute, timesListRouteCalled = 1)
    }

    @Test
    fun testOnInitGetSuccessAndThenInitChangeScheduleSuccess_showDataAndNavToIntervals(){

        val testPosition = 0
        val testModeWithItemAndState = defaultCreateModeAndContactWithOnePhone()
        val testActivityIntervalRoute = BlacklistContactOptionsNavData.ActivityIntervalDetailsRoute()
        val mockLoadingViewState : BlacklistContactOptionsViewState.LoadingViewState = mock()
        val testSelectForEditScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemAndValidUseCase.build())
                .thenReturn(Single.just(testModeWithItemAndState))
        whenever(mockSelectForEditActivityIntervalsUseCase.build(testPosition, testModeWithItemAndState.size))
                .thenReturn(Completable.complete()
                        .observeOn(testSelectForEditScheduler))
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        whenever(mockActivityIntervalDetailsRouteFactory.create()).thenReturn(testActivityIntervalRoute)
        doAnswer { invocationOnMock ->
            BlacklistContactOptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistContactItemAndNumbersAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())

        testViewModel.onInitGetContactAndPhones()
        testViewModel.onInitChangeSchedule(testPosition)

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 2,
                dataViewState = BlacklistContactOptionsViewState.DataViewState(testModeWithItemAndState), timesDataCalled = 1,
                intervalDetailsRoute = testActivityIntervalRoute, timesIntervalDetailsRouteCalled = 0)

        testSelectForEditScheduler.triggerActions()

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 2,
                dataViewState = BlacklistContactOptionsViewState.DataViewState(testModeWithItemAndState), timesDataCalled = 2,
                intervalDetailsRoute = testActivityIntervalRoute, timesIntervalDetailsRouteCalled = 1)
    }

    @Test
    fun testOnInitGetAndThenInitChangeScheduleAndThenOnScheduleChangedSuccess_showData(){

        val testPosition = 0
        val testModeWithItemAndState = defaultCreateModeAndContactWithOnePhone()
        val testActivityIntervalsRoute = BlacklistContactOptionsNavData.ActivityIntervalDetailsRoute()
        val mockLoadingViewState : BlacklistContactOptionsViewState.LoadingViewState = mock()
        val testScheduleChangedScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemAndValidUseCase.build())
                .thenReturn(Single.just(testModeWithItemAndState))
        whenever(mockSelectForEditActivityIntervalsUseCase.build(testPosition, testModeWithItemAndState.size))
                .thenReturn(Completable.complete())
        whenever(mockSaveSelectedIntervalUseCase.build(testPosition))
                .thenReturn(Completable.complete()
                        .observeOn(testScheduleChangedScheduler))
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        whenever(mockActivityIntervalDetailsRouteFactory.create())
                .thenReturn(testActivityIntervalsRoute)
        doAnswer { invocationOnMock ->
            BlacklistContactOptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistContactItemAndNumbersAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())

        testViewModel.onInitGetContactAndPhones()
        testViewModel.onInitChangeSchedule(testPosition)
        testViewModel.onScheduleChanged()

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 3,
                dataViewState = BlacklistContactOptionsViewState.DataViewState(testModeWithItemAndState), timesDataCalled = 2,
                intervalDetailsRoute = testActivityIntervalsRoute, timesIntervalDetailsRouteCalled = 1)

        testScheduleChangedScheduler.triggerActions()

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 3,
                dataViewState = BlacklistContactOptionsViewState.DataViewState(testModeWithItemAndState), timesDataCalled = 3,
                intervalDetailsRoute = testActivityIntervalsRoute, timesIntervalDetailsRouteCalled = 1)
    }

    @Test
    fun testOnInitGetAndThenInitChangeScheduleAndThenOnScheduleChangedOutdatedError_navToListWithCancelAndError(){

        val testPosition = 0
        val testModeWithItemAndState = defaultCreateModeAndContactWithOnePhone()
        val testDetailsRoute = BlacklistContactOptionsNavData.ActivityIntervalDetailsRoute()
        val testListWithErrorRoute = BlacklistContactOptionsNavData.ListRouteWithCancelAndChangedOrDeletedError()
        val mockLoadingViewState : BlacklistContactOptionsViewState.LoadingViewState = mock()
        val testScheduleChangedScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemAndValidUseCase.build())
                .thenReturn(Single.just(testModeWithItemAndState))
        whenever(mockSelectForEditActivityIntervalsUseCase.build(testPosition, testModeWithItemAndState.size))
                .thenReturn(Completable.complete())
        whenever(mockSaveSelectedIntervalUseCase.build(testPosition))
                .thenReturn(Completable.error(OutdatedDataException())
                        .observeOn(testScheduleChangedScheduler))
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        whenever(mockActivityIntervalDetailsRouteFactory.create()).thenReturn(testDetailsRoute)
        whenever(mockListRouteWithCancelAndErrorFactory.create()).thenReturn(testListWithErrorRoute)
        doAnswer { invocationOnMock ->
            BlacklistContactOptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistContactItemAndNumbersAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())

        testViewModel.onInitGetContactAndPhones()
        testViewModel.onInitChangeSchedule(testPosition)
        testViewModel.onScheduleChanged()

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 3,
                dataViewState = BlacklistContactOptionsViewState.DataViewState(testModeWithItemAndState), timesDataCalled = 2,
                intervalDetailsRoute = testDetailsRoute, timesIntervalDetailsRouteCalled = 1,
                listWithErrorRoute = testListWithErrorRoute, timesListWithErrorCalled = 0)

        testScheduleChangedScheduler.triggerActions()

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 3,
                dataViewState = BlacklistContactOptionsViewState.DataViewState(testModeWithItemAndState), timesDataCalled = 2,
                intervalDetailsRoute = testDetailsRoute, timesIntervalDetailsRouteCalled = 1,
                listWithErrorRoute = testListWithErrorRoute, timesListWithErrorCalled = 1)
    }

    @Test
    fun testOnInitGetAndThenOnSetIsCallsBlocked_showUpdDataWithValidToSave(){

        val testValidToSave = false
        val testPosition = 0
        val testIsCallsBlocked = true
        val testChangedCallsBlockedPhoneNumbers = listOf(BlacklistContactPhoneNumberItem(dbId = 0,
                deviceDbId = 0,
                number = "12435",
                isCallsBlocked = testIsCallsBlocked,
                isSmsBlocked = false))
        val testContactItem = defaultContactItem()

        val testModeWithItemAndState = InteractionModeWithBlacklistContactItemAndNumbersAndValidState(
                mode = InteractionMode.CREATE,
                contactItem = testContactItem,
                phoneNumbers = testChangedCallsBlockedPhoneNumbers,
                isValidToSave = !testValidToSave)

        val testModeWithItemAndStateAfterChangeValid = InteractionModeWithBlacklistContactItemAndNumbersAndValidState(
                mode = InteractionMode.CREATE,
                contactItem = testContactItem,
                phoneNumbers = testChangedCallsBlockedPhoneNumbers,
                isValidToSave = testValidToSave)
        val mockLoadingViewState : BlacklistContactOptionsViewState.LoadingViewState = mock()
        val testValidateScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemAndValidUseCase.build())
                .thenReturn(Single.just(testModeWithItemAndState))
        whenever(mockValidatePhoneNumbersForSaveSyncUseCase.build(testChangedCallsBlockedPhoneNumbers))
                .thenReturn(Single.just(testValidToSave)
                        .observeOn(testValidateScheduler))
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        doAnswer { invocationOnMock ->
            BlacklistContactOptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistContactItemAndNumbersAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())

        testViewModel.onInitGetContactAndPhones()
        testViewModel.onSetIsCallsBlocked(testPosition, testIsCallsBlocked)

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 1,
                dataViewState = BlacklistContactOptionsViewState.DataViewState(testModeWithItemAndState), timesDataCalled = 1)

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = BlacklistContactOptionsViewState.DataViewState(testModeWithItemAndState)

        testValidateScheduler.triggerActions()

        verifyViewModelsValuesCallsCount(
                loadingViewState = mockLoadingViewState, timesLoadingCalled = 1,
                dataViewState = BlacklistContactOptionsViewState.DataViewState(testModeWithItemAndState), timesDataCalled = 2)
    }

    private fun verifyViewModelsValuesCallsCount(dataViewState: BlacklistContactOptionsViewState.DataViewState? = null, timesDataCalled: Int? = null,
                                                 loadingViewState: BlacklistContactOptionsViewState.LoadingViewState? = null, timesLoadingCalled: Int? = null,
                                                 listRoute: BlacklistContactOptionsNavData.ListRoute? = null, timesListRouteCalled: Int? = null,
                                                 listWithErrorRoute: BlacklistContactOptionsNavData.ListRouteWithCancelAndChangedOrDeletedError? = null, timesListWithErrorCalled: Int? = null,
                                                 intervalDetailsRoute: BlacklistContactOptionsNavData.ActivityIntervalDetailsRoute? = null, timesIntervalDetailsRouteCalled: Int? = null) {
        verifyMockLiveDataIfNonNullsSettings(mockViewStateData, timesDataCalled, dataViewState)
        verifyMockLiveDataIfNonNullsSettings(mockViewStateData, timesLoadingCalled, loadingViewState)
        verifyMockLiveDataIfNonNullsSettings(mockNavigateSingleData, timesListRouteCalled, listRoute)
        verifyMockLiveDataIfNonNullsSettings(mockNavigateSingleData, timesListWithErrorCalled, listWithErrorRoute)
        verifyMockLiveDataIfNonNullsSettings(mockNavigateSingleData, timesIntervalDetailsRouteCalled, intervalDetailsRoute)
    }

    private fun <T> verifyMockLiveDataIfNonNullsSettings(mockLiveData: MutableLiveData<T>,
                                                         timesDataCalled: Int? = null,
                                                         dataViewState: T? = null) {
        if(dataViewState != null && timesDataCalled != null){
            verify(mockLiveData, times(timesDataCalled)).value = dataViewState
        }
    }

    private fun defaultCreateModeAndContactWithOnePhone() : InteractionModeWithBlacklistContactItemAndNumbersAndValidState {
        return InteractionModeWithBlacklistContactItemAndNumbersAndValidState(mode = InteractionMode.CREATE,
                contactItem = defaultContactItem(),
                phoneNumbers = defaultContactPhonesWithOnePhone(),
                isValidToSave = false)
    }

    private fun defaultContactItem() : BlacklistContactItem {
        return BlacklistContactItem(dbId = 0,
                deviceDbId = 0,
                deviceKey = "123",
                name = "name",
                photoUrl = "localhost")
    }

    private fun defaultContactPhonesWithOnePhone() : List<BlacklistContactPhoneNumberItem> {
        return listOf(BlacklistContactPhoneNumberItem(dbId = 0,
                deviceDbId = 0,
                number = "12435",
                isCallsBlocked = false,
                isSmsBlocked = false))
    }
}