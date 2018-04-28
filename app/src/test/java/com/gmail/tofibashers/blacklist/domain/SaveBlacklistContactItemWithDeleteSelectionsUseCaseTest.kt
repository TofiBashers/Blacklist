package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.RxSchedulersOverrideRule
import com.gmail.tofibashers.blacklist.data.repo.IActivityIntervalRepository
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistContactItemRepository
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistContactItemWithPhonesAndActivityIntervalsRepository
import com.gmail.tofibashers.blacklist.data.repo.IInteractionModeRepository
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactPhoneMapper
import com.gmail.tofibashers.blacklist.utils.TimeFormatUtils
import com.nhaarman.mockito_kotlin.atLeastOnce
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*


/**
 * Created by TofiBashers on 26.04.2018.
 */
@RunWith(MockitoJUnitRunner::class)
class SaveBlacklistContactItemWithDeleteSelectionsUseCaseTest {

    @Mock
    lateinit var mockActivityIntervalRepository: IActivityIntervalRepository

    @Mock
    lateinit var mockBlacklistContactItemRepository: IBlacklistContactItemRepository

    @Mock
    lateinit var mockBlacklistContactWithPhonesAndActivityIntervalsRepository: IBlacklistContactItemWithPhonesAndActivityIntervalsRepository

    @Mock
    lateinit var mockInteractionModeRepository: IInteractionModeRepository

    @Mock
    lateinit var mockDeleteAllSelectionsSyncUseCase: IDeleteAllSelectionsSyncUseCase

    @Mock
    lateinit var mockCreateDefaultActivityIntervalsUseCase: ICreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase

    @Mock
    lateinit var mockBlacklistContactItemMapper: BlacklistContactItemMapper

    @Mock
    lateinit var mockBlacklistContactPhoneMapper: BlacklistContactPhoneMapper

    @InjectMocks
    lateinit var testUseCase: SaveBlacklistContactItemWithDeleteSelectionsUseCase

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
                .thenReturn(Completable.complete())

        val testObserver = testUseCase.build(testBlacklistContact, testBlacklistContactPhones)
                .test()

