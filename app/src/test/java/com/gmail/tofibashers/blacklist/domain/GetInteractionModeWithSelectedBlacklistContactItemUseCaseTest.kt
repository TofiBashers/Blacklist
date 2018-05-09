package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.RxSchedulersOverrideRule
import com.gmail.tofibashers.blacklist.data.repo.*
import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.entity.mapper.BlacklistContactItemMapper
import com.gmail.tofibashers.blacklist.entity.mapper.WhitelistContactItemMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


/**
 * Created by TofiBashers on 24.04.2018.
 */
@RunWith(MockitoJUnitRunner::class)
class GetInteractionModeWithSelectedBlacklistContactItemUseCaseTest {

    @Mock
    lateinit var mockInteractionModeRepository: IInteractionModeRepository

    @Mock
    lateinit var mockBlacklistContactItemRepository: IBlacklistContactItemRepository

    @Mock
    lateinit var mockBlacklistContactPhoneRepository: IBlacklistContactPhoneRepository

    @Mock
    lateinit var mockWhitelistContactItemRepository: IWhitelistContactItemRepository

    @Mock
    lateinit var mockWhitelistContactPhoneRepository: IWhitelistContactPhoneRepository

    @Mock
    lateinit var mockBlacklistContactItemMapper: BlacklistContactItemMapper

    @Mock
    lateinit var mockWhitelistContactItemMapper: WhitelistContactItemMapper

    @Mock
    lateinit var mockBlacklistContactPhoneItemFactory: BlacklistContactPhoneNumberItemFactory

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
    fun testOnInteractionModeCreateWithSelected_resultModeWithNumbersInvalidToSave(){

        val testData = generateCreateWithSelectedContactAndResWithNumbersInvalidToSave()

        whenever(mockInteractionModeRepository.getSelectedMode())
                .thenReturn(Maybe.just(InteractionMode.CREATE))
        whenever(mockWhitelistContactItemRepository.getSelected())
                .thenReturn(Maybe.just(testData.testSelectedWhitelistContact))
        whenever(mockWhitelistContactItemMapper.toBlacklistContact(testData.testSelectedWhitelistContact))
                .thenReturn(testData.testMappedBlacklistContact)
        whenever(mockWhitelistContactPhoneRepository.getAllAssociatedWithContactSortedByNumberAsc(testData.testSelectedWhitelistContact))
                .thenReturn(testData.testAssociatedContactNumbersSingle)
        doAnswer { invocationOnMock -> InteractionModeWithBlacklistContactItemAndNumbersAndValidState(
                invocationOnMock.arguments[0] as InteractionMode,
                invocationOnMock.arguments[1] as BlacklistContactItem,
                invocationOnMock.arguments[2] as List<BlacklistContactPhoneNumberItem>,
                invocationOnMock.arguments[3] as Boolean) }
                .whenever(mockInteractionModeWithBlacklistContactItemAndNumbersAndValidStateFactory).create(any(), any(), any(), any())
        doAnswer { invocationOnMock -> BlacklistContactPhoneNumberItem(
                invocationOnMock.arguments[0] as Long?,
                invocationOnMock.arguments[1] as Long?,
                invocationOnMock.arguments[2] as String,
                invocationOnMock.arguments[3] as Boolean,
                invocationOnMock.arguments[4] as Boolean) }
                .whenever(mockBlacklistContactPhoneItemFactory).create(anyOrNull(), anyOrNull(), any(), any(), any())

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
                .thenReturn(Maybe.just(InteractionMode.EDIT))
        whenever(mockBlacklistContactItemRepository.getSelected())
                .thenReturn(Maybe.just(testAndRes.testSelectedBlacklistContact))
        whenever(mockBlacklistContactPhoneRepository.getAllAssociatedWithBlacklistContact(testAndRes.testSelectedBlacklistContact))
                .thenReturn(testAndRes.testAssociatedBlacklistContactNumbersSingle)
        whenever(mockBlacklistContactItemMapper.toWhitelistContactItem(testAndRes.testSelectedBlacklistContact))
                .thenReturn(testAndRes.testMappedWhitelistContact)
        whenever(mockWhitelistContactPhoneRepository.getAllAssociatedWithContact(testAndRes.testMappedWhitelistContact))
                .thenReturn(testAndRes.testAssociatedContactNumbersSingle)
        doAnswer { invocationOnMock -> InteractionModeWithBlacklistContactItemAndNumbersAndValidState(
                invocationOnMock.arguments[0] as InteractionMode,
                invocationOnMock.arguments[1] as BlacklistContactItem,
                invocationOnMock.arguments[2] as List<BlacklistContactPhoneNumberItem>,
                invocationOnMock.arguments[3] as Boolean) }
                .whenever(mockInteractionModeWithBlacklistContactItemAndNumbersAndValidStateFactory).create(any(), any(), any(), any())
        doAnswer { invocationOnMock -> BlacklistContactPhoneNumberItem(
                invocationOnMock.arguments[0] as Long?,
                invocationOnMock.arguments[1] as Long?,
                invocationOnMock.arguments[2] as String,
                invocationOnMock.arguments[3] as Boolean,
                invocationOnMock.arguments[4] as Boolean) }
                .whenever(mockBlacklistContactPhoneItemFactory).create(anyOrNull(), anyOrNull(), any(), any(), any())

        val testObserver = testUseCase.build().test()

        testObserver.assertResult(testAndRes.expectedResult)
    }

