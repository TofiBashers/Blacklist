package com.gmail.tofibashers.blacklist.ui.blacklist_contact_options

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.*
import com.gmail.tofibashers.blacklist.entity.InteractionModeWithBlacklistContactItemAndNumbersAndValidState
import com.gmail.tofibashers.blacklist.entity.OutdatedDataException
import com.gmail.tofibashers.blacklist.ui.common.DisposableViewModel
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import com.gmail.tofibashers.blacklist.ui.common.SavingResult
import javax.inject.Inject


/**
 * Created by TofiBashers on 15.04.2018.
 */
class BlacklistContactOptionsViewModel
@Inject
constructor(
        private val getInteractionModeWithItemAndValidUseCase: IGetInteractionModeWithSelectedBlacklistContactItemUseCase,
        private val saveContactWithDeleteSelectionsUseCase: ISaveBlacklistContactItemWithDeleteSelectionsUseCase,
        private val selectForEditActivityIntervalsUseCase: ISelectForEditActivityIntervalsOfBlacklistContactPhoneUseCase,
        private val saveSelectedIntervalsUseCase: ISaveSelectedActivityIntervalsToAllBlacklistContactPhonesIntervalsUseCase,
        private val validatePhoneNumbersForSaveSyncUseCase: IValidateBlacklistContactPhoneNumbersForSaveSyncUseCase,
        private val deleteAllSelectionsUseCase: IDeleteAllSelectionsUseCase,
        private val dataViewStateFactory: BlacklistContactOptionsViewState_DataViewStateFactory,
        private val loadingViewStateFactory: BlacklistContactOptionsViewState_LoadingViewStateFactory,
        private val listRouteFactory: BlacklistContactOptionsNavData_ListRouteFactory,
        private val activityIntervalDetailsRouteFactory: BlacklistContactOptionsNavData_ActivityIntervalDetailsRouteFactory,
        private val listRouteWithCancelAndErrorFactory: BlacklistContactOptionsNavData_ListRouteWithCancelAndChangedOrDeletedErrorFactory,
        val viewStateData: MutableLiveData<BlacklistContactOptionsViewState>,
        val navigateSingleData: SingleLiveEvent<BlacklistContactOptionsNavData>
) : DisposableViewModel() {

    private var state: InteractionModeWithBlacklistContactItemAndNumbersAndValidState? = null
    private var selectedForChangeSchedulePosition: Int? = null

    fun onInitGetContactAndPhones(){
        viewStateData.value = loadingViewStateFactory.create()
        getInteractionModeWithItemAndValidUseCase.build()
                .subscribe(GetInteractionModeWithBlacklistContactItemAndNumbersAndValidStateObserver())
    }

    fun onInitSave(){
        viewStateData.value = loadingViewStateFactory.create()
        saveContactWithDeleteSelectionsUseCase.build(state!!.contactItem, state!!.phoneNumbers)
                .subscribe(SaveItemObserver())
    }

    fun onInitCancel(){
        viewStateData.value = loadingViewStateFactory.create()
        disableAllRequests()
        deleteAllSelectionsUseCase.build()
                .subscribe(RemoveAllSelectionsObserver())
    }

    fun onInitChangeSchedule(numPosition: Int){
        selectedForChangeSchedulePosition = numPosition
        viewStateData.value = loadingViewStateFactory.create()
        selectForEditActivityIntervalsUseCase.build(numPosition, state!!.size)
                .subscribe(SelectForEditActivityIntervalsObserver())
    }

    fun onScheduleChanged(){
        viewStateData.value = loadingViewStateFactory.create()
        saveSelectedIntervalsUseCase.build(selectedForChangeSchedulePosition!!)
                .subscribe(SaveSelectedIntervalObserver())
    }

    fun onSetIsCallsBlocked(numPosition: Int, isCallsBlocked: Boolean){
        state!![numPosition].isCallsBlocked = isCallsBlocked
        validatePhoneNumbersForSaveSyncUseCase.build(state!!.phoneNumbers)
                .subscribe(SyncValidateItemObserver())
    }

    fun onSetIsSmsBlocked(numPosition: Int, isSmsBlocked: Boolean){
        state!![numPosition].isSmsBlocked = isSmsBlocked
        validatePhoneNumbersForSaveSyncUseCase.build(state!!.phoneNumbers)
                .subscribe(SyncValidateItemObserver())
    }

    private inner class GetInteractionModeWithBlacklistContactItemAndNumbersAndValidStateObserver
        : DisposableSavingSingleObserver<InteractionModeWithBlacklistContactItemAndNumbersAndValidState>() {

        override fun onSuccess(modeWithItemAndState: InteractionModeWithBlacklistContactItemAndNumbersAndValidState) {
            state = modeWithItemAndState
            viewStateData.value = dataViewStateFactory.create(modeWithItemAndState)
        }

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

    private inner class SelectForEditActivityIntervalsObserver
        : DisposableSavingCompletableObserver() {

        override fun onComplete() {
            viewStateData.value = dataViewStateFactory.create(state!!)
            navigateSingleData.value = activityIntervalDetailsRouteFactory.create()
        }

        override fun onError(e: Throwable) {
            throw RuntimeException(e)
        }
    }

    private inner class SaveSelectedIntervalObserver
        : DisposableSavingCompletableObserver() {

        override fun onComplete() {
            viewStateData.value = dataViewStateFactory.create(state!!)
        }

        override fun onError(e: Throwable) {
            if(e is OutdatedDataException) {
                navigateSingleData.value = listRouteWithCancelAndErrorFactory.create()
            }
            else throw RuntimeException(e)
        }
    }

    private inner class SaveItemObserver : DisposableSavingCompletableObserver() {

        override fun onComplete() {
            navigateSingleData.value = listRouteFactory.create(SavingResult.SAVED)
        }

        override fun onError(e: Throwable) {
            if(e is OutdatedDataException) {
                navigateSingleData.value = listRouteWithCancelAndErrorFactory.create()
            }
            else throw RuntimeException(e)
        }
    }

    private inner class RemoveAllSelectionsObserver : DisposableSavingCompletableObserver() {

        override fun onComplete() {
            navigateSingleData.value = listRouteFactory.create(SavingResult.CANCELED)
        }

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

    private inner class SyncValidateItemObserver : DisposableSavingSingleObserver<Boolean>() {

        override fun onSuccess(isValidToSave: Boolean) {
            state!!.isValidToSave = isValidToSave
            viewStateData.value = dataViewStateFactory.create(state!!)
        }

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

}