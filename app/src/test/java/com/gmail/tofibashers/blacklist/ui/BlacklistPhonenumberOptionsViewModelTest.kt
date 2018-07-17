package com.gmail.tofibashers.blacklist.ui

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.IDeleteAllSelectionsUseCase
import com.gmail.tofibashers.blacklist.domain.IGetInteractionModeWithSelectedBlackListItemUseCase
import com.gmail.tofibashers.blacklist.domain.ISaveBlacklistPhoneItemWithDeleteSelectionsUseCase
import com.gmail.tofibashers.blacklist.domain.IValidateBlacklistItemForSaveSyncUseCase
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import com.gmail.tofibashers.blacklist.entity.InteractionModeWithBlacklistPhoneNumberItemAndValidState
import com.gmail.tofibashers.blacklist.entity.NumberAlreadyExistsException
import com.gmail.tofibashers.blacklist.ui.common.SavingResult
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options.*
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
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
class BlacklistPhonenumberOptionsViewModelTest {

    @Mock
    lateinit var mockGetInteractionModeWithItemUseCase: IGetInteractionModeWithSelectedBlackListItemUseCase

    @Mock
    lateinit var mockSaveBlacklistPhoneItemWithDeleteSelectionsUseCase: ISaveBlacklistPhoneItemWithDeleteSelectionsUseCase

    @Mock
    lateinit var mockDeleteAllSelectionsUseCase: IDeleteAllSelectionsUseCase

    @Mock
    lateinit var mockSyncValidateBlacklistItemForSaveUseCase: IValidateBlacklistItemForSaveSyncUseCase

    @Mock
    lateinit var mockDataViewStateFactory: BlacklistPhonenumberOptionsViewState_DataViewStateFactory

    @Mock
    lateinit var mockLoadingViewStateFactory: BlacklistPhonenumberOptionsViewState_LoadingViewStateFactory

    @Mock
    lateinit var mockListRouteFactory: BlacklistPhonenumberOptionsNavData_ListRouteFactory

    @Mock
    lateinit var mockActivityIntervalDetailsRouteFactory: BlacklistPhonenumberOptionsNavData_ActivityIntervalDetailsRouteFactory

    @Mock
    lateinit var mockNumberAlreadyExistsRouteFactory: BlacklistPhonenumberOptionsNavData_SavedNumberAlreadyExistsRouteFactory

    @Mock
    lateinit var mockViewStateData: MutableLiveData<BlacklistPhonenumberOptionsViewState>

    @Mock
    lateinit var mockNavigateSingleData: SingleLiveEvent<BlacklistPhonenumberOptionsNavData>

    lateinit var testViewModel: BlacklistPhonenumberOptionsViewModel

    @Rule
    @JvmField
    var expectedExceptionRule = ExpectedException.none()


    @Before
    fun setUp() {
        testViewModel = BlacklistPhonenumberOptionsViewModel(mockGetInteractionModeWithItemUseCase,
                mockSaveBlacklistPhoneItemWithDeleteSelectionsUseCase,
                mockDeleteAllSelectionsUseCase,
                mockSyncValidateBlacklistItemForSaveUseCase,
                mockDataViewStateFactory,
                mockLoadingViewStateFactory,
                mockListRouteFactory,
                mockActivityIntervalDetailsRouteFactory,
                mockNumberAlreadyExistsRouteFactory,
                mockViewStateData,
                mockNavigateSingleData)
    }

    @Test
    fun testOnInitGetItemValue_setValueToView(){

        val testInteractionMode = InteractionModeWithBlacklistPhoneNumberItemAndValidState(InteractionMode.CREATE,
                BlacklistPhoneNumberItem(number = "123",
                        isCallsBlocked = true,
                        isSmsBlocked = true),
                true)

        val mockLoadingViewState : BlacklistPhonenumberOptionsViewState.LoadingViewState = mock()

        val testScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemUseCase.build())
                .thenReturn(Single.just(deepCopy(testInteractionMode))
                        .observeOn(testScheduler))

        doAnswer { invocationOnMock ->
            BlacklistPhonenumberOptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistPhoneNumberItemAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitGetItem()

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(0)).value = BlacklistPhonenumberOptionsViewState.DataViewState(testInteractionMode)

