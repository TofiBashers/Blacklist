package com.gmail.tofibashers.blacklist.ui

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.*
import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import com.gmail.tofibashers.blacklist.entity.GetBlacklistResult
import com.gmail.tofibashers.blacklist.entity.SystemVerWarningType
import com.gmail.tofibashers.blacklist.ui.blacklist.*
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Before


/**
 * Created by TofiBashers on 25.02.2018.
 */

@RunWith(MockitoJUnitRunner::class)
class BlacklistViewModelTest {

    @Mock
    lateinit var mockGetBlacklistItemsUseCase: IGetBlacklistItemsSortByNumberWithIgnoreHiddenUseCase

    @Mock
    lateinit var mockSaveIgnoreHiddenNumbersUseCase: ISaveIgnoreHiddenNumbersSyncUseCase

    @Mock
    lateinit var mockSelectBlacklistElementUseCase: ISelectEditModeAndBlacklistItemUseCase

    @Mock
    lateinit var mockSelectCreateModeUseCase: ISelectCreateModeUseCase

    @Mock
    lateinit var mockDeleteBlacklistItemUseCase: IDeleteBlacklistItemUseCase

    @Mock
    lateinit var mockListViewStateFactory: BlacklistViewState_ListViewStateFactory

    @Mock
    lateinit var mockLoadingViewStateFactory: BlacklistViewState_LoadingViewStateFactory

    @Mock
    lateinit var mockViewStateData: MutableLiveData<BlacklistViewState>

    @Mock
    lateinit var mockNavigateSingleData: SingleLiveEvent<BlacklistNavRoute>

    @Mock
    lateinit var mockWarningMessageData: SingleLiveEvent<GetBlacklistResult.SystemVerWarning>

    lateinit var testViewModel: BlacklistViewModel

    @Rule
    @JvmField
    var expectedExceptionRule = ExpectedException.none()

    @Before
    fun setUp() {
        testViewModel = BlacklistViewModel(mockGetBlacklistItemsUseCase,
                mockSaveIgnoreHiddenNumbersUseCase,
                mockSelectBlacklistElementUseCase,
                mockSelectCreateModeUseCase,
                mockDeleteBlacklistItemUseCase,
                mockListViewStateFactory,
                mockLoadingViewStateFactory,
                mockViewStateData,
                mockNavigateSingleData,
                mockWarningMessageData)
    }

    @Test
    fun testOnInitGetListError_throw(){
        val testException = Throwable()
        expectedExceptionRule.expectCause(errorTypeAndEqualsCause(RuntimeException::class, testException))

        whenever(mockGetBlacklistItemsUseCase.build())
                .thenReturn(Observable.error(testException))

        testViewModel.onInitGetList()
    }