        testObserver.assertError(RuntimeException::class.java)
    }

    @Test
    fun testOnEditModeAndContactInvalid_OutdatedDataException() {

        val testData = generateEditModeAndContactInvalidWithoutIntervalsAndResult()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(testData.maybeInteractionMode)
        whenever(mockBlacklistContactItemRepository.getByDeviceIdAndDeviceKey(testData.testInpContactItem.deviceDbId, testData.testInpContactItem.deviceKey))
                .thenReturn(testData.testMaybeValidBlacklistContactByDevideIdAndKey)
        whenever(mockActivityIntervalRepository.getSelectedMultipleActivityIntervalsLists())
                .thenReturn(testData.testMaybeSelectedMultipleActivityIntervalsList)
        whenever(mockDeleteAllSelectionsSyncUseCase.build())
                .thenReturn(testData.expectedDeleteAllSelectionsRes)

        val testObserver = testUseCase.build(testData.testInpContactItem, testData.testInpContactPhones)
                .test()

        testObserver.assertError(OutdatedDataException::class.java)
    }

    @Test
    fun testOnEditModeWithValidContactWithoutIntervals_RuntimeException() {

        val testData = generateEditModeWithValidContactWithoutIntervalsAndResult()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(testData.maybeInteractionMode)
        whenever(mockBlacklistContactItemRepository.getByDeviceIdAndDeviceKey(testData.testInpContactItem.deviceDbId,
                testData.testInpContactItem.deviceKey))
                .thenReturn(testData.testMaybeValidBlacklistContactByDevideIdAndKey)
        whenever(mockActivityIntervalRepository.getSelectedMultipleActivityIntervalsLists())
                .thenReturn(testData.testMaybeSelectedMultipleActivityIntervalsList)
        whenever(mockDeleteAllSelectionsSyncUseCase.build())
                .thenReturn(testData.expectedDeleteAllSelectionsRes)

        val testObserver = testUseCase.build(testData.testInpContactItem, testData.testInpContactPhones)
                .test()

        testObserver.assertError(RuntimeException::class.java)
    }

    @Test
    fun testOnCreateModeAndSelectedNone_createAndPutDefaultIntervals() {

        val testDataWithResult = generateCreateModeWithCreatedDefaultIntervals()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(testDataWithResult.maybeInteractionMode)
        whenever(mockActivityIntervalRepository.getSelectedMultipleActivityIntervalsLists())
                .thenReturn(testDataWithResult.testMaybeSelectedMultipleActivityIntervalsList)
        whenever(mockCreateDefaultActivityIntervalsUseCase.build())
                .thenReturn(testDataWithResult.testCreatedDefaultIntervals)
        whenever(mockBlacklistContactItemMapper.toBlacklistContactItemWithPhonesAndIntervals(testDataWithResult.testInpContactItem,
                testDataWithResult.testInpContactPhones,
                testDataWithResult.testCreatedMultipleIntervals,
                mockBlacklistContactPhoneMapper))
                .thenReturn(testDataWithResult.expectedArgPutBlacklistContactItemWithPhonesAndIntervals)
        whenever(mockBlacklistContactWithPhonesAndActivityIntervalsRepository.put(testDataWithResult.expectedArgPutBlacklistContactItemWithPhonesAndIntervals))
                .thenReturn(Completable.complete())
        whenever(mockDeleteAllSelectionsSyncUseCase.build())
                .thenReturn(Completable.complete())

        val testObserver = testUseCase.build(testDataWithResult.testInpContactItem, testDataWithResult.testInpContactPhones)
                .test()

        testObserver.assertComplete()
        verify(mockBlacklistContactWithPhonesAndActivityIntervalsRepository, atLeastOnce())
                .put(testDataWithResult.expectedArgPutBlacklistContactItemWithPhonesAndIntervals)
    }

    @Test
    fun testOnCreateModeAndSelected_putSelectedIntervals() {

        val testDataWithResult = generateCreateModeWithSelectedIntervals()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(testDataWithResult.maybeInteractionMode)
        whenever(mockActivityIntervalRepository.getSelectedMultipleActivityIntervalsLists())
                .thenReturn(Maybe.just(testDataWithResult.testSelectedMultipleIntervals))
        whenever(mockBlacklistContactItemMapper.toBlacklistContactItemWithPhonesAndIntervals(testDataWithResult.testInpContactItem,
                testDataWithResult.testInpContactPhones,
                testDataWithResult.testSelectedMultipleIntervals,
                mockBlacklistContactPhoneMapper))
                .thenReturn(testDataWithResult.expectedArgPutBlacklistContactItemWithPhonesAndIntervals)
        whenever(mockBlacklistContactWithPhonesAndActivityIntervalsRepository.put(testDataWithResult.expectedArgPutBlacklistContactItemWithPhonesAndIntervals))
                .thenReturn(Completable.complete())
        whenever(mockDeleteAllSelectionsSyncUseCase.build())
                .thenReturn(Completable.complete())

        val testObserver = testUseCase.build(testDataWithResult.testInpContactItem, testDataWithResult.testInpContactPhones)
                .test()

        testObserver.assertComplete()
        verify(mockBlacklistContactWithPhonesAndActivityIntervalsRepository, atLeastOnce())
                .put(testDataWithResult.expectedArgPutBlacklistContactItemWithPhonesAndIntervals)
    }

    private fun generateCreateModeWithCreatedDefaultIntervals() : TestDataWithCreatedIntervalsWithMaybeSelectedAndRes {

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

        val testCreatedDefaultIntervals = mutableListOf<ActivityInterval>().apply {
            for(weekdayId in nonLocalizedOrderWeekdayIds){
                add(ActivityInterval(null, weekdayId, resBeginTimeDefault, resEndTimeDefault))
            }
        }

        val testCreatedDefaultMultipleIntervals = Collections.nCopies(testInpContactPhones.size, testCreatedDefaultIntervals)

        val testPhonesWithIntervals = testInpContactPhones.map {
            BlacklistContactPhoneWithActivityIntervals(it.dbId,
                    it.deviceDbId,
                    it.number,
                    it.isCallsBlocked,
                    it.isSmsBlocked,
                    testCreatedDefaultIntervals)
        }

        val expectedArgPutBlacklistContactItemWithPhonesAndIntervals = BlacklistContactItemWithPhonesAndIntervals(testContactDbId,
                testContactDeviceDbId,
                testContactDeviceKey,
                testContactName,
                testContactPhotoUrl,
                testPhonesWithIntervals)
        return TestDataWithCreatedIntervalsWithMaybeSelectedAndRes(Maybe.just(InteractionMode.CREATE),
                testInpContactItem,
                testInpContactPhones,
                Single.just(testCreatedDefaultIntervals),
                testCreatedDefaultMultipleIntervals,
                Maybe.empty(),
                expectedArgPutBlacklistContactItemWithPhonesAndIntervals)
    }

    private fun generateCreateModeWithSelectedIntervals() : TestDataWithOnlySelectedIntervalsAndRes {

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

        val testSelectedMultipleIntervals = Collections.nCopies(testInpContactPhones.size, testSelectedIntervals)

        val testPhonesWithIntervals = testInpContactPhones.map {
            BlacklistContactPhoneWithActivityIntervals(it.dbId,
                    it.deviceDbId,
                    it.number,
                    it.isCallsBlocked,
                    it.isSmsBlocked,
                    testSelectedIntervals)
        }

        val expectedArgPutBlacklistContactItemWithPhonesAndIntervals = BlacklistContactItemWithPhonesAndIntervals(testContactDbId,
                testContactDeviceDbId,
                testContactDeviceKey,
                testContactName,
                testContactPhotoUrl,
                testPhonesWithIntervals)
        return TestDataWithOnlySelectedIntervalsAndRes(Maybe.just(InteractionMode.CREATE),
                testInpContactItem,
                testInpContactPhones,
                testSelectedMultipleIntervals,
                expectedArgPutBlacklistContactItemWithPhonesAndIntervals)
    }

    private fun generateEditModeWithValidContactWithoutIntervalsAndResult() : TestDataWithResWithMaybeValidItemAndMaybeSelectedIntervals {

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
        return TestDataWithResWithMaybeValidItemAndMaybeSelectedIntervals(Maybe.just(InteractionMode.EDIT),
                testBlacklistContact,
                testBlacklistContactPhones,
                Maybe.just(testBlacklistContact),
                Maybe.empty(),
                Completable.complete(),
                null)
    }

    private fun generateEditModeAndContactInvalidWithoutIntervalsAndResult() : TestDataWithResWithMaybeValidItemAndMaybeSelectedIntervals {
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
        return TestDataWithResWithMaybeValidItemAndMaybeSelectedIntervals(Maybe.just(InteractionMode.EDIT),
                testBlacklistContact,
                testBlacklistContactPhones,
                Maybe.empty(),
                Maybe.empty(),
                Completable.complete(),
                null)
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
}