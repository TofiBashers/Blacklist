package com.gmail.tofibashers.blacklist.ui.options

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.*
import com.gmail.tofibashers.blacklist.entity.InteractionModeWithBlacklistPhoneNumberItemAndValidState
import com.gmail.tofibashers.blacklist.entity.NumberAlreadyExistsException
import com.gmail.tofibashers.blacklist.ui.common.DisposableViewModel
import com.gmail.tofibashers.blacklist.ui.common.SavingResult
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import javax.inject.Inject


/**
 * Created by TofiBashers on 14.01.2018.
 */
class OptionsViewModel
@Inject
constructor(
        private val getInteractionModeWithItemUseCase: IGetInteractionModeWithSelectedBlackListItemUseCase,
        private val saveWithDeleteSelectionsUseCase: ISaveBlacklistPhoneItemWithDeleteSelectionsUseCase,
        private val deleteAllSelectionsUseCase: IDeleteAllSelectionsUseCase,
        private val syncValidateBlacklistItemForSaveUseCase: IValidateBlacklistItemForSaveSyncUseCase,
        private val dataViewStateFactory: OptionsViewState_DataViewStateFactory,
        private val loadingViewStateFactory: OptionsViewState_LoadingViewStateFactory,
        private val listRouteFactory: OptionsNavData_ListRouteFactory,
        private val activityIntervalDetailsRouteFactory: OptionsNavData_ActivityIntervalDetailsRouteFactory,
        private val numberAlreadyExistsRouteFactory: OptionsNavData_SavedNumberAlreadyExistsRouteFactory,
        val viewStateData: MutableLiveData<OptionsViewState>,
        val navigateSingleData: SingleLiveEvent<OptionsNavData>
) : DisposableViewModel(){

    private var state: InteractionModeWithBlacklistPhoneNumberItemAndValidState? = null

    fun onInitGetItem(){
        viewStateData.value = loadingViewStateFactory.create()
        getInteractionModeWithItemUseCase.build()
                .subscribe(GetInteractionModeWithSelectedItemAndValidObserver())
    }

    fun onInitSave(){
        viewStateData.value = loadingViewStateFactory.create()
        saveWithDeleteSelectionsUseCase.build(state!!.phoneNumberItem)
                .subscribe(SaveItemObserver())
    }

    fun onInitCancel(){
        viewStateData.value = loadingViewStateFactory.create()
        disableAllRequests()
        deleteAllSelectionsUseCase.build()
                .subscribe(RemoveAllSelectionsObserver())
    }

    fun onInitChangeSchedule(){
        navigateSingleData.value = activityIntervalDetailsRouteFactory.create()
    }

    fun onNumberChanged(number: String){
        state!!.phoneNumberItem.number = number
        syncValidateBlacklistItemForSaveUseCase.build(state!!.phoneNumberItem)
                .subscribe(SyncValidateItemObserver())
    }

    fun onSetIsCallsBlocked(isCallsBlocked: Boolean){
        state!!.phoneNumberItem.isCallsBlocked = isCallsBlocked
        syncValidateBlacklistItemForSaveUseCase.build(state!!.phoneNumberItem)
                .subscribe(SyncValidateItemObserver())
    }

    fun onSetIsSmsBlocked(isSmsBlocked: Boolean){
        state!!.phoneNumberItem.isSmsBlocked = isSmsBlocked
        syncValidateBlacklistItemForSaveUseCase.build(state!!.phoneNumberItem)
                .subscribe(SyncValidateItemObserver())
    }

    private inner class GetInteractionModeWithSelectedItemAndValidObserver : DisposableSavingSingleObserver<InteractionModeWithBlacklistPhoneNumberItemAndValidState>() {

        override fun onSuccess(modeWithItemAndState: InteractionModeWithBlacklistPhoneNumberItemAndValidState) {
            state = modeWithItemAndState
            viewStateData.value = dataViewStateFactory.create(state)
        }

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

    private inner class SaveItemObserver : DisposableSavingCompletableObserver() {

        override fun onComplete() {
            navigateSingleData.value = listRouteFactory.create(SavingResult.SAVED)
        }

        override fun onError(error: Throwable) {
            if(error is NumberAlreadyExistsException){
                viewStateData.value = dataViewStateFactory.create(state!!)
                navigateSingleData.value = numberAlreadyExistsRouteFactory.create()
            }
            else throw RuntimeException(error)
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
            viewStateData.value = dataViewStateFactory.create(state)
        }

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

}