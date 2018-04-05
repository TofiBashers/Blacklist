package com.gmail.tofibashers.blacklist.ui.options

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.*
import com.gmail.tofibashers.blacklist.entity.InteractionModeWithBlacklistItemAndValidState
import com.gmail.tofibashers.blacklist.entity.NumberAlreadyExistsException
import com.gmail.tofibashers.blacklist.ui.common.DisposableViewModel
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import javax.inject.Inject


/**
 * Created by TofiBashers on 14.01.2018.
 */
class OptionsViewModel
@Inject
constructor(
        private val getInteractionModeWithItemUseCase: IGetInteractionModeWithSelectedBlackListItemUseCase,
        private val saveBlacklistItemWithDeleteSelectionsUseCase: ISaveBlacklistItemWithDeleteSelectionsUseCase,
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

    private var state: InteractionModeWithBlacklistItemAndValidState? = null

    fun onInitGetItem(){
        viewStateData.value = loadingViewStateFactory.create()
        getInteractionModeWithItemUseCase.build()
                .subscribe(GetInteractionModeWithSelectedItemAndValidObserver())
    }

    fun onInitSave(){
        viewStateData.value = loadingViewStateFactory.create()
        saveBlacklistItemWithDeleteSelectionsUseCase.build(state!!.item)
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
        state!!.item.number = number
        syncValidateBlacklistItemForSaveUseCase.build(state!!.item)
                .subscribe(SyncValidateItemObserver())
    }

    fun onSetIsCallsBlocked(isCallsBlocked: Boolean){
        state!!.item.isCallsBlocked = isCallsBlocked
        syncValidateBlacklistItemForSaveUseCase.build(state!!.item)
                .subscribe(SyncValidateItemObserver())
    }

    fun onSetIsSmsBlocked(isSmsBlocked: Boolean){
        state!!.item.isSmsBlocked = isSmsBlocked
        syncValidateBlacklistItemForSaveUseCase.build(state!!.item)
                .subscribe(SyncValidateItemObserver())
    }

    private inner class GetInteractionModeWithSelectedItemAndValidObserver : DisposableSavingSingleObserver<InteractionModeWithBlacklistItemAndValidState>() {

        override fun onSuccess(modeWithItemAndState: InteractionModeWithBlacklistItemAndValidState) {
            state = modeWithItemAndState
            viewStateData.value = dataViewStateFactory.create(state)
        }

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

    private inner class SaveItemObserver : DisposableSavingCompletableObserver() {

        override fun onComplete() {
            navigateSingleData.value = listRouteFactory.create(OptionsResult.OK)
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
            navigateSingleData.value = listRouteFactory.create(OptionsResult.CANCELED)
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