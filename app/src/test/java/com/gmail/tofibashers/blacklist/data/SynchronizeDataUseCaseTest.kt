package com.gmail.tofibashers.blacklist.data

import com.gmail.tofibashers.blacklist.RxSchedulersOverrideRule
import com.gmail.tofibashers.blacklist.data.datasource.IDatabaseSource
import com.gmail.tofibashers.blacklist.data.datasource.IDeviceDatasource
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactItem
import com.gmail.tofibashers.blacklist.data.db.entity.DbBlacklistContactPhoneItem
import com.gmail.tofibashers.blacklist.data.db.entity.mapper.DbBlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.data.device.DeviceContactItem
import com.gmail.tofibashers.blacklist.data.device.DeviceContactPhoneItem
import com.gmail.tofibashers.blacklist.data.device.mapper.DeviceContactItemMapper
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.CompletableTransformer
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.hamcrest.collection.IsIterableContainingInAnyOrder.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.invocation.InvocationOnMock
import org.mockito.junit.MockitoJUnitRunner


/**
 * Created by TofiBashers on 05.05.2018.
 */
@RunWith(MockitoJUnitRunner::class)
class SynchronizeDataUseCaseTest {

    private val oneSignalAndNeverFlowable = Flowable.concat(Flowable.just(Unit), Flowable.never())

    @Mock
    lateinit var mockDatabaseSource: IDatabaseSource

    @Mock
    lateinit var mockDeviceDatasource: IDeviceDatasource

    @Mock
    lateinit var mockDbBlacklistContactItemMapper: DbBlacklistContactItemMapper

    @Mock
    lateinit var mockDeviceContactItemMapper: DeviceContactItemMapper

    @InjectMocks
    lateinit var testUseCase: SynchronizeDataUseCase

    @Rule
    @JvmField
    val schedulersRule = RxSchedulersOverrideRule()

    @Before
    fun setUp() {
        whenever(mockDatabaseSource.inTransactionCompletable())
                .thenReturn(CompletableTransformer { it })
        doAnswer { invocationOnMock: InvocationOnMock ->
                val item = invocationOnMock.arguments[0] as DbBlacklistContactItem
                return@doAnswer DeviceContactItem(item.deviceDbId,
                        item.deviceLookupKey,
                        item.name,
                        item.photoUrl)
            }.whenever(mockDbBlacklistContactItemMapper).toDeviceContactItem(any())
        doAnswer { invocationOnMock: InvocationOnMock ->
            val item = invocationOnMock.arguments[0] as DeviceContactItem
            val id = invocationOnMock.arguments[1] as Long?
            return@doAnswer DbBlacklistContactItem(id, item.id, item.lookupKey, item.name, item.photoUrl)
        }.whenever(mockDeviceContactItemMapper).toDbBlacklistContactItem(any(), any())
    }

