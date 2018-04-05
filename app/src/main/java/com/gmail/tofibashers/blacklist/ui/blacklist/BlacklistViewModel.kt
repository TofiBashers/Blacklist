package com.gmail.tofibashers.blacklist.ui.blacklist

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.*
import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import com.gmail.tofibashers.blacklist.entity.GetBlacklistResult
import com.gmail.tofibashers.blacklist.ui.common.DisposableViewModel
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import javax.inject.Inject


/**
 * Created by TofiBashers on 20.01.2018.
 */
class BlacklistViewModel
@Inject
constructor(
        private val getBlacklistItemsUseCase: IGetBlacklistItemsSortByNumberWithIgnoreHiddenUseCase,
        private val saveIgnoreHiddenNumbersSyncUseCase: ISaveIgnoreHiddenNumbersSyncUseCase,
        private val selectBlacklistElementUseCase: ISelectEditModeAndBlacklistItemUseCase,
        private val selectCreateModeUseCase: ISelectCreateModeUseCase,
        private val deleteBlacklistItemUseCase: IDeleteBlacklistItemUseCase,
        private val listViewStateFactory: BlacklistViewState_ListViewStateFactory,
        private val loadingViewStateFactory: BlacklistViewState_LoadingViewStateFactory,
        val viewStateData: MutableLiveData<BlacklistViewState>,
        val navigateSingleData: SingleLiveEvent<BlacklistNavRoute>,
        val warningMessageData: SingleLiveEvent<GetBlacklistResult.SystemVerWarning>
) : DisposableViewModel() {

    private var itemsWithIgnoreHidden: GetBlacklistResult.ListWithIgnoreResult? = null

    fun onInitGetList(){
        viewStateData.value = loadingViewStateFactory.create()
        getBlacklistItemsUseCase.build()
                .subscribe(GetListObserver())
    }

    fun onInitItemChange(blacklistItem: BlacklistItem){
        viewStateData.value = loadingViewStateFactory.create()
        selectBlacklistElementUseCase.build(blacklistItem)
                .subscribe(SelectObserver())
    }

    fun onInitItemDelete(blacklistItem: BlacklistItem){
        viewStateData.value = loadingViewStateFactory.create()
        deleteBlacklistItemUseCase.build(blacklistItem)
                .subscribe(DeleteObserver())
    }

    fun onInitCreateItem(){
        viewStateData.value = loadingViewStateFactory.create()
        selectCreateModeUseCase.build()
                .subscribe(SelectObserver())
    }

    fun onIgnoreHiddenStateChanged(ignoreHidden: Boolean){
        saveIgnoreHiddenNumbersSyncUseCase.build(ignoreHidden)
                .subscribe(SaveIgnoreHiddenObserver())
    }

    fun onAdditionOrCreationCancelled(){
        viewStateData.value = listViewStateFactory.create(itemsWithIgnoreHidden!!)
    }

    private inner class SelectObserver : DisposableSavingCompletableObserver() {

        override fun onComplete() {
            navigateSingleData.value = BlacklistNavRoute.OPTIONS
        }

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

    private inner class SaveIgnoreHiddenObserver : DisposableSavingCompletableObserver() {

        override fun onComplete() {}

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

    private inner class DeleteObserver : DisposableSavingCompletableObserver() {

        override fun onComplete() {}

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

    private inner class GetListObserver : DisposableSavingObserver<GetBlacklistResult>() {

        override fun onComplete() {}

        override fun onNext(itemsWithIgnoreHidden: GetBlacklistResult) {
            when(itemsWithIgnoreHidden){
                is GetBlacklistResult.ListWithIgnoreResult -> {
                    this@BlacklistViewModel.itemsWithIgnoreHidden = itemsWithIgnoreHidden
                    viewStateData.value = listViewStateFactory.create(itemsWithIgnoreHidden)
                }
                is GetBlacklistResult.SystemVerWarning ->
                    warningMessageData.value = itemsWithIgnoreHidden
            }
        }

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

}