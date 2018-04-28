package com.gmail.tofibashers.blacklist.ui

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.*
import com.gmail.tofibashers.blacklist.entity.*
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
    lateinit var mockSelectPhoneNumberItemUseCase: ISelectEditModeAndPhoneNumberItemUseCase

    @Mock
    lateinit var mockSelectCreateModeUseCase: ISelectCreateModeUseCase

    @Mock
    lateinit var mockDeletePhoneNumberItemUseCase: IDeletePhoneNumberItemUseCase

    @Mock
    lateinit var mockSelectContactItemUseCase: ISelectEditModeAndContactItemUseCase

    @Mock
    lateinit var mockDeleteBlacklistContactItemUseCase: IDeleteBlacklistContactItemUseCase

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
                mockSelectPhoneNumberItemUseCase,
                mockSelectCreateModeUseCase,
                mockDeletePhoneNumberItemUseCase,
                mockSelectContactItemUseCase,
                mockDeleteBlacklistContactItemUseCase,
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
    fun testOnInitGetListsValues_setListsToView(){

        val testWarning = GetBlacklistResult.SystemVerWarning(SystemVerWarningType.MAY_UPDATE_TO_INCOMPATIBLE_VER)
        val testResults = listOf(
                GetBlacklistResult.ListWithIgnoreResult(listOf(
                        SectionBlacklistItem.Header(SectionBlacklistItem.Header.Type.NUMBERS),
                        SectionBlacklistItem.PhoneNumber(BlacklistPhoneNumberItem(number = "123",
                                isCallsBlocked = true,
                                isSmsBlocked = true)),
                        SectionBlacklistItem.Header(SectionBlacklistItem.Header.Type.CONTACTS),
                        SectionBlacklistItem.Contact(BlacklistContactItemWithNonIgnoredNumbersFlag(dbId = 0,
                                deviceDbId = 0,
                                deviceKey = "131",
                                name = "testContact",
                                photoUrl = "url",
                                withNonIgnoredNumbers = true))),
                        false),
                GetBlacklistResult.ListWithIgnoreResult(listOf(
                        SectionBlacklistItem.Header(SectionBlacklistItem.Header.Type.NUMBERS),
                        SectionBlacklistItem.PhoneNumber(BlacklistPhoneNumberItem(number = "789",
                                isCallsBlocked = true,
                                isSmsBlocked = true)),
                        SectionBlacklistItem.Header(SectionBlacklistItem.Header.Type.CONTACTS),
                        SectionBlacklistItem.Contact(BlacklistContactItemWithNonIgnoredNumbersFlag(dbId = 1,
                                deviceDbId = 1,
                                deviceKey = "145",
                                name = "testContact2",
                                photoUrl = "url2",
                                withNonIgnoredNumbers = true))),
                        true))

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
    fun testOnInitGetListSuccessThenPhoneItemChangeSuccess_navToOptions(){

        val testPosition = 0
        val testBlacklistPhoneItem = BlacklistPhoneNumberItem(10, "12", true, true)
        val mockLoadingViewState : BlacklistViewState.LoadingViewState = mock()
        val testScheduler = TestScheduler()
        val testGetResult = GetBlacklistResult.ListWithIgnoreResult(
                listOf(SectionBlacklistItem.PhoneNumber(testBlacklistPhoneItem)),
                ignoreHidden = true)

        whenever(mockGetBlacklistItemsUseCase.build())
                .thenReturn(Observable.just<GetBlacklistResult>(testGetResult)
                        .compose{ asNeverComplete(it) })
        whenever(mockSelectPhoneNumberItemUseCase.build(testBlacklistPhoneItem))
                .thenReturn(Completable.complete().observeOn(testScheduler))
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitGetList()
        testViewModel.onInitPhoneNumberItemChange(testPosition)

        verify(mockViewStateData, times(2)).value  = mockLoadingViewState
        verify(mockNavigateSingleData, times(0)).value = BlacklistNavRoute.OPTIONS

        testScheduler.triggerActions()

        verify(mockNavigateSingleData, times(1)).value = BlacklistNavRoute.OPTIONS
    }

    @Test
    fun testOnInitGetListSuccessThenPhoneItemChangeError_throw(){

        val testPosition = 0
        val testBlacklistPhoneItem = BlacklistPhoneNumberItem(10, "12", true, true)
        val testGetResult = GetBlacklistResult.ListWithIgnoreResult(
                listOf(SectionBlacklistItem.PhoneNumber(testBlacklistPhoneItem)),
                ignoreHidden = true)

        whenever(mockGetBlacklistItemsUseCase.build())
                .thenReturn(Observable.just<GetBlacklistResult>(testGetResult)
                        .compose{ asNeverComplete(it) })
        val testException = RuntimeException()
        expectedExceptionRule.expectCause(errorTypeAndEqualsCause(RuntimeException::class, testException))

        whenever(mockSelectPhoneNumberItemUseCase.build(testBlacklistPhoneItem))
                .thenReturn(Completable.error(testException))

        testViewModel.onInitGetList()
        testViewModel.onInitPhoneNumberItemChange(testPosition)
    }

    @Test
    fun testOnInitContactItemChangeSuccess_navToOptions(){

        val testPosition = 0
        val testBlacklistContactItem = BlacklistContactItemWithNonIgnoredNumbersFlag(dbId = 0,
                deviceDbId = 0,
                deviceKey = "123",
                name = "testContact",
                photoUrl = "url",
                withNonIgnoredNumbers = true)
        val mockLoadingViewState : BlacklistViewState.LoadingViewState = mock()
        val testScheduler = TestScheduler()
        val testGetResult = GetBlacklistResult.ListWithIgnoreResult(
                listOf(SectionBlacklistItem.Contact(testBlacklistContactItem)),
                ignoreHidden = true)

        whenever(mockGetBlacklistItemsUseCase.build())
                .thenReturn(Observable.just<GetBlacklistResult>(testGetResult)
                        .compose{ asNeverComplete(it) })
        whenever(mockSelectContactItemUseCase.build(testBlacklistContactItem))
                .thenReturn(Completable.complete().observeOn(testScheduler))
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitGetList()
        testViewModel.onInitContactItemChange(testPosition)

        verify(mockViewStateData, times(2)).value  = mockLoadingViewState
        verify(mockNavigateSingleData, times(0)).value = BlacklistNavRoute.BLACKLIST_CONTACT_OPTIONS

        testScheduler.triggerActions()

        verify(mockNavigateSingleData, times(1)).value = BlacklistNavRoute.BLACKLIST_CONTACT_OPTIONS
    }

    @Test
    fun testOnInitContactItemChangeError_throw(){

        val testPosition = 0
        val testBlacklistContactItem = BlacklistContactItemWithNonIgnoredNumbersFlag(dbId = 0,
                deviceDbId = 0,
                deviceKey = "123",
                name = "testContact",
                photoUrl = "url",
                withNonIgnoredNumbers = true)
        val testGetResult = GetBlacklistResult.ListWithIgnoreResult(
                listOf(SectionBlacklistItem.Contact(testBlacklistContactItem)),
                ignoreHidden = true)

        val testException = RuntimeException()
        expectedExceptionRule.expectCause(errorTypeAndEqualsCause(RuntimeException::class, testException))

        whenever(mockGetBlacklistItemsUseCase.build())
                .thenReturn(Observable.just<GetBlacklistResult>(testGetResult)
                        .compose{ asNeverComplete(it) })
        whenever(mockSelectContactItemUseCase.build(testBlacklistContactItem))
                .thenReturn(Completable.error(testException))

        testViewModel.onInitGetList()
        testViewModel.onInitContactItemChange(testPosition)
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
    fun testOnInitAddContactItemSuccess_navToSelectContact(){

        val mockLoadingViewState : BlacklistViewState.LoadingViewState = mock()
        val testScheduler = TestScheduler()

        whenever(mockSelectCreateModeUseCase.build())
                .thenReturn(Completable.complete()
                        .observeOn(testScheduler))
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitAddContactItem()

        verify(mockViewStateData, times(1)).value  = mockLoadingViewState
        verify(mockNavigateSingleData, times(0)).value = BlacklistNavRoute.SELECT_CONTACT

        testScheduler.triggerActions()

        verify(mockNavigateSingleData, times(1)).value = BlacklistNavRoute.SELECT_CONTACT
    }

    @Test
    fun testOnInitGetListSuccessThenPhoneItemDeleteSuccess_nothing(){

        val testPosition = 0
        val testBlacklistItem = BlacklistPhoneNumberItem(10, "12", true, true)
        val testGetResult = GetBlacklistResult.ListWithIgnoreResult(
                listOf(SectionBlacklistItem.PhoneNumber(testBlacklistItem)),
                ignoreHidden = true)
        val mockLoadingViewState : BlacklistViewState.LoadingViewState = mock()
        val mockListViewState : BlacklistViewState.ListViewState = mock()

        whenever(mockGetBlacklistItemsUseCase.build())
                .thenReturn(Observable.just<GetBlacklistResult>(testGetResult)
                        .compose{ asNeverComplete(it) })
        whenever(mockDeletePhoneNumberItemUseCase.build(testBlacklistItem))
                .thenReturn(Completable.complete())
        whenever(mockListViewStateFactory.create(any())).thenReturn(mockListViewState)
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitGetList()
        testViewModel.onInitPhoneNumberItemDelete(testPosition)

        verify(mockViewStateData, times(1)).value  = mockListViewState
        verify(mockViewStateData, times(2)).value  = mockLoadingViewState
        verifyNoMoreInteractions(mockViewStateData, mockNavigateSingleData)
    }

    @Test
    fun testOnInitGetListSuccessThenPhoneItemDeleteError_throw(){

        val testPosition = 0
        val testBlacklistItem = BlacklistPhoneNumberItem(10, "12", true, true)
        val testGetResult = GetBlacklistResult.ListWithIgnoreResult(
                listOf(SectionBlacklistItem.PhoneNumber(testBlacklistItem)),
                ignoreHidden = true)
        val testException = RuntimeException()
        expectedExceptionRule.expectCause(errorTypeAndEqualsCause(RuntimeException::class, testException))

        whenever(mockGetBlacklistItemsUseCase.build())
                .thenReturn(Observable.just<GetBlacklistResult>(testGetResult)
                        .compose{ asNeverComplete(it) })
        whenever(mockDeletePhoneNumberItemUseCase.build(testBlacklistItem))
                .thenReturn(Completable.error(testException))

        testViewModel.onInitGetList()
        testViewModel.onInitPhoneNumberItemDelete(testPosition)
    }

    @Test
    fun testOnInitGetListSuccessThenContactItemDeleteSuccess_nothing(){

        val testPosition = 0
        val testContactItem = BlacklistContactItemWithNonIgnoredNumbersFlag(dbId = 0,
                deviceDbId = 0,
                deviceKey = "123",
                name = "testContact",
                photoUrl = "url",
                withNonIgnoredNumbers = true)
        val mockLoadingViewState : BlacklistViewState.LoadingViewState = mock()
        val mockListViewState : BlacklistViewState.ListViewState = mock()
        val testGetResult = GetBlacklistResult.ListWithIgnoreResult(
                listOf(SectionBlacklistItem.Contact(testContactItem)),
                ignoreHidden = true)

        whenever(mockGetBlacklistItemsUseCase.build())
                .thenReturn(Observable.just<GetBlacklistResult>(testGetResult)
                        .compose{ asNeverComplete(it) })
        whenever(mockDeleteBlacklistContactItemUseCase.build(testContactItem))
                .thenReturn(Completable.complete())
        whenever(mockListViewStateFactory.create(any())).thenReturn(mockListViewState)
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitGetList()
        testViewModel.onInitContactItemDelete(testPosition)

        verify(mockViewStateData, times(1)).value = mockListViewState
        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        verifyNoMoreInteractions(mockViewStateData, mockNavigateSingleData)
    }

    @Test
    fun testOnInitGetListSuccessThenContactItemDeleteError_throw(){

        val testPosition = 0
        val testContactItem = BlacklistContactItemWithNonIgnoredNumbersFlag(dbId = 0,
                deviceDbId = 0,
                deviceKey = "123",
                name = "testContact",
                photoUrl = "url",
                withNonIgnoredNumbers = true)
        val testGetResult = GetBlacklistResult.ListWithIgnoreResult(
                listOf(SectionBlacklistItem.Contact(testContactItem)),
                ignoreHidden = true)

        val testException = RuntimeException()
        expectedExceptionRule.expectCause(errorTypeAndEqualsCause(RuntimeException::class, testException))

        whenever(mockGetBlacklistItemsUseCase.build())
                .thenReturn(Observable.just<GetBlacklistResult>(testGetResult)
                        .compose{ asNeverComplete(it) })
        whenever(mockDeleteBlacklistContactItemUseCase.build(testContactItem))
                .thenReturn(Completable.error(testException))

        testViewModel.onInitGetList()
        testViewModel.onInitContactItemDelete(testPosition)
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

    private fun <T> asNeverComplete(source: Observable<T>) : Observable<T> = Observable.concat(source, Observable.never())
}