package com.gmail.tofibashers.blacklist.ui

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.IGetAllNonIgnoredContactsWithChangesUseCase
import com.gmail.tofibashers.blacklist.domain.ISelectContactItemUseCase
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsNavData
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsViewState
import com.gmail.tofibashers.blacklist.ui.common.SavingResult
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import com.gmail.tofibashers.blacklist.ui.select_contact.*
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


/**
 * Created by TofiBashers on 23.04.2018.
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectContactViewModelTest {

    @Mock
    lateinit var mockSelectContactItemUseCase: ISelectContactItemUseCase

    @Mock
    lateinit var mockGetAllNonIgnoredContactsUseCase: IGetAllNonIgnoredContactsWithChangesUseCase

    @Mock
    lateinit var mockParentRouteFactory: SelectContactNavData_ParentRouteFactory

    @Mock
    lateinit var mockEditContactRouteFactory: SelectContactNavData_EditContactRouteFactory

    @Mock
    lateinit var mockLoadingViewStateFactory: SelectContactViewState_LoadingViewStateFactory

    @Mock
    lateinit var mockDataViewStateFactory: SelectContactViewState_DataViewStateFactory

    @Mock
    lateinit var mockViewStateData: MutableLiveData<SelectContactViewState>

    @Mock
    lateinit var mockNavigateSingleData: SingleLiveEvent<SelectContactNavData>

    private lateinit var testViewModel: SelectContactViewModel

    @Before
    fun setUp() {
        testViewModel = SelectContactViewModel(mockSelectContactItemUseCase,
                mockGetAllNonIgnoredContactsUseCase,
                mockParentRouteFactory,
                mockEditContactRouteFactory,
                mockLoadingViewStateFactory,
                mockDataViewStateFactory,
                mockViewStateData,
                mockNavigateSingleData)
    }

    @Test
    fun onInitGetLists_setListsData() {

        val testResults = listOf(
                listOf(WhitelistContactItemWithHasPhones(deviceDbId = 0,
                        deviceKey = "34234",
                        name = "nameA",
                        photoUrl = "http://",
                        hasPhones = true)),
                listOf(WhitelistContactItemWithHasPhones(deviceDbId = 1,
                        deviceKey = "645457",
                        name = "nameB",
                        photoUrl = "http://",
                        hasPhones = true)))
        val mockLoadingViewState : SelectContactViewState.LoadingViewState = mock()
        val testDataSubject = PublishSubject.create<List<WhitelistContactItemWithHasPhones>>()

        whenever(mockGetAllNonIgnoredContactsUseCase.build())
                .thenReturn(testDataSubject)
        doAnswer { invocationOnMock ->
            SelectContactViewState.DataViewState(invocationOnMock.arguments[0] as List<WhitelistContactItemWithHasPhones>) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create()).thenReturn(mockLoadingViewState)

        testViewModel.onInitGetList()

        verify(mockViewStateData, times(1)).value  = mockLoadingViewState
        verify(mockViewStateData, times(0)).value = SelectContactViewState.DataViewState(testResults[0])
        verify(mockViewStateData, times(0)).value = SelectContactViewState.DataViewState(testResults[1])

        testDataSubject.onNext(testResults[0])
        verify(mockViewStateData, times(1)).value  = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = SelectContactViewState.DataViewState(testResults[0])
        verify(mockViewStateData, times(0)).value = SelectContactViewState.DataViewState(testResults[1])

        testDataSubject.onNext(testResults[1])
        verify(mockViewStateData, times(1)).value  = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = SelectContactViewState.DataViewState(testResults[0])
        verify(mockViewStateData, times(1)).value = SelectContactViewState.DataViewState(testResults[1])
    }

    @Test
    fun onInitGetListsAndThenChangeContactSuccess_navToEditContact() {

        val testPosition = 0
        val testDeviceDbId = 0L
        val testDeviceKey = "34234"
        val testResult = listOf(WhitelistContactItemWithHasPhones(testDeviceDbId,
                        testDeviceKey,
                        name = "nameA",
                        photoUrl = "http://",
                        hasPhones = true))
        val mockLoadingViewState : SelectContactViewState.LoadingViewState = mock()
        val testEditRoute = SelectContactNavData.EditContactRoute(testDeviceDbId, testDeviceKey)

        whenever(mockGetAllNonIgnoredContactsUseCase.build())
                .thenReturn(Observable.just(testResult)
                        .compose { asNeverComplete(it) })
        doAnswer { invocationOnMock ->
            SelectContactViewState.DataViewState(invocationOnMock.arguments[0] as List<WhitelistContactItemWithHasPhones>) }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create())
                .thenReturn(mockLoadingViewState)
        whenever(mockEditContactRouteFactory.create(testDeviceDbId, testDeviceKey))
                .thenReturn(testEditRoute)

        testViewModel.onInitGetList()

        verify(mockViewStateData, times(1)).value  = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = SelectContactViewState.DataViewState(testResult)
        verify(mockNavigateSingleData, times(0)).value = any()

        testViewModel.onInitChangeContact(testPosition)

        verify(mockViewStateData, times(1)).value  = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = SelectContactViewState.DataViewState(testResult)
        verify(mockNavigateSingleData, times(1)).value = testEditRoute
    }

    @Test
    fun onInitGetListsAndThenSelectContactSuccess_navToEditContact() {

        val testPosition = 0
        val testContactItem = WhitelistContactItemWithHasPhones(0,
                "34234",
                name = "nameA",
                photoUrl = "http://",
                hasPhones = true)
        val testResult = listOf(testContactItem)
        val mockLoadingViewState: SelectContactViewState.LoadingViewState = mock()
        val testOptRoute = SelectContactNavData.ParentRoute(SavingResult.SAVED)
        val testSelectScheduler = TestScheduler()

        whenever(mockGetAllNonIgnoredContactsUseCase.build())
                .thenReturn(Observable.just(testResult)
                        .compose { asNeverComplete(it) })
        whenever(mockSelectContactItemUseCase.build(testContactItem))
                .thenReturn(Completable.complete()
                        .observeOn(testSelectScheduler))
        doAnswer { invocationOnMock ->
            SelectContactViewState.DataViewState(invocationOnMock.arguments[0] as List<WhitelistContactItemWithHasPhones>)
        }
                .whenever(mockDataViewStateFactory).create(any())
        whenever(mockLoadingViewStateFactory.create())
                .thenReturn(mockLoadingViewState)
        whenever(mockParentRouteFactory.create(SavingResult.SAVED))
                .thenReturn(testOptRoute)

        testViewModel.onInitGetList()
        testViewModel.onInitSelectContact(testPosition)

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = SelectContactViewState.DataViewState(testResult)
        verify(mockNavigateSingleData, times(0)).value = any()

        testSelectScheduler.triggerActions()

        verify(mockViewStateData, times(2)).value = mockLoadingViewState
        verify(mockViewStateData, times(1)).value = SelectContactViewState.DataViewState(testResult)
        verify(mockNavigateSingleData, times(1)).value = testOptRoute
    }

    private fun <T> asNeverComplete(source: Observable<T>) : Observable<T> = Observable.concat(source, Observable.never())
}