package com.gmail.tofibashers.blacklist.ui

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.IDeleteAllSelectionsUseCase
import com.gmail.tofibashers.blacklist.domain.IGetInteractionModeWithSelectedBlackListItemUseCase
import com.gmail.tofibashers.blacklist.domain.ISaveBlacklistItemWithDeleteSelectionsUseCase
import com.gmail.tofibashers.blacklist.domain.IValidateBlacklistItemForSaveSyncUseCase
import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import com.gmail.tofibashers.blacklist.entity.InteractionModeWithBlacklistItemAndValidState
import com.gmail.tofibashers.blacklist.entity.NumberAlreadyExistsException
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import com.gmail.tofibashers.blacklist.ui.options.*
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
class OptionsViewModelTest {

    @Mock
    lateinit var mockGetInteractionModeWithItemUseCase: IGetInteractionModeWithSelectedBlackListItemUseCase

    @Mock
    lateinit var mockSaveBlacklistItemWithDeleteSelectionsUseCase: ISaveBlacklistItemWithDeleteSelectionsUseCase

    @Mock
    lateinit var mockDeleteAllSelectionsUseCase: IDeleteAllSelectionsUseCase

    @Mock
    lateinit var mockSyncValidateBlacklistItemForSaveUseCase: IValidateBlacklistItemForSaveSyncUseCase

    @Mock
    lateinit var mockDataViewStateFactory: OptionsViewState_DataViewStateFactory

    @Mock
    lateinit var mockLoadingViewStateFactory: OptionsViewState_LoadingViewStateFactory

    @Mock
    lateinit var mockListRouteFactory: OptionsNavData_ListRouteFactory

    @Mock
    lateinit var mockActivityIntervalDetailsRouteFactory: OptionsNavData_ActivityIntervalDetailsRouteFactory

    @Mock
    lateinit var mockNumberAlreadyExistsRouteFactory: OptionsNavData_SavedNumberAlreadyExistsRouteFactory

    @Mock
    lateinit var mockViewStateData: MutableLiveData<OptionsViewState>

    @Mock
    lateinit var mockNavigateSingleData: SingleLiveEvent<OptionsNavData>

    lateinit var testViewModel: OptionsViewModel

    @Rule
    @JvmField
    var expectedExceptionRule = ExpectedException.none()


    @Before
    fun setUp() {
        testViewModel = OptionsViewModel(mockGetInteractionModeWithItemUseCase,
                mockSaveBlacklistItemWithDeleteSelectionsUseCase,
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

        val testInteractionMode = InteractionModeWithBlacklistItemAndValidState(InteractionMode.CREATE,
                BlacklistItem(number = "123",
                        isCallsBlocked = true,
                        isSmsBlocked = true),
                true)

        val mockLoadingViewState : OptionsViewState.LoadingViewState = mock()

        val testScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemUseCase.build())
                .thenReturn(Single.just(deepCopy(testInteractionMode))
                        .observeOn(testScheduler))

        doAnswer { invocationOnMock ->
            OptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistItemAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitGetItem()

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(0)).value = OptionsViewState.DataViewState(testInteractionMode)

        testScheduler.triggerActions()

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = OptionsViewState.DataViewState(testInteractionMode)
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

        val testItem = BlacklistItem(number = "123",
                isCallsBlocked = true,
                isSmsBlocked = true)
        val testInteractionMode = InteractionModeWithBlacklistItemAndValidState(InteractionMode.CREATE,
                testItem,
                true)

        val mockLoadingViewState : OptionsViewState.LoadingViewState = mock()
        val mockListRoute : OptionsNavData.ListRoute = mock()

        val testSaveScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemUseCase.build())
                .thenReturn(Single.just(deepCopy(testInteractionMode)))
        whenever(mockSaveBlacklistItemWithDeleteSelectionsUseCase.build(testItem))
                .thenReturn(Completable.complete()
                        .subscribeOn(testSaveScheduler))