        testScheduler.triggerActions()

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = BlacklistPhonenumberOptionsViewState.DataViewState(testInteractionMode)
    }

    @Test
    fun testOnInitGetItemError_throw(){

        val testException = Throwable()
        expectedExceptionRule.expectCause(errorTypeAndEqualsCause(RuntimeException::class, testException))

        whenever(mockGetInteractionModeWithItemUseCase.build())
                .thenReturn(Single.error(testException))

        testViewModel.onInitGetItem()
    }

    @Test
    fun testOnInitGetItemValueThenInitSaveSuccess_navigateToList(){

        val testItem = BlacklistPhoneNumberItem(number = "123",
                isCallsBlocked = true,
                isSmsBlocked = true)
        val testInteractionMode = InteractionModeWithBlacklistPhoneNumberItemAndValidState(InteractionMode.CREATE,
                testItem,
                true)

        val mockLoadingViewState : BlacklistPhonenumberOptionsViewState.LoadingViewState = mock()
        val mockListRoute : BlacklistPhonenumberOptionsNavData.ListRoute = mock()

        val testSaveScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemUseCase.build())
                .thenReturn(Single.just(deepCopy(testInteractionMode)))
        whenever(mockSaveBlacklistPhoneItemWithDeleteSelectionsUseCase.build(testItem))
                .thenReturn(Completable.complete()
                        .subscribeOn(testSaveScheduler))

        doAnswer { invocationOnMock ->
            BlacklistPhonenumberOptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistPhoneNumberItemAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        whenever(mockListRouteFactory.create(SavingResult.SAVED)).thenReturn(mockListRoute)

        testViewModel.onInitGetItem()
        testViewModel.onInitSave()

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = BlacklistPhonenumberOptionsViewState.DataViewState(testInteractionMode)
        verifyZeroInteractions(mockNavigateSingleData)

        testSaveScheduler.triggerActions()

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        verify(mockNavigateSingleData, times(1)).value = mockListRoute
        verify(mockViewStateData, times(1)).value = BlacklistPhonenumberOptionsViewState.DataViewState(testInteractionMode)
    }

    @Test
    fun testOnInitGetItemValueThenInitSaveNumberAlreadyExistsException_navigateToNumberAlreadyExists(){

        val testItem = BlacklistPhoneNumberItem(number = "123",
                isCallsBlocked = true,
                isSmsBlocked = true)
        val testModeWithItem = InteractionModeWithBlacklistPhoneNumberItemAndValidState(InteractionMode.CREATE,
                testItem,
                true)

        val mockLoadingViewState : BlacklistPhonenumberOptionsViewState.LoadingViewState = mock()
        val mockSavedNumberAlreadyExistsRoute : BlacklistPhonenumberOptionsNavData.SavedNumberAlreadyExistsRoute = mock()

        val testSaveScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemUseCase.build())
                .thenReturn(Single.just(deepCopy(testModeWithItem)))
        whenever(mockSaveBlacklistPhoneItemWithDeleteSelectionsUseCase.build(testItem))
                .thenReturn(Completable.error(NumberAlreadyExistsException())
                        .subscribeOn(testSaveScheduler))

        doAnswer { invocationOnMock ->
            BlacklistPhonenumberOptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistPhoneNumberItemAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        whenever(mockNumberAlreadyExistsRouteFactory.create()).thenReturn(mockSavedNumberAlreadyExistsRoute)

        testViewModel.onInitGetItem()
        testViewModel.onInitSave()

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = BlacklistPhonenumberOptionsViewState.DataViewState(testModeWithItem)
        verifyZeroInteractions(mockNavigateSingleData)

        testSaveScheduler.triggerActions()

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        val viewStateNavigateOrder = inOrder(mockViewStateData, mockNavigateSingleData)
        viewStateNavigateOrder.verify(mockViewStateData, times(2)).value = BlacklistPhonenumberOptionsViewState.DataViewState(testModeWithItem)
        viewStateNavigateOrder.verify(mockNavigateSingleData, times(1)).value = mockSavedNumberAlreadyExistsRoute
    }

    @Test
    fun testOnInitSaveSuccessWithoutPreOnInitGetItem_throw(){

        expectedExceptionRule.expect(NullPointerException::class.java)

        val mockLoadingViewState : BlacklistPhonenumberOptionsViewState.LoadingViewState = mock()

        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        testViewModel.onInitSave()
    }

    @Test
    fun testOnInitGetItemValueThenInitCancelSuccess_navigateToList(){

        val testInteractionMode = InteractionModeWithBlacklistPhoneNumberItemAndValidState(InteractionMode.CREATE,
                BlacklistPhoneNumberItem(number = "123",
                        isCallsBlocked = true,
                        isSmsBlocked = true),
                true)

        val mockLoadingViewState : BlacklistPhonenumberOptionsViewState.LoadingViewState = mock()
        val mockListRoute : BlacklistPhonenumberOptionsNavData.ListRoute = mock()

        val testCancelScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemUseCase.build())
                .thenReturn(Single.just(deepCopy(testInteractionMode)))
        whenever(mockDeleteAllSelectionsUseCase.build())
                .thenReturn(Completable.complete()
                        .subscribeOn(testCancelScheduler))

        doAnswer { invocationOnMock ->
            BlacklistPhonenumberOptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistPhoneNumberItemAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        whenever(mockListRouteFactory.create(SavingResult.CANCELED)).thenReturn(mockListRoute)

        testViewModel.onInitGetItem()
        testViewModel.onInitCancel()

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = BlacklistPhonenumberOptionsViewState.DataViewState(testInteractionMode)
        verifyZeroInteractions(mockNavigateSingleData)

        testCancelScheduler.triggerActions()

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = BlacklistPhonenumberOptionsViewState.DataViewState(testInteractionMode)
        verify(mockNavigateSingleData, times(1)).value = mockListRoute
    }

    @Test
    fun testOnInitGetItemValueThenInitChangeNumberSuccess_setValueThenValueWithNewValidState(){

        val testInitValidToSave = true
        val testModifValidToSave = false
        val testModifNumber = ""
        val testInitBlacklistItem = BlacklistPhoneNumberItem(number = "123",
                isCallsBlocked = true,
                isSmsBlocked = true)
        val testInitBlacklistItemWithModifNumber = BlacklistPhoneNumberItem(number = testModifNumber,
                isCallsBlocked = true,
                isSmsBlocked = true)

        val testInteractionMode = InteractionModeWithBlacklistPhoneNumberItemAndValidState(InteractionMode.CREATE,
                testInitBlacklistItem,
                testInitValidToSave)

        val expResInteractionMode = InteractionModeWithBlacklistPhoneNumberItemAndValidState(InteractionMode.CREATE,
                testInitBlacklistItemWithModifNumber,
                testModifValidToSave)

        val mockLoadingViewState : BlacklistPhonenumberOptionsViewState.LoadingViewState = mock()

        val testSyncScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemUseCase.build())
                .thenReturn(Single.just(deepCopy(testInteractionMode)))
        whenever(mockSyncValidateBlacklistItemForSaveUseCase.build(testInitBlacklistItemWithModifNumber))
                .thenReturn(Single.just(testModifValidToSave)
                        .observeOn(testSyncScheduler))

        doAnswer { invocationOnMock ->
            BlacklistPhonenumberOptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistPhoneNumberItemAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        testViewModel.onInitGetItem()
        testViewModel.onNumberChanged(testModifNumber)

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(0)).value = BlacklistPhonenumberOptionsViewState.DataViewState(expResInteractionMode)

        testSyncScheduler.triggerActions()

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(2)).value = BlacklistPhonenumberOptionsViewState.DataViewState(expResInteractionMode)
    }

    @Test
    fun testOnInitGetItemValueThenIsCallsBlockedSuccess_setValueThenValueWithNewValidState(){

        val testInitIsCallsBlocked = true
        val testModifIsCallsBlocked = false
        val testInitValidToSave = true
        val testModifValidToSave = false
        val testInitBlacklistItem = BlacklistPhoneNumberItem(number = "123",
                isCallsBlocked = testInitIsCallsBlocked,
                isSmsBlocked = false)
        val testBlacklistItemWithModifIsCallsBlocked = BlacklistPhoneNumberItem(number = "123",
                isCallsBlocked = testModifIsCallsBlocked,
                isSmsBlocked = false)

        val testInteractionMode = InteractionModeWithBlacklistPhoneNumberItemAndValidState(InteractionMode.CREATE,
                testInitBlacklistItem,
                testInitValidToSave)

        val expResInteractionMode = InteractionModeWithBlacklistPhoneNumberItemAndValidState(InteractionMode.CREATE,
                testBlacklistItemWithModifIsCallsBlocked,
                testModifValidToSave)

        val expResViewState = BlacklistPhonenumberOptionsViewState.DataViewState(expResInteractionMode)

        val mockLoadingViewState : BlacklistPhonenumberOptionsViewState.LoadingViewState = mock()

        val testSyncScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemUseCase.build())
                .thenReturn(Single.just(deepCopy(testInteractionMode)))
        whenever(mockSyncValidateBlacklistItemForSaveUseCase.build(testBlacklistItemWithModifIsCallsBlocked))
                .thenReturn(Single.just(testModifValidToSave)
                        .observeOn(testSyncScheduler))

        doAnswer { invocationOnMock ->
            BlacklistPhonenumberOptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistPhoneNumberItemAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitGetItem()
        testViewModel.onSetIsCallsBlocked(testModifIsCallsBlocked)

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(0)).value = expResViewState

        testSyncScheduler.triggerActions()

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(2)).value = expResViewState
    }

    private fun deepCopy(srcMode: InteractionModeWithBlacklistPhoneNumberItemAndValidState) : InteractionModeWithBlacklistPhoneNumberItemAndValidState {
        return InteractionModeWithBlacklistPhoneNumberItemAndValidState(srcMode.mode,
                BlacklistPhoneNumberItem(srcMode.phoneNumberItem.dbId,
                        srcMode.phoneNumberItem.number,
                        srcMode.phoneNumberItem.isCallsBlocked,
                        srcMode.phoneNumberItem.isSmsBlocked),
                srcMode.isValidToSave)
    }
}