    private fun generateCreateWithSelectedContactAndResWithNumbersInvalidToSave() : TestDataWithExpectedResCreateMode {
        val testBlacklistContactDbId = 0L
        val testDeviceDbId = 0L
        val testDeviceKey = "dsd"
        val testName = "name"
        val testPhotoUrl = "http://"
        val testSelectedContact = WhitelistContactItem(testDeviceDbId,
                testDeviceKey,
                testName,
                testPhotoUrl)
        val testMappedBlacklistContact = BlacklistContactItem(testBlacklistContactDbId,
                testDeviceDbId,
                testDeviceKey,
                testName,
                testPhotoUrl)
        val testAssociatedPhones = listOf(WhitelistContactPhone(deviceDbId = 0,
                number = "1234"))
        val resBlacklistPhones = listOf(BlacklistContactPhoneNumberItem(dbId = null,
                deviceDbId = 0,
                number = "1234",
                isSmsBlocked = false,
                isCallsBlocked = false))
        val resModeWithContactAndState = InteractionModeWithBlacklistContactItemAndNumbersAndValidState(InteractionMode.CREATE,
                testMappedBlacklistContact,
                resBlacklistPhones,
                false)
        return TestDataWithExpectedResCreateMode(testSelectedContact,
                testMappedBlacklistContact,
                Single.just(testAssociatedPhones),
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
        val testMappedWhitelistContact =
                WhitelistContactItem(testDeviceDbId,
                        testDeviceKey,
                        testName,
                        testPhotoUrl)
        val testAssociatedContactPhones = listOf(WhitelistContactPhone(deviceDbId = 0,
                number = "12"))
        val testAssociatedBlacklistContactPhones = listOf(
                BlacklistContactPhoneNumberItem(dbId = 2,
                        deviceDbId = 2,
                        number = "34",
                        isSmsBlocked = true,
                        isCallsBlocked = true),
                BlacklistContactPhoneNumberItem(dbId = 1,
                        deviceDbId = 1,
                        number = "23",
                        isSmsBlocked = true,
                        isCallsBlocked = true))
        val resBlacklistPhones = listOf(
                BlacklistContactPhoneNumberItem(dbId = null,
                        deviceDbId = 0,
                        number = "12",
                        isSmsBlocked = false,
                        isCallsBlocked = false),
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
        return TestDataWithExpectedResEditMode(testSelectedBlacklistContact,
                testMappedWhitelistContact,
                Single.just(testAssociatedContactPhones),
                Single.just(testAssociatedBlacklistContactPhones),
                resModeWithContactAndState)
    }

    private data class TestDataWithExpectedResCreateMode(val testSelectedWhitelistContact: WhitelistContactItem,
                                                         val testMappedBlacklistContact: BlacklistContactItem,
                                                         val testAssociatedContactNumbersSingle: Single<List<WhitelistContactPhone>>,
                                                         val expectedResult: InteractionModeWithBlacklistContactItemAndNumbersAndValidState)

    private data class TestDataWithExpectedResEditMode(val testSelectedBlacklistContact: BlacklistContactItem,
                                                       val testMappedWhitelistContact: WhitelistContactItem,
                                                       val testAssociatedContactNumbersSingle: Single<List<WhitelistContactPhone>>,
                                                       val testAssociatedBlacklistContactNumbersSingle: Single<List<BlacklistContactPhoneNumberItem>>,
                                                       val expectedResult: InteractionModeWithBlacklistContactItemAndNumbersAndValidState)
}