        doAnswer { invocationOnMock ->
            OptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistItemAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        whenever(mockListRouteFactory.create(OptionsResult.OK)).thenReturn(mockListRoute)

        testViewModel.onInitGetItem()
        testViewModel.onInitSave()

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = OptionsViewState.DataViewState(testInteractionMode)
        verifyZeroInteractions(mockNavigateSingleData)

        testSaveScheduler.triggerActions()

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        verify(mockNavigateSingleData, times(1)).value = mockListRoute
        verify(mockViewStateData, times(1)).value = OptionsViewState.DataViewState(testInteractionMode)
    }

    @Test
    fun testOnInitGetItemValueThenInitSaveNumberAlreadyExistsException_navigateToNumberAlreadyExists(){

        val testItem = BlacklistItem(number = "123",
                isCallsBlocked = true,
                isSmsBlocked = true)
        val testModeWithItem = InteractionModeWithBlacklistItemAndValidState(InteractionMode.CREATE,
                testItem,
                true)

        val mockLoadingViewState : OptionsViewState.LoadingViewState = mock()
        val mockSavedNumberAlreadyExistsRoute : OptionsNavData.SavedNumberAlreadyExistsRoute = mock()

        val testSaveScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemUseCase.build())
                .thenReturn(Single.just(deepCopy(testModeWithItem)))
        whenever(mockSaveBlacklistItemWithDeleteSelectionsUseCase.build(testItem))
                .thenReturn(Completable.error(NumberAlreadyExistsException())
                        .subscribeOn(testSaveScheduler))

        doAnswer { invocationOnMock ->
            OptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistItemAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        whenever(mockNumberAlreadyExistsRouteFactory.create()).thenReturn(mockSavedNumberAlreadyExistsRoute)

        testViewModel.onInitGetItem()
        testViewModel.onInitSave()

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = OptionsViewState.DataViewState(testModeWithItem)
        verifyZeroInteractions(mockNavigateSingleData)

        testSaveScheduler.triggerActions()

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        val viewStateNavigateOrder = inOrder(mockViewStateData, mockNavigateSingleData)
        viewStateNavigateOrder.verify(mockViewStateData, times(2)).value = OptionsViewState.DataViewState(testModeWithItem)
        viewStateNavigateOrder.verify(mockNavigateSingleData, times(1)).value = mockSavedNumberAlreadyExistsRoute
    }

    @Test
    fun testOnInitSaveSuccessWithoutPreOnInitGetItem_throw(){

        expectedExceptionRule.expect(NullPointerException::class.java)

        val mockLoadingViewState : OptionsViewState.LoadingViewState = mock()

        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        testViewModel.onInitSave()
    }

    @Test
    fun testOnInitGetItemValueThenInitCancelSuccess_navigateToList(){

        val testInteractionMode = InteractionModeWithBlacklistItemAndValidState(InteractionMode.CREATE,
                BlacklistItem(number = "123",
                        isCallsBlocked = true,
                        isSmsBlocked = true),
                true)

        val mockLoadingViewState : OptionsViewState.LoadingViewState = mock()
        val mockListRoute : OptionsNavData.ListRoute = mock()

        val testCancelScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemUseCase.build())
                .thenReturn(Single.just(deepCopy(testInteractionMode)))
        whenever(mockDeleteAllSelectionsUseCase.build())
                .thenReturn(Completable.complete()
                        .subscribeOn(testCancelScheduler))

        doAnswer { invocationOnMock ->
            OptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistItemAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        whenever(mockListRouteFactory.create(OptionsResult.CANCELED)).thenReturn(mockListRoute)

        testViewModel.onInitGetItem()
        testViewModel.onInitCancel()

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = OptionsViewState.DataViewState(testInteractionMode)
        verifyZeroInteractions(mockNavigateSingleData)

        testCancelScheduler.triggerActions()

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = OptionsViewState.DataViewState(testInteractionMode)
        verify(mockNavigateSingleData, times(1)).value = mockListRoute
    }

