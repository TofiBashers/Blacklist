package com.gmail.tofibashers.blacklist.ui.select_contact

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.IGetAllNonIgnoredContactsWithChangesUseCase
import com.gmail.tofibashers.blacklist.domain.ISelectContactItemUseCase
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItemWithHasPhones
import com.gmail.tofibashers.blacklist.ui.common.DisposableViewModel
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import javax.inject.Inject


/**
 * Created by TofiBashers on 11.04.2018.
 */
class SelectContactViewModel
@Inject
constructor(
        private val selectContactItemUseCase: ISelectContactItemUseCase,
        private val getAllNonIgnoredContactsUseCase: IGetAllNonIgnoredContactsWithChangesUseCase,
        private val parentRouteFactory: SelectContactNavData_ParentRouteFactory,
        private val editContactRouteFactory: SelectContactNavData_EditContactRouteFactory,
        private val blacklistContactOptionsRouteFactory: SelectContactNavData_BlacklistContactOptionsRouteFactory,
        private val loadingViewStateFactory: SelectContactViewState_LoadingViewStateFactory,
        private val dataViewStateFactory: SelectContactViewState_DataViewStateFactory,
        val viewStateData: MutableLiveData<SelectContactViewState>,
        val navigateSingleData: SingleLiveEvent<SelectContactNavData>
) : DisposableViewModel(){

    private var list: List<WhitelistContactItemWithHasPhones>? = null

    fun onInitGetList(){
        viewStateData.value = loadingViewStateFactory.create()
        getAllNonIgnoredContactsUseCase.build()
                .subscribe(GetContactsObserver())
    }

    fun onInitChangeContact(position: Int){
        val item = list!![position]
        navigateSingleData.value = editContactRouteFactory.create(item.deviceDbId, item.deviceKey)
    }

    fun onInitSelectContact(position: Int){
        viewStateData.value = loadingViewStateFactory.create()
        selectContactItemUseCase.build(list!![position])
                .subscribe(SelectItemObserver())
    }

    fun onInitCancel(){
        requestsDisposable.clear()
        navigateSingleData.value = parentRouteFactory.create()
    }

    private inner class GetContactsObserver : DisposableSavingObserver<List<WhitelistContactItemWithHasPhones>>() {

        override fun onNext(contactsList: List<WhitelistContactItemWithHasPhones>) {
            list = contactsList
            viewStateData.value = dataViewStateFactory.create(contactsList)
        }

        override fun onComplete() {}

        override fun onError(e: Throwable) {
            throw RuntimeException(e)
        }

    }

    private inner class SelectItemObserver : DisposableSavingCompletableObserver() {

        override fun onComplete() {
            navigateSingleData.value = blacklistContactOptionsRouteFactory.create()
        }

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }
}