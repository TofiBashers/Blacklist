package com.gmail.tofibashers.blacklist.ui.blacklist

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.*
import com.gmail.tofibashers.blacklist.entity.GetBlacklistResult
import com.gmail.tofibashers.blacklist.entity.SectionBlacklistItem
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
        private val selectPhoneNumberElementUseCase: ISelectEditModeAndPhoneNumberItemUseCase,
        private val selectCreateModeUseCase: ISelectCreateModeUseCase,
        private val deletePhoneNumberItemUseCase: IDeletePhoneNumberItemUseCase,
        private val selectContactItemUseCase: ISelectEditModeAndBlacklistContactItemWithPhonesAndIntervalsUseCase,
        private val deleteContactItemUseCase: IDeleteBlacklistContactItemUseCase,
        private val listViewStateFactory: BlacklistViewState_ListViewStateFactory,
        private val loadingViewStateFactory: BlacklistViewState_LoadingViewStateFactory,
        private val blacklistContactOptionsRouteFactory: BlacklistNavRoute_BlacklistContactOptionsRouteFactory,
        private val blacklistPhonenumberOptionsRouteFactory: BlacklistNavRoute_BlacklistPhonenumberOptionsRouteFactory,
        private val selectContactRouteFactory: BlacklistNavRoute_SelectContactRouteFactory,
        private val contactInOptionsRouteFactory: BlacklistNavRoute_ContactOpenInContactsAppRouteFactory,
        private val phonenumberCallRouteFactory: BlacklistNavRoute_PhonenumberCallRouteFactory,
        private val phonenumberSmsRouteFactory: BlacklistNavRoute_PhonenumberSmsRouteFactory,
        val viewStateData: MutableLiveData<BlacklistViewState>,
        val navigateSingleData: SingleLiveEvent<BlacklistNavRoute>,
        val warningMessageData: SingleLiveEvent<GetBlacklistResult.SystemVerWarning>
) : DisposableViewModel() {

    private var itemsWithIgnoreHidden: GetBlacklistResult.ListWithIgnoreResult? = null
    private var navRouteAfterSelect: BlacklistNavRoute? = null

    fun onInitGetList(){
        viewStateData.value = loadingViewStateFactory.create()
        getBlacklistItemsUseCase.build()
                .subscribe(GetListObserver())
    }

    fun onInitContactOpenInContactsApp(position: Int){
        val contactSection = itemsWithIgnoreHidden!![position] as SectionBlacklistItem.Contact
        val deviceDbId = contactSection.contactItem.deviceDbId
        val deviceKey = contactSection.contactItem.deviceKey
        if(deviceDbId != null && deviceKey != null){
            navigateSingleData.value = contactInOptionsRouteFactory.create(deviceDbId, deviceKey)
        }
    }

    fun onInitContactItemChange(position: Int){
        this.navRouteAfterSelect = blacklistContactOptionsRouteFactory.create()
        viewStateData.value = loadingViewStateFactory.create()
        val contactSection = itemsWithIgnoreHidden!![position] as SectionBlacklistItem.Contact
        selectContactItemUseCase.build(contactSection.contactItem)
                .subscribe(SelectObserver())
    }

    fun onInitContactItemDelete(position: Int){
        viewStateData.value = loadingViewStateFactory.create()
        val contactSection = itemsWithIgnoreHidden!![position] as SectionBlacklistItem.Contact
        deleteContactItemUseCase.build(contactSection.contactItem)
                .subscribe(DeleteObserver())
    }

    fun onInitPhoneNumberItemCall(position: Int){
        val phonenumberSection = itemsWithIgnoreHidden!![position] as SectionBlacklistItem.PhoneNumber
        navigateSingleData.value = phonenumberCallRouteFactory.create(phonenumberSection.phoneNumberItem.number)
    }

    fun onInitPhoneNumberItemSms(position: Int){
        val phonenumberSection = itemsWithIgnoreHidden!![position] as SectionBlacklistItem.PhoneNumber
        navigateSingleData.value = phonenumberSmsRouteFactory.create(phonenumberSection.phoneNumberItem.number)
    }

    fun onInitPhoneNumberItemChange(position: Int){
        this.navRouteAfterSelect = blacklistPhonenumberOptionsRouteFactory.create()
        viewStateData.value = loadingViewStateFactory.create()
        val phoneNumberSection = itemsWithIgnoreHidden!![position] as SectionBlacklistItem.PhoneNumber
        selectPhoneNumberElementUseCase.build(phoneNumberSection.phoneNumberItem)
                .subscribe(SelectObserver())
    }

    fun onInitPhoneNumberItemDelete(position: Int){
        viewStateData.value = loadingViewStateFactory.create()
        val phoneNumberSection = itemsWithIgnoreHidden!![position] as SectionBlacklistItem.PhoneNumber
        deletePhoneNumberItemUseCase.build(phoneNumberSection.phoneNumberItem)
                .subscribe(DeleteObserver())
    }

    fun onInitCreateItem(){
        this.navRouteAfterSelect = blacklistPhonenumberOptionsRouteFactory.create()
        viewStateData.value = loadingViewStateFactory.create()
        selectCreateModeUseCase.build()
                .subscribe(SelectObserver())
    }

    fun onInitAddContactItem(){
        this.navRouteAfterSelect = selectContactRouteFactory.create()
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
            navigateSingleData.value = navRouteAfterSelect!!
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