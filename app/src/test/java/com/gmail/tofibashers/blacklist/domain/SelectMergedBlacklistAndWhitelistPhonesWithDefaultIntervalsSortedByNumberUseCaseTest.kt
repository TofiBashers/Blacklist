package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.MustBeNotSubscribedException
import com.gmail.tofibashers.blacklist.RxSchedulersOverrideRule
import com.gmail.tofibashers.blacklist.data.repo.IBlacklistContactPhoneWithActivityIntervalsRepository
import com.gmail.tofibashers.blacklist.data.repo.IWhitelistContactPhoneRepository
import com.gmail.tofibashers.blacklist.entity.*
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
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
 * Created by TofiBashers on 16.05.2018.
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCaseTest {

    @Mock
    lateinit var mockCreateDefaultActivityIntervalsUseCase: ICreateDefaultActivityIntervalsForNonLocalizedWeekSyncUseCase

    @Mock
    lateinit var mockWhitelistContactPhoneRepository: IWhitelistContactPhoneRepository

    @Mock
    lateinit var mockBlacklistContactPhoneWithActivityIntervalsRepository: IBlacklistContactPhoneWithActivityIntervalsRepository

    @Mock
    lateinit var mockBlacklistContactPhoneWithActivityIntervalsFactory: BlacklistContactPhoneWithActivityIntervalsFactory

    @InjectMocks
    lateinit var testUseCase: SelectMergedBlacklistAndWhitelistPhonesWithDefaultIntervalsSortedByNumberUseCase

    @Rule
    @JvmField
    val schedulersRule = RxSchedulersOverrideRule()

    @Test
    fun testOnWhitelistPhoneNotSelected_RuntimeException(){

        val testInteractionMode = InteractionMode.CREATE

        whenever(mockWhitelistContactPhoneRepository.getSelectedList())
                .thenReturn(Maybe.empty())

        val testObserver = testUseCase.build(testInteractionMode).test()

        testObserver.assertError(RuntimeException::class.java)

        verifyZeroInteractions(mockCreateDefaultActivityIntervalsUseCase,
                mockBlacklistContactPhoneWithActivityIntervalsFactory,
                mockBlacklistContactPhoneWithActivityIntervalsRepository)
    }

    @Test
    fun testOnAllPhonesSelectedInEditMode_sortByNumberAndPut(){

        val testDataWithRes = generateAllPhonesSelectedInEditMode()

        whenever(mockWhitelistContactPhoneRepository.getSelectedList())
                .thenReturn(testDataWithRes.testMaybeSelectedWhitelistContactPhones!!)
        whenever(mockBlacklistContactPhoneWithActivityIntervalsRepository.getSelectedList())
                .thenReturn(testDataWithRes.testMaybeSelectedBlacklistContactPhonesWithIntervals!!)
        whenever(mockCreateDefaultActivityIntervalsUseCase.build())
                .thenReturn(testDataWithRes.testSingleDefaultIntervals!!)
        whenever(mockBlacklistContactPhoneWithActivityIntervalsRepository.putSelectedList(any()))
                .thenReturn(Completable.complete())

        doAnswer { invocationOnMock -> BlacklistContactPhoneWithActivityIntervals(
                invocationOnMock.arguments[0] as Long?,
                invocationOnMock.arguments[1] as Long?,
                invocationOnMock.arguments[2] as String,
                invocationOnMock.arguments[3] as Boolean,
                invocationOnMock.arguments[4] as Boolean,
                invocationOnMock.arguments[5] as List<ActivityInterval>) }
                .whenever(mockBlacklistContactPhoneWithActivityIntervalsFactory).create(anyOrNull(), anyOrNull(), any(), any(), any(), any())

        val testObserver = testUseCase.build(testDataWithRes.testInpInteractionMode).test()

        testObserver.assertComplete()
        testObserver.assertValue(testDataWithRes.resPhonesWithIntervals)
    }

    @Test
    fun testOblyWhitelistPhonesSelectedInCreateMode_sortByNumberAndPut(){

        val testDataWithRes = generateOblyWhitelistPhonesSelectedInCreateMode()

        whenever(mockWhitelistContactPhoneRepository.getSelectedList())
                .thenReturn(testDataWithRes.testMaybeSelectedWhitelistContactPhones!!)
        whenever(mockCreateDefaultActivityIntervalsUseCase.build())
                .thenReturn(testDataWithRes.testSingleDefaultIntervals!!)
        whenever(mockBlacklistContactPhoneWithActivityIntervalsRepository.putSelectedList(any()))
                .thenReturn(Completable.complete())

        doAnswer { invocationOnMock -> BlacklistContactPhoneWithActivityIntervals(
                invocationOnMock.arguments[0] as Long?,
                invocationOnMock.arguments[1] as Long?,
                invocationOnMock.arguments[2] as String,
                invocationOnMock.arguments[3] as Boolean,
                invocationOnMock.arguments[4] as Boolean,
                invocationOnMock.arguments[5] as List<ActivityInterval>) }
                .whenever(mockBlacklistContactPhoneWithActivityIntervalsFactory).create(anyOrNull(), anyOrNull(), any(), any(), any(), any())

        val testObserver = testUseCase.build(testDataWithRes.testInpInteractionMode).test()

        testObserver.assertComplete()
        testObserver.assertValue(testDataWithRes.resPhonesWithIntervals)
    }

    @Test
    fun testOnlyWhitelistContactPhonesSelectedInEditMode_RuntimeException(){

        val testDataWithRes = generateOnlyWhitelistContactPhonesSelectedInEditMode()

        whenever(mockWhitelistContactPhoneRepository.getSelectedList())
                .thenReturn(testDataWithRes.testMaybeSelectedWhitelistContactPhones!!)
        whenever(mockCreateDefaultActivityIntervalsUseCase.build())
                .thenReturn(testDataWithRes.testSingleDefaultIntervals!!)

        val testObserver = testUseCase.build(testDataWithRes.testInpInteractionMode).test()

        testObserver.assertError(RuntimeException::class.java)
        verifyZeroInteractions(mockBlacklistContactPhoneWithActivityIntervalsRepository)
    }

    private fun generateAllPhonesSelectedInEditMode() : TestDataWithExpectedRes{

        val testDefaultIntervals = mutableListOf<ActivityInterval>().apply{
            for(weekdayId in IntRange(1, 7)){
                add(ActivityInterval(weekDayId = weekdayId,
                        beginTime = LocalTime.MIDNIGHT,
                        endTime = LocalTime.MIDNIGHT))
            }
        }

        val testWhitelistPhonesUnsorted = listOf(
                WhitelistContactPhone(
                        deviceDbId = 0L,
                        number = "634"),
                WhitelistContactPhone(
                        deviceDbId = 1L,
                        number = "547"))

        val testBlacklistPhonesUnsorted = listOf(
                BlacklistContactPhoneWithActivityIntervals(dbId = 1,
                        deviceDbId = 2,
                        number = "23",
                        isSmsBlocked = true,
                        isCallsBlocked = true,
                        activityIntervals = listOf(
                                ActivityInterval(weekDayId = 1,
                                        beginTime = LocalTime.MIDNIGHT,
                                        endTime = LocalTime.MIDNIGHT))),
                BlacklistContactPhoneWithActivityIntervals(dbId = 2,
                        deviceDbId = 3,
                        number = "1",
                        isSmsBlocked = true,
                        isCallsBlocked = true,
                        activityIntervals = listOf(
                                ActivityInterval(weekDayId = 3,
                                        beginTime = LocalTime.MIDNIGHT,
                                        endTime = LocalTime.MIDNIGHT))))

        val resPhonesWithIntervalsSortedByNum = listOf(
                BlacklistContactPhoneWithActivityIntervals(dbId = 2,
                        deviceDbId = 3,
                        number = "1",
                        isSmsBlocked = true,
                        isCallsBlocked = true,
                        activityIntervals = listOf(
                                ActivityInterval(weekDayId = 3,
                                        beginTime = LocalTime.MIDNIGHT,
                                        endTime = LocalTime.MIDNIGHT))),
                BlacklistContactPhoneWithActivityIntervals(dbId = 1,
                        deviceDbId = 2,
                        number = "23",
                        isSmsBlocked = true,
                        isCallsBlocked = true,
                        activityIntervals = listOf(
                                ActivityInterval(weekDayId = 1,
                                        beginTime = LocalTime.MIDNIGHT,
                                        endTime = LocalTime.MIDNIGHT))),
                BlacklistContactPhoneWithActivityIntervals(dbId = null,
                        deviceDbId = 1,
                        number = "547",
                        isSmsBlocked = false,
                        isCallsBlocked = false,
                        activityIntervals = testDefaultIntervals),
                BlacklistContactPhoneWithActivityIntervals(dbId = null,
                        deviceDbId = 0,
                        number = "634",
                        isSmsBlocked = false,
                        isCallsBlocked = false,
                        activityIntervals = testDefaultIntervals))

        return TestDataWithExpectedRes(testInpInteractionMode = InteractionMode.EDIT,
                testMaybeSelectedWhitelistContactPhones = Maybe.just(testWhitelistPhonesUnsorted),
                testMaybeSelectedBlacklistContactPhonesWithIntervals = Maybe.just(testBlacklistPhonesUnsorted),
                testSingleDefaultIntervals = Single.just(testDefaultIntervals),
                resPhonesWithIntervals = resPhonesWithIntervalsSortedByNum)
    }

    private fun generateOblyWhitelistPhonesSelectedInCreateMode() : TestDataWithExpectedRes{

        val testDefaultIntervals = mutableListOf<ActivityInterval>().apply{
            for(weekdayId in IntRange(1, 7)){
                add(ActivityInterval(weekDayId = weekdayId,
                        beginTime = LocalTime.MIDNIGHT,
                        endTime = LocalTime.MIDNIGHT))
            }
        }

        val testWhitelistPhonesUnsorted = listOf(
                WhitelistContactPhone(
                        deviceDbId = 0L,
                        number = "634"),
                WhitelistContactPhone(
                        deviceDbId = 1L,
                        number = "547"))

        val resPhonesWithIntervalsSortedByNum = listOf(
                BlacklistContactPhoneWithActivityIntervals(dbId = null,
                        deviceDbId = 1,
                        number = "547",
                        isSmsBlocked = false,
                        isCallsBlocked = false,
                        activityIntervals = testDefaultIntervals),
                BlacklistContactPhoneWithActivityIntervals(dbId = null,
                        deviceDbId = 0,
                        number = "634",
                        isSmsBlocked = false,
                        isCallsBlocked = false,
                        activityIntervals = testDefaultIntervals))

        return TestDataWithExpectedRes(testInpInteractionMode = InteractionMode.CREATE,
                testMaybeSelectedWhitelistContactPhones = Maybe.just(testWhitelistPhonesUnsorted),
                testMaybeSelectedBlacklistContactPhonesWithIntervals = Maybe.empty(),
                testSingleDefaultIntervals = Single.just(testDefaultIntervals),
                resPhonesWithIntervals = resPhonesWithIntervalsSortedByNum)
    }

    private fun generateOnlyWhitelistContactPhonesSelectedInEditMode() : TestDataWithExpectedRes{

        val testDefaultIntervals = mutableListOf<ActivityInterval>().apply{
            for(weekdayId in IntRange(1, 7)){
                add(ActivityInterval(weekDayId = weekdayId,
                        beginTime = LocalTime.MIDNIGHT,
                        endTime = LocalTime.MIDNIGHT))
            }
        }

        val testWhitelistPhonesUnsorted = listOf(
                WhitelistContactPhone(
                        deviceDbId = 0L,
                        number = "634"),
                WhitelistContactPhone(
                        deviceDbId = 1L,
                        number = "547"))

        return TestDataWithExpectedRes(testInpInteractionMode = InteractionMode.EDIT,
                testMaybeSelectedWhitelistContactPhones = Maybe.just(testWhitelistPhonesUnsorted),
                testMaybeSelectedBlacklistContactPhonesWithIntervals = Maybe.empty(),
                testSingleDefaultIntervals = Single.just(testDefaultIntervals))
    }


    private data class TestDataWithExpectedRes(val testInpInteractionMode: InteractionMode,
                                               val testMaybeSelectedWhitelistContactPhones: Maybe<List<WhitelistContactPhone>>? = null,
                                               val testMaybeSelectedBlacklistContactPhonesWithIntervals: Maybe<List<BlacklistContactPhoneWithActivityIntervals>>? = null,
                                               val testSingleDefaultIntervals: Single<List<ActivityInterval>>? = null,
                                               val resPhonesWithIntervals: List<BlacklistContactPhoneWithActivityIntervals>? = null)
}