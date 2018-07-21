package com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options

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
class BlacklistPhonenumberOptionsViewModel
@Inject
constructor(
        private val getInteractionModeWithItemUseCase: IGetInteractionModeWithSelectedBlacklistItemUseCase,
        private val saveWithDeleteSelectionsUseCase: ISaveBlacklistPhoneItemWithDeleteSelectionsUseCase,
        private val deleteAllSelectionsUseCase: IDeleteAllSelectionsUseCase,
        private val syncValidateBlacklistPhoneNumberItemForSaveUseCase: IValidateBlacklistPhoneNumberItemForSaveSyncUseCase,
        private val dataViewStateFactory: BlacklistPhonenumberOptionsViewState_DataViewStateFactory,
        private val loadingViewStateFactory: BlacklistPhonenumberOptionsViewState_LoadingViewStateFactory,
        private val listRouteFactory: BlacklistPhonenumberOptionsNavData_ListRouteFactory,
        private val activityIntervalDetailsRouteFactory: BlacklistPhonenumberOptionsNavData_ActivityIntervalDetailsRouteFactory,
        private val numberAlreadyExistsRouteFactory: BlacklistPhonenumberOptionsNavData_SavedNumberAlreadyExistsRouteFactory,
        val viewStateData: MutableLiveData<BlacklistPhonenumberOptionsViewState>,
        val navigateSingleData: SingleLiveEvent<BlacklistPhonenumberOptionsNavData>
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
        syncValidateBlacklistPhoneNumberItemForSaveUseCase.build(state!!.phoneNumberItem)
                .subscribe(SyncValidateItemObserver())
    }

    fun onSetIsCallsBlocked(isCallsBlocked: Boolean){
        state!!.phoneNumberItem.isCallsBlocked = isCallsBlocked
        syncValidateBlacklistPhoneNumberItemForSaveUseCase.build(state!!.phoneNumberItem)
                .subscribe(SyncValidateItemObserver())
    }

    fun onSetIsSmsBlocked(isSmsBlocked: Boolean){
        state!!.phoneNumberItem.isSmsBlocked = isSmsBlocked
        syncValidateBlacklistPhoneNumberItemForSaveUseCase.build(state!!.phoneNumberItem)
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