    @Test
    fun testOnDbContactsAndPhonesWithNonExistsAndNotUpdatedSuccess_putAndDelete(){

        val testUnchangedContactIds: List<Long> = listOf(1)
        val testDeletedContactIds: List<Long> = listOf(4, 5)
        val testChangedContactIds: List<Long> = listOf(7)

        val testUnchangedContactDeviceIds: List<Long> = listOf(636)
        val testChangedUpdContactDeviceIds: List<Long> = listOf(7474)

        val testUnchangedLookupKey = "435455"
        val testUnchangedName = "fdgd"
        val testUnchangedPhotoUrl = "http://"

        val testSrcDbBlacklistContacts = listOf(
                DbBlacklistContactItem(id = testUnchangedContactIds[0],
                        deviceDbId = testUnchangedContactDeviceIds[0],
                        deviceLookupKey = testUnchangedLookupKey,
                        name = testUnchangedName,
                        photoUrl = testUnchangedPhotoUrl),
                DbBlacklistContactItem(id = testDeletedContactIds[0],
                        deviceDbId = 2,
                        deviceLookupKey = "45634",
                        name = "name",
                        photoUrl = null),
                DbBlacklistContactItem(id = testDeletedContactIds[1],
                        deviceDbId = 3,
                        deviceLookupKey = "5854",
                        name = "name",
                        photoUrl = null),
                DbBlacklistContactItem(id = testChangedContactIds[0],
                        deviceDbId = 4,
                        deviceLookupKey = "3574",
                        name = "name",
                        photoUrl = null))

        val testMaybeChangedDeviceContacts = listOf(
                Maybe.just(DeviceContactItem(id = testUnchangedContactDeviceIds[0],
                        lookupKey = testUnchangedLookupKey,
                        name = testUnchangedName,
                        photoUrl = testUnchangedPhotoUrl)),
                Maybe.empty<DeviceContactItem>(),
                Maybe.empty<DeviceContactItem>(),
                Maybe.just(DeviceContactItem(id = testChangedUpdContactDeviceIds[0],
                        lookupKey = "435455",
                        name = "fdgd",
                        photoUrl = null)))

        val resChangedDbBlacklistContactsList = listOf(DbBlacklistContactItem(
                id = testChangedContactIds[0],
                deviceDbId = testChangedUpdContactDeviceIds[0],
                deviceLookupKey = "435455",
                name = "fdgd",
                photoUrl = null))

        val resDeletedDbBlacklistContactsList = listOf(testSrcDbBlacklistContacts[1],
                testSrcDbBlacklistContacts[2])


        val testUnchangedPhoneIds: List<Long> = listOf(1)
        val testDeletedPhoneIds: List<Long> = listOf(7, 8)
        val testUnchangedPhoneDeviceIds: List<Long> = listOf(1546)
        val testDeletedPhoneDeviceIds: List<Long> = listOf(7564, 8878)

        val testSrcDbPhoneItems = listOf(
                DbBlacklistContactPhoneItem(id = testUnchangedPhoneIds[0],
                        blacklistContactId = 5436,
                        deviceDbId = testUnchangedPhoneDeviceIds[0],
                        number = "213523",
                        ignoreCalls = true,
                        ignoreSms = true),
                DbBlacklistContactPhoneItem(id = testDeletedPhoneIds[0],
                        blacklistContactId = 8369,
                        deviceDbId = testDeletedPhoneDeviceIds[0],
                        number = "1234",
                        ignoreCalls = true,
                        ignoreSms = true),
                DbBlacklistContactPhoneItem(id = testDeletedPhoneIds[1],
                        blacklistContactId = 3634,
                        deviceDbId = testDeletedPhoneDeviceIds[1],
                        number = "1234",
                        ignoreCalls = true,
                        ignoreSms = true))

        val testMaybeDevicePhoneItems = listOf(
                Maybe.just(DeviceContactPhoneItem(id = testUnchangedPhoneDeviceIds[0],
                        number = "213523")),
                Maybe.empty<DeviceContactPhoneItem>(),
                Maybe.empty<DeviceContactPhoneItem>())

        val resDeletedDbBlacklistPhonesList = listOf(
                testSrcDbPhoneItems[1],
                testSrcDbPhoneItems[2])

        whenever(mockDeviceDatasource.getInitialAndAnyDeviceDataChangesSignal())
                .thenReturn(oneSignalAndNeverFlowable)
        whenever(mockDatabaseSource.getAllBlacklistContactItems())
                .thenReturn(Single.just(testSrcDbBlacklistContacts))
        doAnswer { invocationOnMock: InvocationOnMock ->
            val deviceDbId = invocationOnMock.arguments[0] as Long?
            val foundIndex = testSrcDbBlacklistContacts.indexOfFirst { it.deviceDbId == deviceDbId }
            return@doAnswer testMaybeChangedDeviceContacts[foundIndex]
        }.whenever(mockDeviceDatasource).getDeviceContactByIdAndLookupKey(any(), any())

        whenever(mockDatabaseSource.putBlacklistContactItems(any()))
                .thenReturn(Completable.complete())
        whenever(mockDatabaseSource.deleteBlacklistContactItems(any()))
                .thenReturn(Completable.complete())

        whenever(mockDatabaseSource.getAllBlacklistContactPhones())
                .thenReturn(Single.just(testSrcDbPhoneItems))
        doAnswer { invocationOnMock: InvocationOnMock ->
            val deviceDbId = invocationOnMock.arguments[0] as Long?
            val foundIndex = testSrcDbPhoneItems.indexOfFirst { it.deviceDbId == deviceDbId }
            return@doAnswer testMaybeDevicePhoneItems[foundIndex]
        }.whenever(mockDeviceDatasource).getDeviceContactPhoneWithIdAndNumber(any(), any())

        whenever(mockDatabaseSource.deleteBlacklistContactPhoneItems(any()))
                .thenReturn(Completable.complete())

        val testObserver = testUseCase.buildSyncOnSubscribeAndAfterAllChanges()
                .test()

        testObserver.assertNoErrors()

        val dbBlacklistPutContactItemsCaptor = argumentCaptor<List<DbBlacklistContactItem>>()
        val dbBlacklistDeleteContactItemsCaptor = argumentCaptor<List<DbBlacklistContactItem>>()
        val dbBlacklistDeletePhoneItemsCaptor = argumentCaptor<List<DbBlacklistContactPhoneItem>>()

        verify(mockDatabaseSource, times(1)).putBlacklistContactItems(dbBlacklistPutContactItemsCaptor.capture())
        verify(mockDatabaseSource, times(1)).deleteBlacklistContactItems(dbBlacklistDeleteContactItemsCaptor.capture())
        verify(mockDatabaseSource, times(1)).deleteBlacklistContactPhoneItems(dbBlacklistDeletePhoneItemsCaptor.capture())

        assertThat(dbBlacklistPutContactItemsCaptor.firstValue,
                containsInAnyOrder(*resChangedDbBlacklistContactsList.toTypedArray()))
        assertThat(dbBlacklistDeleteContactItemsCaptor.firstValue,
                containsInAnyOrder(*resDeletedDbBlacklistContactsList.toTypedArray()))
        assertThat(dbBlacklistDeletePhoneItemsCaptor.firstValue,
                containsInAnyOrder(*resDeletedDbBlacklistPhonesList.toTypedArray()))
    }

