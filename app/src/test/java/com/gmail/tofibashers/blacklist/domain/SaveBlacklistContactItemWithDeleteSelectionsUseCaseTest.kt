package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.MustBeNotSubscribedException
import com.gmail.tofibashers.blacklist.RxSchedulersOverrideRule
import com.gmail.tofibashers.blacklist.data.repo.*
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactPhoneMapper
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactPhoneWithActivityIntervalsMapper
import com.gmail.tofibashers.blacklist.utils.TimeFormatUtils
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.invocation.InvocationOnMock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*


/**
 * Created by TofiBashers on 26.04.2018.
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SaveBlacklistContactItemWithDeleteSelectionsUseCaseTest {

    @Mock
    lateinit var mockBlacklistContactItemRepository: IBlacklistContactItemRepository

    @Mock
    lateinit var mockBlacklistContactWithPhonesAndActivityIntervalsRepository: IBlacklistContactItemWithPhonesAndActivityIntervalsRepository

    @Mock
    lateinit var mockBlacklistContactPhoneWithActivityIntervalsRepository: IBlacklistContactPhoneWithActivityIntervalsRepository

    @Mock
    lateinit var mockBlacklistContactPhoneRepository: IBlacklistContactPhoneRepository

    @Mock
    lateinit var mockInteractionModeRepository: IInteractionModeRepository

    @Mock
    lateinit var mockDeleteAllSelectionsSyncUseCase: IDeleteAllSelectionsSyncUseCase

    @Mock
    lateinit var mockValidateBaseBlacklistPhoneForSaveSyncUseCase: IValidateBaseBlacklistPhoneForSaveSyncUseCase

    @Mock
    lateinit var mockBlacklistContactItemMapper: BlacklistContactItemMapper

    @Mock
    lateinit var mockBlacklistContactPhoneMapper: BlacklistContactPhoneMapper

    @Mock
    lateinit var mockBlacklistContactPhoneWithActivityIntervalsMapper: BlacklistContactPhoneWithActivityIntervalsMapper

    @InjectMocks
    lateinit var testUseCase: SaveBlacklistContactItemWithOnlyBlacklistPhonesWithDeleteSelectionsUseCase

    @Rule
    @JvmField
    val schedulersRule = RxSchedulersOverrideRule()

    @Test
    fun testOnNonSelectedMode_RuntimeException() {

        val testDeviceDbId = 0L
        val testDeviceKey = "ggfag"
        val testBlacklistContact =
                BlacklistContactItem(dbId = 0,
                        deviceDbId = testDeviceDbId,
                        deviceKey = testDeviceKey,
                        name = "name",
                        photoUrl = "http://")
        val testBlacklistContactPhones = listOf(
                BlacklistContactPhoneNumberItem(dbId = 2,
                        deviceDbId = 2,
                        number = "34",
                        isSmsBlocked = true,
                        isCallsBlocked = true))

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(Maybe.empty())
        whenever(mockDeleteAllSelectionsSyncUseCase.build())
                .thenReturn(Completable.error(MustBeNotSubscribedException()))

        val testObserver = testUseCase.build(testBlacklistContact, testBlacklistContactPhones)
                .test()

        testObserver.assertError(RuntimeException::class.java)
    }

    @Test
    fun testOnEditModeAndContactInvalid_OutdatedDataException() {

        val testData = generateEditModeAndContactInvalidWithoutPhonesWithIntervalsAndResult()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(testData.maybeInteractionMode)
        whenever(mockBlacklistContactItemRepository.getByDbId(testData.testInpContactItem.deviceDbId))
                .thenReturn(testData.testMaybeValidBlacklistContactById)
        whenever(mockBlacklistContactPhoneWithActivityIntervalsRepository.getSelectedList())
                .thenReturn(testData.testMaybeSelectedPhonesWithActivityIntervalsList)
        whenever(mockDeleteAllSelectionsSyncUseCase.build())
                .thenReturn(testData.expectedDeleteAllSelectionsRes)

        val testObserver = testUseCase.build(testData.testInpContactItem, testData.testInpContactPhones)
                .test()

        testObserver.assertError(OutdatedDataException::class.java)
    }

    @Test
    fun testOnEditModeWithValidContactWithoutPhonesAndIntervals_RuntimeException() {

        val testData = generateEditModeWithValidContactWithEmptyPhonesAndIntervalsAndResult()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(testData.maybeInteractionMode)
        whenever(mockBlacklistContactItemRepository.getByDbId(testData.testInpContactItem.dbId))
                .thenReturn(testData.testMaybeValidBlacklistContactById)
        whenever(mockBlacklistContactPhoneWithActivityIntervalsRepository.getSelectedList())
                .thenReturn(testData.testMaybeSelectedPhonesWithActivityIntervalsList)
        whenever(mockDeleteAllSelectionsSyncUseCase.build())
                .thenReturn(testData.expectedDeleteAllSelectionsRes)

        val testObserver = testUseCase.build(testData.testInpContactItem, testData.testInpContactPhones)
                .test()

        testObserver.assertError(RuntimeException::class.java)
    }

    @Test
    fun testOnCreateModeAndSelected_putSelectedIntervals() {

        val testDataWithResult = generateCreateModeWithSelectedPhonesAllValidPhoneIntervals()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(testDataWithResult.maybeInteractionMode)
        whenever(mockBlacklistContactPhoneWithActivityIntervalsRepository.getSelectedList())
                .thenReturn(testDataWithResult.testMaybeSelectedPhonesWithActivityIntervalsList)

        var indexOfValidatedPhoneWithInterval = 0
        doAnswer { invocationOnMock: InvocationOnMock ->
            val validResult = testDataWithResult.testPhonesWithUpdActivityIntervalsValidationForSaveResults!![indexOfValidatedPhoneWithInterval]
            indexOfValidatedPhoneWithInterval++
            return@doAnswer validResult
        }.whenever(mockValidateBaseBlacklistPhoneForSaveSyncUseCase).build(any())

        whenever(mockBlacklistContactItemMapper.toBlacklistContactItemWithPhonesAndIntervals(testDataWithResult.testInpContactItem,
                testDataWithResult.testPhonesWithUpdActivityIntervalsList!!))
                .thenReturn(testDataWithResult.expectedArgPutBlacklistContactItemWithPhonesAndIntervals)

        doAnswer { invocationOnMock: InvocationOnMock ->
            val phone = invocationOnMock.arguments[0] as BlacklistContactPhoneNumberItem
            val intervals = invocationOnMock.arguments[1] as List<ActivityInterval>
            return@doAnswer BlacklistContactPhoneWithActivityIntervals(
                    phone.dbId,
                    phone.deviceDbId,
                    phone.number,
                    phone.isCallsBlocked,
                    phone.isSmsBlocked,
                    intervals
            )
        }.whenever(mockBlacklistContactPhoneMapper).toBlacklistContactPhoneWithIntervals(any(), any())

        whenever(mockBlacklistContactWithPhonesAndActivityIntervalsRepository.put(testDataWithResult.expectedArgPutBlacklistContactItemWithPhonesAndIntervals!!))
                .thenReturn(testDataWithResult.expectedPutRes!!)
        whenever(mockDeleteAllSelectionsSyncUseCase.build())
                .thenReturn(testDataWithResult.expectedDeleteAllSelectionsRes)

        val testObserver = testUseCase.build(testDataWithResult.testInpContactItem, testDataWithResult.testInpContactPhones)
                .test()

        testObserver.assertComplete()
        verify(mockBlacklistContactWithPhonesAndActivityIntervalsRepository, atLeastOnce())
                .put(testDataWithResult.expectedArgPutBlacklistContactItemWithPhonesAndIntervals)
    }

    @Test
    fun testOnEditModeAndSelected_removeInvalidAndPutValidSelectedPhonesWithIntervals() {

        val testDataWithResult = generateEditModeWithSelecteValidAndInvalidPhonesWithIntervals()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(testDataWithResult.maybeInteractionMode)
        whenever(mockBlacklistContactPhoneWithActivityIntervalsRepository.getSelectedList())
                .thenReturn(testDataWithResult.testMaybeSelectedPhonesWithActivityIntervalsList!!)
        whenever(mockBlacklistContactItemRepository.getByDbId(testDataWithResult.testInpContactItem.deviceDbId))
                .thenReturn(testDataWithResult.testMaybeValidBlacklistContactById!!)

        var indexOfValidatedPhoneWithInterval = 0
        doAnswer { invocationOnMock: InvocationOnMock ->
            val validResult = testDataWithResult.testPhonesWithUpdActivityIntervalsValidationForSaveResults!![indexOfValidatedPhoneWithInterval]
            indexOfValidatedPhoneWithInterval++
            return@doAnswer validResult
        }.whenever(mockValidateBaseBlacklistPhoneForSaveSyncUseCase).build(any())

        whenever(mockBlacklistContactItemMapper.toBlacklistContactItemWithPhonesAndIntervals(
                testDataWithResult.testInpContactItem,
                testDataWithResult.testPhonesWithUpdActivityIntervalsList!!))
                .thenReturn(testDataWithResult.expectedArgPutBlacklistContactItemWithPhonesAndIntervals!!)

        doAnswer { invocationOnMock: InvocationOnMock ->
            val phone = invocationOnMock.arguments[0] as BlacklistContactPhoneNumberItem
            val intervals = invocationOnMock.arguments[1] as List<ActivityInterval>
            return@doAnswer BlacklistContactPhoneWithActivityIntervals(
                    phone.dbId,
                    phone.deviceDbId,
                    phone.number,
                    phone.isCallsBlocked,
                    phone.isSmsBlocked,
                    intervals
            )
        }.whenever(mockBlacklistContactPhoneMapper).toBlacklistContactPhoneWithIntervals(any(), any())

        doAnswer { invocationOnMock: InvocationOnMock ->
            val phoneWithIntervalsList = invocationOnMock.arguments[0] as List<BlacklistContactPhoneWithActivityIntervals>
            return@doAnswer phoneWithIntervalsList.map {
                BlacklistContactPhoneNumberItem(
                        it.dbId,
                        it.deviceDbId,
                        it.number,
                        it.isCallsBlocked,
                        it.isSmsBlocked
                )
            }
        }.whenever(mockBlacklistContactPhoneWithActivityIntervalsMapper).toBlacklistContactPhoneList(any())

        whenever(mockBlacklistContactPhoneRepository.removeAssociatedWithBlacklistContact(
                items = *testDataWithResult.testInvalidPhonesForDeleteResult!!,
                blacklistContact = testDataWithResult.testInpContactItem))
                .thenReturn(testDataWithResult.expectedDeleteInvalidPhonesRes!!)

        whenever(mockBlacklistContactWithPhonesAndActivityIntervalsRepository.put(testDataWithResult.expectedArgPutBlacklistContactItemWithPhonesAndIntervals))
                .thenReturn(testDataWithResult.expectedPutRes!!)
        whenever(mockDeleteAllSelectionsSyncUseCase.build())
                .thenReturn(testDataWithResult.expectedDeleteAllSelectionsRes)

        val testObserver = testUseCase.build(testDataWithResult.testInpContactItem, testDataWithResult.testInpContactPhones)
                .test()

        testObserver.assertComplete()
        verify(mockBlacklistContactWithPhonesAndActivityIntervalsRepository, atLeastOnce())
                .put(testDataWithResult.expectedArgPutBlacklistContactItemWithPhonesAndIntervals)
    }

    private fun generateCreateModeWithSelectedPhonesAllValidPhoneIntervals() : TestDataWithModeAndRes {

        val testContactDbId = 0L
        val testContactDeviceDbId = 0L
        val testContactDeviceKey = "ggfag"
        val testContactName = "name"
        val testContactPhotoUrl = "http://"
        val testInpContactItem = BlacklistContactItem(testContactDbId,
                testContactDeviceDbId,
                testContactDeviceKey,
                testContactName,
                testContactPhotoUrl)
        val testInpContactPhones = listOf(
                BlacklistContactPhoneNumberItem(dbId = 1,
                        deviceDbId = 1,
                        number = "12",
                        isSmsBlocked = true,
                        isCallsBlocked = true),
                BlacklistContactPhoneNumberItem(dbId = 2,
                        deviceDbId = 2,
                        number = "34",
                        isSmsBlocked = true,
                        isCallsBlocked = true))

        val nonLocalizedOrderWeekdayIds = IntRange(1, 7).toList()
        val resBeginTimeDefault = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME
        val resEndTimeDefault = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME

        val testSelectedIntervals = mutableListOf<ActivityInterval>().apply {
            for(weekdayId in nonLocalizedOrderWeekdayIds){
                add(ActivityInterval(null, weekdayId, resBeginTimeDefault, resEndTimeDefault))
            }
        }

        val testSelectedPhonesWithIntervals = testInpContactPhones.map {
            BlacklistContactPhoneWithActivityIntervals(it.dbId,
                    it.deviceDbId,
                    it.number,
                    false,
                    false,
                    testSelectedIntervals)
        }

        val testPhonesWithUpdIntervals = testInpContactPhones.map {
            BlacklistContactPhoneWithActivityIntervals(it.dbId,
                    it.deviceDbId,
                    it.number,
                    it.isCallsBlocked,
                    it.isSmsBlocked,
                    testSelectedIntervals)
        }

        val testPhonesWithUpdIntervalsValidResult = Collections.nCopies(testPhonesWithUpdIntervals.count(), Single.just(true))

        val expectedArgPutBlacklistContactItemWithPhonesAndIntervals = BlacklistContactItemWithPhonesAndIntervals(testContactDbId,
                testContactDeviceDbId,
                testContactDeviceKey,
                testContactName,
                testContactPhotoUrl,
                testPhonesWithUpdIntervals)

        return TestDataWithModeAndRes(
                maybeInteractionMode = Maybe.just(InteractionMode.CREATE),
                testInpContactItem = testInpContactItem,
                testInpContactPhones = testInpContactPhones,
                testMaybeValidBlacklistContactById = Maybe.just(testInpContactItem),
                testMaybeSelectedPhonesWithActivityIntervalsList = Maybe.just(testSelectedPhonesWithIntervals),
                testPhonesWithUpdActivityIntervalsList = testPhonesWithUpdIntervals,
                testPhonesWithUpdActivityIntervalsValidationForSaveResults = testPhonesWithUpdIntervalsValidResult,
                expectedDeleteAllSelectionsRes = Completable.complete(),
                expectedPutRes = Completable.complete(),
                expectedArgPutBlacklistContactItemWithPhonesAndIntervals = expectedArgPutBlacklistContactItemWithPhonesAndIntervals)
    }

    private fun generateEditModeWithSelecteValidAndInvalidPhonesWithIntervals() : TestDataWithModeAndRes {

        val testContactDbId = 0L
        val testContactDeviceDbId = 0L
        val testContactDeviceKey = "ggfag"
        val testContactName = "name"
        val testContactPhotoUrl = "http://"
        val testInpContactItem = BlacklistContactItem(testContactDbId,
                testContactDeviceDbId,
                testContactDeviceKey,
                testContactName,
                testContactPhotoUrl)
        val testInpContactPhones = listOf(
                BlacklistContactPhoneNumberItem(dbId = 1,
                        deviceDbId = 1,
                        number = "12",
                        isSmsBlocked = true,
                        isCallsBlocked = true),
                BlacklistContactPhoneNumberItem(dbId = 2,
                        deviceDbId = 2,
                        number = "34",
                        isSmsBlocked = true,
                        isCallsBlocked = true),
                BlacklistContactPhoneNumberItem(dbId = 3,
                        deviceDbId = 3,
                        number = "78",
                        isSmsBlocked = false,
                        isCallsBlocked = false))

        val nonLocalizedOrderWeekdayIds = IntRange(1, 7).toList()
        val resBeginTimeDefault = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME
        val resEndTimeDefault = TimeFormatUtils.MIDNIGHT_ISO_UNZONED_TIME

        val testSelectedIntervals = mutableListOf<ActivityInterval>().apply {
            for(weekdayId in nonLocalizedOrderWeekdayIds){
                add(ActivityInterval(null, weekdayId, resBeginTimeDefault, resEndTimeDefault))
            }
        }

        val testSelectedPhonesWithIntervals = testInpContactPhones.map {
            BlacklistContactPhoneWithActivityIntervals(it.dbId,
                    it.deviceDbId,
                    it.number,
                    false,
                    false,
                    testSelectedIntervals)
        }

        val testPhonesWithUpdIntervals = testInpContactPhones.subList(0,2)
                .map {
                    BlacklistContactPhoneWithActivityIntervals(it.dbId,
                            it.deviceDbId,
                            it.number,
                            it.isCallsBlocked,
                            it.isSmsBlocked,
                    testSelectedIntervals)
        }

        val testPhonesWithUpdIntervalsValidResult = listOf(
                Single.just(true),
                Single.just(true),
                Single.just(false))

        val expectedArgPutBlacklistContactItemWithPhonesAndIntervals = BlacklistContactItemWithPhonesAndIntervals(testContactDbId,
                testContactDeviceDbId,
                testContactDeviceKey,
                testContactName,
                testContactPhotoUrl,
                listOf(testPhonesWithUpdIntervals[0], testPhonesWithUpdIntervals[1]))

        return TestDataWithModeAndRes(
                maybeInteractionMode = Maybe.just(InteractionMode.EDIT),
                testInpContactItem = testInpContactItem,
                testInpContactPhones = testInpContactPhones,
                testMaybeValidBlacklistContactById = Maybe.just(testInpContactItem),
                testMaybeSelectedPhonesWithActivityIntervalsList = Maybe.just(testSelectedPhonesWithIntervals),
                testPhonesWithUpdActivityIntervalsList = testPhonesWithUpdIntervals,
                testPhonesWithUpdActivityIntervalsValidationForSaveResults = testPhonesWithUpdIntervalsValidResult,
                testInvalidPhonesForDeleteResult = arrayOf(testInpContactPhones[2]),
                expectedDeleteInvalidPhonesRes = Completable.complete(),
                expectedDeleteAllSelectionsRes = Completable.complete(),
                expectedPutRes = Completable.complete(),
                expectedArgPutBlacklistContactItemWithPhonesAndIntervals = expectedArgPutBlacklistContactItemWithPhonesAndIntervals)
    }

    private fun generateEditModeWithValidContactWithEmptyPhonesAndIntervalsAndResult() : TestDataWithModeAndRes {

        val testDeviceDbId = 0L
        val testDeviceKey = "ggfag"
        val testBlacklistContact =
                BlacklistContactItem(dbId = 0,
                        deviceDbId = testDeviceDbId,
                        deviceKey = testDeviceKey,
                        name = "name",
                        photoUrl = "http://")
        val testBlacklistContactPhones = listOf(
                BlacklistContactPhoneNumberItem(dbId = 2,
                        deviceDbId = 2,
                        number = "34",
                        isSmsBlocked = true,
                        isCallsBlocked = true))
        return TestDataWithModeAndRes(maybeInteractionMode = Maybe.just(InteractionMode.EDIT),
                testInpContactItem = testBlacklistContact,
                testInpContactPhones = testBlacklistContactPhones,
                testMaybeValidBlacklistContactById = Maybe.just(testBlacklistContact),
                testMaybeSelectedPhonesWithActivityIntervalsList = Maybe.empty(),
                expectedDeleteAllSelectionsRes = Completable.error(MustBeNotSubscribedException()))
    }


    private fun generateEditModeAndContactInvalidWithoutPhonesWithIntervalsAndResult() : TestDataWithModeAndRes {
        val testDeviceDbId = 0L
        val testDeviceKey = "ggfag"
        val testBlacklistContact =
                BlacklistContactItem(dbId = 0,
                        deviceDbId = testDeviceDbId,
                        deviceKey = testDeviceKey,
                        name = "name",
                        photoUrl = "http://")
        val testBlacklistContactPhones = listOf(
                BlacklistContactPhoneNumberItem(dbId = 2,
                        deviceDbId = 2,
                        number = "34",
                        isSmsBlocked = true,
                        isCallsBlocked = true))
        return TestDataWithModeAndRes(
                maybeInteractionMode = Maybe.just(InteractionMode.EDIT),
                testInpContactItem = testBlacklistContact,
                testInpContactPhones = testBlacklistContactPhones,
                testMaybeValidBlacklistContactById = Maybe.empty(),
                testMaybeSelectedPhonesWithActivityIntervalsList = Maybe.error(MustBeNotSubscribedException()),
                expectedDeleteAllSelectionsRes = Completable.error(MustBeNotSubscribedException()))
    }

    private data class TestDataWithCreatedIntervalsWithMaybeSelectedAndRes(val maybeInteractionMode: Maybe<InteractionMode>,
                                                                           val testInpContactItem: BlacklistContactItem,
                                                                           val testInpContactPhones: List<BlacklistContactPhoneNumberItem>,
                                                                           val testCreatedDefaultIntervals: Single<List<ActivityInterval>>,
                                                                           val testCreatedMultipleIntervals: List<List<ActivityInterval>>,
                                                                           val testMaybeSelectedMultipleActivityIntervalsList: Maybe<List<List<ActivityInterval>>>,
                                                                           val expectedArgPutBlacklistContactItemWithPhonesAndIntervals: BlacklistContactItemWithPhonesAndIntervals)

    private data class TestDataWithOnlySelectedIntervalsAndRes(val maybeInteractionMode: Maybe<InteractionMode>,
                                                               val testInpContactItem: BlacklistContactItem,
                                                               val testInpContactPhones: List<BlacklistContactPhoneNumberItem>,
                                                               val testSelectedMultipleIntervals: List<List<ActivityInterval>>,
                                                               val expectedArgPutBlacklistContactItemWithPhonesAndIntervals: BlacklistContactItemWithPhonesAndIntervals)

    private data class TestDataWithResWithMaybeValidItemAndMaybeSelectedIntervals(val maybeInteractionMode: Maybe<InteractionMode>,
                                                                                  val testInpContactItem: BlacklistContactItem,
                                                                                  val testInpContactPhones: List<BlacklistContactPhoneNumberItem>,
                                                                                  val testMaybeValidBlacklistContactByDevideIdAndKey: Maybe<BlacklistContactItem>,
                                                                                  val testMaybeSelectedMultipleActivityIntervalsList: Maybe<List<List<ActivityInterval>>>,
                                                                                  val expectedDeleteAllSelectionsRes: Completable,
                                                                                  val expectedArgPutBlacklistContactItemWithPhonesAndIntervals: BlacklistContactItemWithPhonesAndIntervals? = null)

    private data class TestDataWithModeAndRes(val maybeInteractionMode: Maybe<InteractionMode>,
                                              val testInpContactItem: BlacklistContactItem,
                                              val testInpContactPhones: List<BlacklistContactPhoneNumberItem>,
                                              val testMaybeValidBlacklistContactById: Maybe<BlacklistContactItem>? = null,
                                              val testMaybeSelectedPhonesWithActivityIntervalsList: Maybe<List<BlacklistContactPhoneWithActivityIntervals>>? = null,
                                              val testPhonesWithUpdActivityIntervalsList: List<BlacklistContactPhoneWithActivityIntervals>? = null,
                                              val testPhonesWithUpdActivityIntervalsValidationForSaveResults: List<Single<Boolean>>? = null,
                                              val testInvalidPhonesForDeleteResult: Array<BlacklistContactPhoneNumberItem>? = null,
                                              val expectedDeleteInvalidPhonesRes: Completable? = null,
                                              val expectedPutRes: Completable? = null,
                                              val expectedDeleteAllSelectionsRes: Completable,
                                              val expectedArgPutBlacklistContactItemWithPhonesAndIntervals: BlacklistContactItemWithPhonesAndIntervals? = null)

}