    @Test
    fun testOnInitGetListValues_setListsToView(){

        val testWarning = GetBlacklistResult.SystemVerWarning(SystemVerWarningType.MAY_UPDATE_TO_INCOMPATIBLE_VER)
        val testResults = listOf(
                GetBlacklistResult.ListWithIgnoreResult(listOf(
                        BlacklistItem(number = "123",
                                isCallsBlocked = true,
                                isSmsBlocked = true),
                        BlacklistItem(number = "456",
                                isCallsBlocked = true,
                                isSmsBlocked = false)), false),
                GetBlacklistResult.ListWithIgnoreResult(listOf(
                        BlacklistItem(number = "789",
                                isCallsBlocked = true,
                                isSmsBlocked = true),
                        BlacklistItem(number = "000",
                                isCallsBlocked = true,
                                isSmsBlocked = false)), true))

        val mockLoadingViewState : BlacklistViewState.LoadingViewState = mock()
        val testDataSubject = PublishSubject.create<GetBlacklistResult>()

        whenever(mockGetBlacklistItemsUseCase.build())
                .thenReturn(testDataSubject)

        doAnswer { invocationOnMock ->
            BlacklistViewState.ListViewState(invocationOnMock.arguments[0] as GetBlacklistResult.ListWithIgnoreResult) }
                .whenever(mockListViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitGetList()

        verify(mockViewStateData, times(1)).value  = mockLoadingViewState

        testDataSubject.onNext(testWarning)
        verify(mockWarningMessageData, times(1)).value = testWarning
        verify(mockViewStateData, times(0)).value = BlacklistViewState.ListViewState(testResults[0])
        verify(mockViewStateData, times(0)).value = BlacklistViewState.ListViewState(testResults[1])

        testDataSubject.onNext(testResults[0])
        verify(mockWarningMessageData, times(1)).value = testWarning
        verify(mockViewStateData, times(1)).value = BlacklistViewState.ListViewState(testResults[0])
        verify(mockViewStateData, times(0)).value = BlacklistViewState.ListViewState(testResults[1])

        testDataSubject.onNext(testResults[1])
        verify(mockViewStateData, times(1)).value = BlacklistViewState.ListViewState(testResults[1])
    }

    @Test
    fun testOnInitItemChangeSuccess_navToOptions(){

        val testBlacklistItem = BlacklistItem(10, "12", true, true)
        val mockLoadingViewState : BlacklistViewState.LoadingViewState = mock()
        val testScheduler = TestScheduler()

        whenever(mockSelectBlacklistElementUseCase.build(testBlacklistItem))
                .thenReturn(Completable.complete().observeOn(testScheduler))
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitItemChange(testBlacklistItem)

        verify(mockViewStateData, times(1)).value  = mockLoadingViewState
        verify(mockNavigateSingleData, times(0)).value = BlacklistNavRoute.OPTIONS

        testScheduler.triggerActions()

        verify(mockNavigateSingleData, times(1)).value = BlacklistNavRoute.OPTIONS
    }

    @Test
    fun testOnInitItemChangeError_throw(){

        val testBlacklistItem = BlacklistItem(10, "12", true, true)

        val testException = RuntimeException()
        expectedExceptionRule.expectCause(errorTypeAndEqualsCause(RuntimeException::class, testException))

        whenever(mockSelectBlacklistElementUseCase.build(testBlacklistItem))
                .thenReturn(Completable.error(testException))

        testViewModel.onInitItemChange(testBlacklistItem)
    }

    @Test
    fun testOnInitCreateItemSuccess_navToOptions(){

        val mockLoadingViewState : BlacklistViewState.LoadingViewState = mock()
        val testScheduler = TestScheduler()

        whenever(mockSelectCreateModeUseCase.build())
                .thenReturn(Completable.complete()
                        .observeOn(testScheduler))
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitCreateItem()

        verify(mockViewStateData, times(1)).value  = mockLoadingViewState
        verify(mockNavigateSingleData, times(0)).value = BlacklistNavRoute.OPTIONS

        testScheduler.triggerActions()

        verify(mockNavigateSingleData, times(1)).value = BlacklistNavRoute.OPTIONS
    }

    @Test
    fun testOnInitCreateItemError_throw(){

        val testException = RuntimeException()
        expectedExceptionRule.expectCause(errorTypeAndEqualsCause(RuntimeException::class, testException))

        whenever(mockSelectCreateModeUseCase.build())
                .thenReturn(Completable.error(testException))

        testViewModel.onInitCreateItem()
    }

    @Test
    fun testOnInitItemDeleteSuccess_nothing(){

        val testBlacklistItem = BlacklistItem(10, "12", true, true)
        val mockLoadingViewState : BlacklistViewState.LoadingViewState = mock()

        whenever(mockDeleteBlacklistItemUseCase.build(testBlacklistItem))
                .thenReturn(Completable.complete())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitItemDelete(testBlacklistItem)

        verify(mockViewStateData, times(1)).value  = mockLoadingViewState
        verifyNoMoreInteractions(mockViewStateData, mockNavigateSingleData)
    }

    @Test
    fun testOnInitItemDeleteError_throw(){

        val testBlacklistItem = BlacklistItem(10, "12", true, true)

        val testException = RuntimeException()
        expectedExceptionRule.expectCause(errorTypeAndEqualsCause(RuntimeException::class, testException))

        whenever(mockDeleteBlacklistItemUseCase.build(testBlacklistItem))
                .thenReturn(Completable.error(testException))

        testViewModel.onInitItemDelete(testBlacklistItem)
    }

    @Test
    fun testOnIgnoreHiddenStateChangedSuccess_nothing(){

        val testIgnoreHidden = false

        whenever(mockSaveIgnoreHiddenNumbersUseCase.build(testIgnoreHidden))
                .thenReturn(Completable.complete())

        testViewModel.onIgnoreHiddenStateChanged(testIgnoreHidden)

        verifyNoMoreInteractions(mockViewStateData, mockNavigateSingleData)
    }

    @Test
    fun testOnIgnoreHiddenStateChangedError_throw(){

        val testIgnoreHidden = false

        val testException = RuntimeException()
        expectedExceptionRule.expectCause(errorTypeAndEqualsCause(RuntimeException::class, testException))

        whenever(mockSaveIgnoreHiddenNumbersUseCase.build(testIgnoreHidden))
                .thenReturn(Completable.error(testException))

        testViewModel.onIgnoreHiddenStateChanged(testIgnoreHidden)
    }
}