    @Test
    fun testOnDbContactsAndPhonesWithoutSuccess_notPutAndNotDelete(){

        val testUnchangedContactIds: List<Long> = listOf(1, 4, 5, 7)
        val testUnchangedLookupKeys = listOf("435455", "45634", "5854", "3574")
        val testUnchangedName = "fdgd"
        val testUnchangedPhotoUrl = "http://"

        val testUnchangedContactDeviceIds: List<Long> = listOf(636, 7474, 5645, 4573)

        val testSrcDbBlacklistContacts = listOf(
                DbBlacklistContactItem(id = testUnchangedContactIds[0],
                        deviceDbId = testUnchangedContactDeviceIds[0],
                        deviceLookupKey = testUnchangedLookupKeys[0],
                        name = testUnchangedName,
                        photoUrl = testUnchangedPhotoUrl),
                DbBlacklistContactItem(id = testUnchangedContactIds[1],
                        deviceDbId = testUnchangedContactDeviceIds[1],
                        deviceLookupKey = testUnchangedLookupKeys[1],
                        name = testUnchangedName,
                        photoUrl = testUnchangedPhotoUrl),
                DbBlacklistContactItem(id = testUnchangedContactIds[2],
                        deviceDbId = testUnchangedContactDeviceIds[2],
                        deviceLookupKey = testUnchangedLookupKeys[2],
                        name = testUnchangedName,
                        photoUrl = testUnchangedPhotoUrl),
                DbBlacklistContactItem(id = testUnchangedContactIds[3],
                        deviceDbId = testUnchangedContactDeviceIds[3],
                        deviceLookupKey = testUnchangedLookupKeys[3],
                        name = testUnchangedName,
                        photoUrl = testUnchangedPhotoUrl))

        val testMaybeChangedDeviceContacts = listOf(
                Maybe.just(DeviceContactItem(id = testUnchangedContactDeviceIds[0],
                        lookupKey = testUnchangedLookupKeys[0],
                        name = testUnchangedName,
                        photoUrl = testUnchangedPhotoUrl)),
                Maybe.just(DeviceContactItem(id = testUnchangedContactDeviceIds[1],
                        lookupKey = testUnchangedLookupKeys[1],
                        name = testUnchangedName,
                        photoUrl = testUnchangedPhotoUrl)),
                Maybe.just(DeviceContactItem(id = testUnchangedContactDeviceIds[2],
                        lookupKey = testUnchangedLookupKeys[2],
                        name = testUnchangedName,
                        photoUrl = testUnchangedPhotoUrl)),
                Maybe.just(DeviceContactItem(id = testUnchangedContactDeviceIds[3],
                        lookupKey = testUnchangedLookupKeys[3],
                        name = testUnchangedName,
                        photoUrl = testUnchangedPhotoUrl)))

        val testUnchangedPhoneIds: List<Long> = listOf(1, 7, 8)
        val testUnchangedPhoneDeviceIds: List<Long> = listOf(1546, 7564, 8878)
        val testUnchangedNumber = "213523"

        val testSrcDbPhoneItems = listOf(
                DbBlacklistContactPhoneItem(id = testUnchangedPhoneIds[0],
                        blacklistContactId = 5436,
                        deviceDbId = testUnchangedPhoneDeviceIds[0],
                        number = testUnchangedNumber,
                        ignoreCalls = true,
                        ignoreSms = true),
                DbBlacklistContactPhoneItem(id = testUnchangedPhoneIds[1],
                        blacklistContactId = 8369,
                        deviceDbId = testUnchangedPhoneDeviceIds[1],
                        number = testUnchangedNumber,
                        ignoreCalls = true,
                        ignoreSms = true),
                DbBlacklistContactPhoneItem(id = testUnchangedPhoneIds[2],
                        blacklistContactId = 3634,
                        deviceDbId = testUnchangedPhoneDeviceIds[2],
                        number = testUnchangedNumber,
                        ignoreCalls = true,
                        ignoreSms = true))

        val testMaybeDevicePhoneItems = listOf(
                Maybe.just(DeviceContactPhoneItem(id = testUnchangedPhoneDeviceIds[0],
                        number = testUnchangedNumber)),
                Maybe.just(DeviceContactPhoneItem(id = testUnchangedPhoneDeviceIds[1],
                        number = testUnchangedNumber)),
                Maybe.just(DeviceContactPhoneItem(id = testUnchangedPhoneDeviceIds[2],
                        number = testUnchangedNumber)))

        whenever(mockDeviceDatasource.getInitialAndAnyDeviceDataChangesSignal())
                .thenReturn(oneSignalAndNeverFlowable)
        whenever(mockDatabaseSource.getAllBlacklistContactItems())
                .thenReturn(Single.just(testSrcDbBlacklistContacts))
        doAnswer { invocationOnMock: InvocationOnMock ->
            val deviceDbId = invocationOnMock.arguments[0] as Long?
            val foundIndex = testSrcDbBlacklistContacts.indexOfFirst { it.deviceDbId == deviceDbId }
            return@doAnswer testMaybeChangedDeviceContacts[foundIndex]
        }.whenever(mockDeviceDatasource).getDeviceContactByIdAndLookupKey(any(), any())

        whenever(mockDatabaseSource.getAllBlacklistContactPhones())
                .thenReturn(Single.just(testSrcDbPhoneItems))
        doAnswer { invocationOnMock: InvocationOnMock ->
            val deviceDbId = invocationOnMock.arguments[0] as Long?
            val foundIndex = testSrcDbPhoneItems.indexOfFirst { it.deviceDbId == deviceDbId }
            return@doAnswer testMaybeDevicePhoneItems[foundIndex]
        }.whenever(mockDeviceDatasource).getDeviceContactPhoneWithIdAndNumber(any(), any())

        val testObserver = testUseCase.buildSyncOnSubscribeAndAfterAllChanges()
                .test()

        testObserver.assertNoErrors()

        verify(mockDatabaseSource, times(0)).putBlacklistContactItems(any())
        verify(mockDatabaseSource, times(0)).deleteBlacklistContactItems(any())
        verify(mockDatabaseSource, times(0)).deleteBlacklistContactPhoneItems(any())
    }

    @Test
    fun testOnDbContactsAndPhonesAllEmptySuccess_notPutContactsAndPutEmptyPhonesList(){

        val testSrcDbBlacklistContacts = emptyList<DbBlacklistContactItem>()

        val testSrcDbPhoneItems = emptyList<DbBlacklistContactPhoneItem>()

        whenever(mockDeviceDatasource.getInitialAndAnyDeviceDataChangesSignal())
                .thenReturn(oneSignalAndNeverFlowable)
        whenever(mockDatabaseSource.getAllBlacklistContactItems())
                .thenReturn(Single.just(testSrcDbBlacklistContacts))
        whenever(mockDatabaseSource.getAllBlacklistContactPhones())
                .thenReturn(Single.just(testSrcDbPhoneItems))

        val testObserver = testUseCase.buildSyncOnSubscribeAndAfterAllChanges()
                .test()

        testObserver.assertNoErrors()

        verify(mockDatabaseSource, times(0)).putBlacklistContactItems(any())
        verify(mockDatabaseSource, times(0)).deleteBlacklistContactItems(any())
        verify(mockDatabaseSource, times(0)).deleteBlacklistContactPhoneItems(any())
    }
}