    @Test
    fun testOnInitGetItemValueThenInitChangeNumberSuccess_setValueThenValueWithNewValidState(){

        val testInitValidToSave = true
        val testModifValidToSave = false
        val testModifNumber = ""
        val testInitBlacklistItem = BlacklistItem(number = "123",
                isCallsBlocked = true,
                isSmsBlocked = true)
        val testInitBlacklistItemWithModifNumber = BlacklistItem(number = testModifNumber,
                isCallsBlocked = true,
                isSmsBlocked = true)

        val testInteractionMode = InteractionModeWithBlacklistItemAndValidState(InteractionMode.CREATE,
                testInitBlacklistItem,
                testInitValidToSave)

        val expResInteractionMode = InteractionModeWithBlacklistItemAndValidState(InteractionMode.CREATE,
                testInitBlacklistItemWithModifNumber,
                testModifValidToSave)

        val mockLoadingViewState : OptionsViewState.LoadingViewState = mock()

        val testSyncScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemUseCase.build())
                .thenReturn(Single.just(deepCopy(testInteractionMode)))
        whenever(mockSyncValidateBlacklistItemForSaveUseCase.build(testInitBlacklistItemWithModifNumber))
                .thenReturn(Single.just(testModifValidToSave)
                        .observeOn(testSyncScheduler))

        doAnswer { invocationOnMock ->
            OptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistItemAndValidState) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)
        testViewModel.onInitGetItem()
        testViewModel.onNumberChanged(testModifNumber)

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(0)).value = OptionsViewState.DataViewState(expResInteractionMode)

        testSyncScheduler.triggerActions()

        verify(mockViewStateData, times(1)).value = mockLoadingViewState
        verify(mockViewStateData, times(2)).value = OptionsViewState.DataViewState(expResInteractionMode)
    }

    @Test
    fun testOnInitGetItemValueThenIsCallsBlockedSuccess_setValueThenValueWithNewValidState(){

        val testInitIsCallsBlocked = true
        val testModifIsCallsBlocked = false
        val testInitValidToSave = true
        val testModifValidToSave = false
        val testInitBlacklistItem = BlacklistItem(number = "123",
                isCallsBlocked = testInitIsCallsBlocked,
                isSmsBlocked = false)
        val testBlacklistItemWithModifIsCallsBlocked = BlacklistItem(number = "123",
                isCallsBlocked = testModifIsCallsBlocked,
                isSmsBlocked = false)

        val testInteractionMode = InteractionModeWithBlacklistItemAndValidState(InteractionMode.CREATE,
                testInitBlacklistItem,
                testInitValidToSave)

        val expResInteractionMode = InteractionModeWithBlacklistItemAndValidState(InteractionMode.CREATE,
                testBlacklistItemWithModifIsCallsBlocked,
                testModifValidToSave)

        val expResViewState = OptionsViewState.DataViewState(expResInteractionMode)

        val mockLoadingViewState : OptionsViewState.LoadingViewState = mock()

        val testSyncScheduler = TestScheduler()

        whenever(mockGetInteractionModeWithItemUseCase.build())
                .thenReturn(Single.just(deepCopy(testInteractionMode)))
        whenever(mockSyncValidateBlacklistItemForSaveUseCase.build(testBlacklistItemWithModifIsCallsBlocked))
                .thenReturn(Single.just(testModifValidToSave)
                        .observeOn(testSyncScheduler))

        doAnswer { invocationOnMock ->
            OptionsViewState.DataViewState(invocationOnMock.arguments[0] as InteractionModeWithBlacklistItemAndValidState) }
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

    private fun deepCopy(srcMode: InteractionModeWithBlacklistItemAndValidState) : InteractionModeWithBlacklistItemAndValidState{
        return InteractionModeWithBlacklistItemAndValidState(srcMode.mode,
                BlacklistItem(srcMode.item.dbId,
                        srcMode.item.number,
                        srcMode.item.isCallsBlocked,
                        srcMode.item.isSmsBlocked),
                srcMode.isValidToSave)
    }
}