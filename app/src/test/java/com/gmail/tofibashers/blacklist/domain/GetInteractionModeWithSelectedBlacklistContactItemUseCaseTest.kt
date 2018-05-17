package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.RxSchedulersOverrideRule
import com.gmail.tofibashers.blacklist.data.repo.*
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactPhoneWithActivityIntervalsMapper
import com.gmail.tofibashers.blacklist.entity.mapper.WhitelistContactItemMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Maybe
import io.reactivex.Single
import org.joda.time.LocalTime
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


/**
 * Created by TofiBashers on 24.04.2018.
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class GetInteractionModeWithSelectedBlacklistContactItemUseCaseTest {

    @Mock
    lateinit var mockSelectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase:
            SelectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase

    @Mock
    lateinit var mockInteractionModeRepository: IInteractionModeRepository

    @Mock
    lateinit var mockBlacklistContactItemRepository: IBlacklistContactItemRepository

    @Mock
    lateinit var mockWhitelistContactItemRepository: IWhitelistContactItemRepository

    @Mock
    lateinit var mockBlacklistContactPhoneWithActivityIntervalsMapper: BlacklistContactPhoneWithActivityIntervalsMapper

    @Mock
    lateinit var mockWhitelistContactItemMapper: WhitelistContactItemMapper

    @Mock
    lateinit var mockInteractionModeWithBlacklistContactItemAndNumbersAndValidStateFactory: InteractionModeWithBlacklistContactItemAndNumbersAndValidStateFactory

    @InjectMocks
    lateinit var testUseCase: GetInteractionModeWithSelectedBlacklistContactItemUseCase

    @Rule
    @JvmField
    val schedulersRule = RxSchedulersOverrideRule()

    @Test
    fun testOnInteractionModeCreateWithoutSelected_RuntimeException(){

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(Maybe.just(InteractionMode.CREATE))
        whenever(mockWhitelistContactItemRepository.getSelected())
                .thenReturn(Maybe.empty())

        val testObserver = testUseCase.build().test()

        testObserver.assertError(RuntimeException::class.java)
    }

    @Test
    fun testOnInteractionModeCreateWithSelected_resultModeWithContactsInvalidToSave(){

        val testData = generateCreateWithSelectedContactAndResWithNumbersInvalidToSave()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(Maybe.just(testData.testInteractionMode))
        whenever(mockWhitelistContactItemRepository.getSelected())
                .thenReturn(Maybe.just(testData.testSelectedWhitelistContact))
        whenever(mockSelectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase.build(testData.testInteractionMode))
                .thenReturn(Single.just(testData.testMergedAndSortedByNumberBlacklistPhonesWithIntervals))
        whenever(mockWhitelistContactItemMapper.toBlacklistContact(testData.testSelectedWhitelistContact))
                .thenReturn(testData.testMappedBlacklistContact)
        whenever(mockBlacklistContactPhoneWithActivityIntervalsMapper.toBlacklistContactPhoneList(testData.testMergedAndSortedByNumberBlacklistPhonesWithIntervals))
                .thenReturn(testData.testMappedBlacklistPhones)
        doAnswer { invocationOnMock -> InteractionModeWithBlacklistContactItemAndNumbersAndValidState(
                invocationOnMock.arguments[0] as InteractionMode,
                invocationOnMock.arguments[1] as BlacklistContactItem,
                invocationOnMock.arguments[2] as List<BlacklistContactPhoneNumberItem>,
                invocationOnMock.arguments[3] as Boolean) }
                .whenever(mockInteractionModeWithBlacklistContactItemAndNumbersAndValidStateFactory).create(any(), any(), any(), any())

        val testObserver = testUseCase.build().test()

        testObserver.assertResult(testData.expectedResult)
    }

    @Test
    fun testOnInteractionModeEditWithoutSelected_RuntimeException(){

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(Maybe.just(InteractionMode.EDIT))
        whenever(mockBlacklistContactItemRepository.getSelected())
                .thenReturn(Maybe.empty())

        val testObserver = testUseCase.build().test()

        testObserver.assertError(RuntimeException::class.java)
    }

    @Test
    fun testOnInteractionModeEditWithSelected_resultModeWithNumbersValidToSave(){

        val testAndRes = generateEditWithSelectedContactWithBlacklistsAndWhitelistContactsAndResWithNumbersValidToSave()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(Maybe.just(testAndRes.testInteractionMode))
        whenever(mockBlacklistContactItemRepository.getSelected())
                .thenReturn(Maybe.just(testAndRes.testSelectedBlacklistContact))
        whenever(mockSelectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase.build(testAndRes.testInteractionMode))
                .thenReturn(Single.just(testAndRes.testMergedAndSortedByNumberBlacklistPhonesWithIntervals))
        whenever(mockBlacklistContactPhoneWithActivityIntervalsMapper.toBlacklistContactPhoneList(testAndRes.testMergedAndSortedByNumberBlacklistPhonesWithIntervals))
                .thenReturn(testAndRes.testMappedBlacklistPhones)
        doAnswer { invocationOnMock -> InteractionModeWithBlacklistContactItemAndNumbersAndValidState(
                invocationOnMock.arguments[0] as InteractionMode,
                invocationOnMock.arguments[1] as BlacklistContactItem,
                invocationOnMock.arguments[2] as List<BlacklistContactPhoneNumberItem>,
                invocationOnMock.arguments[3] as Boolean) }
                .whenever(mockInteractionModeWithBlacklistContactItemAndNumbersAndValidStateFactory).create(any(), any(), any(), any())

        val testObserver = testUseCase.build().test()

        testObserver.assertResult(testAndRes.expectedResult)
    }

    private fun generateCreateWithSelectedContactAndResWithNumbersInvalidToSave() : TestDataWithExpectedResCreateMode {
        val testBlacklistContactDbId = 0L
        val testDeviceDbId = 0L
        val testDeviceKey = "dsd"
        val testName = "name"
        val testPhotoUrl = "http://"

        val testPhoneDbId = null
        val testPhoneDeviceDbId = 0L
        val testPhoneNumber = "1234"
        val testIsCallsBlocked = false
        val testIsSmsBlocked = false

        val testSelectedContact = WhitelistContactItem(testDeviceDbId,
                testDeviceKey,
                testName,
                testPhotoUrl)

        val testMappedBlacklistContact = BlacklistContactItem(testBlacklistContactDbId,
                testDeviceDbId,
                testDeviceKey,
                testName,
                testPhotoUrl)

        val testPhonesWithIntervals = listOf(BlacklistContactPhoneWithActivityIntervals(dbId = testPhoneDbId,
                deviceDbId = testPhoneDeviceDbId,
                number = testPhoneNumber,
                isCallsBlocked = testIsCallsBlocked,
                isSmsBlocked = testIsSmsBlocked,
                activityIntervals = listOf(
                        ActivityInterval(weekDayId = 1,
                        beginTime = LocalTime.MIDNIGHT,
                        endTime = LocalTime.MIDNIGHT))
                ))

        val resBlacklistPhones = listOf(BlacklistContactPhoneNumberItem(dbId = testPhoneDbId,
                deviceDbId = testPhoneDeviceDbId,
                number = testPhoneNumber,
                isSmsBlocked = testIsSmsBlocked,
                isCallsBlocked = testIsCallsBlocked))
        val resModeWithContactAndState = InteractionModeWithBlacklistContactItemAndNumbersAndValidState(InteractionMode.CREATE,
                testMappedBlacklistContact,
                resBlacklistPhones,
                false)
        return TestDataWithExpectedResCreateMode(InteractionMode.CREATE,
                testSelectedContact,
                testMappedBlacklistContact,
                testPhonesWithIntervals,
                resBlacklistPhones,
                resModeWithContactAndState)
    }

    private fun generateEditWithSelectedContactWithBlacklistsAndWhitelistContactsAndResWithNumbersValidToSave() : TestDataWithExpectedResEditMode {
        val testBlacklistContactDbId = 0L
        val testDeviceDbId = 0L
        val testDeviceKey = "dsd"
        val testName = "name"
        val testPhotoUrl = "http://"
        val testSelectedBlacklistContact =
                BlacklistContactItem(testBlacklistContactDbId,
                        testDeviceDbId,
                        testDeviceKey,
                        testName,
                        testPhotoUrl)

        val testMergedPhonesSortedByNumber = listOf(
                BlacklistContactPhoneWithActivityIntervals(dbId = 1,
                        deviceDbId = 1,
                        number = "23",
                        isSmsBlocked = true,
                        isCallsBlocked = true,
                        activityIntervals = listOf(
                                ActivityInterval(weekDayId = 1,
                                        beginTime = LocalTime.MIDNIGHT,
                                        endTime = LocalTime.MIDNIGHT))),
                BlacklistContactPhoneWithActivityIntervals(dbId = 2,
                        deviceDbId = 2,
                        number = "34",
                        isSmsBlocked = true,
                        isCallsBlocked = true,
                        activityIntervals = listOf(
                                ActivityInterval(weekDayId = 1,
                                        beginTime = LocalTime.MIDNIGHT,
                                        endTime = LocalTime.MIDNIGHT))))

        val resBlacklistPhones = listOf(
                BlacklistContactPhoneNumberItem(dbId = 1,
                        deviceDbId = 1,
                        number = "23",
                        isSmsBlocked = true,
                        isCallsBlocked = true),
                BlacklistContactPhoneNumberItem(dbId = 2,
                        deviceDbId = 2,
                        number = "34",
                        isSmsBlocked = true,
                        isCallsBlocked = true))
        val resModeWithContactAndState = InteractionModeWithBlacklistContactItemAndNumbersAndValidState(InteractionMode.EDIT,
                testSelectedBlacklistContact,
                resBlacklistPhones,
                true)
        return TestDataWithExpectedResEditMode(InteractionMode.EDIT,
                testSelectedBlacklistContact,
                testMergedPhonesSortedByNumber,
                resBlacklistPhones,
                resModeWithContactAndState)
    }

    private data class TestDataWithExpectedResCreateMode(val testInteractionMode: InteractionMode,
                                                         val testSelectedWhitelistContact: WhitelistContactItem,
                                                         val testMappedBlacklistContact: BlacklistContactItem,
                                                         val testMergedAndSortedByNumberBlacklistPhonesWithIntervals: List<BlacklistContactPhoneWithActivityIntervals>,
                                                         val testMappedBlacklistPhones: List<BlacklistContactPhoneNumberItem>,
                                                         val expectedResult: InteractionModeWithBlacklistContactItemAndNumbersAndValidState)

    private data class TestDataWithExpectedResEditMode(val testInteractionMode: InteractionMode,
                                                       val testSelectedBlacklistContact: BlacklistContactItem,
                                                       val testMergedAndSortedByNumberBlacklistPhonesWithIntervals: List<BlacklistContactPhoneWithActivityIntervals>,
                                                       val testMappedBlacklistPhones: List<BlacklistContactPhoneNumberItem>,
                                                       val expectedResult: InteractionModeWithBlacklistContactItemAndNumbersAndValidState)
}