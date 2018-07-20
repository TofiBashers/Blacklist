package com.gmail.tofibashers.blacklist.ui.blacklist


import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.*
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.*
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.entity.GetBlacklistResult
import com.gmail.tofibashers.blacklist.entity.SystemVerWarningType
import com.gmail.tofibashers.blacklist.ui.common.ThirdPartyAppForNavigationNotFoundException
import com.gmail.tofibashers.blacklist.ui.common.BaseStateableViewActivity
import com.gmail.tofibashers.blacklist.utils.AndroidComponentKeys
import com.gmail.tofibashers.blacklist.utils.setCheckedWithoutInvokeListener
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import kotterknife.bindView
import javax.inject.Inject

class BlacklistActivity : BaseStateableViewActivity<RelativeLayout, CoordinatorLayout>() {

    override val loadingView: RelativeLayout by bindView(R.id.progressbar_view)
    override val dataView: CoordinatorLayout by bindView(R.id.coordinator_toolbar_with_num_list)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val speedDialView: SpeedDialView by bindView(R.id.speeddial_add)
    private val ignoreHiddenSwitch: SwitchCompat by bindView(R.id.switch_ignore_hidden_numbers)
    private val list: RecyclerView by bindView(R.id.num_list)

    @Inject
    lateinit var viewModel: BlacklistViewModel

    @Inject
    lateinit var blacklistAdapter: BlacklistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blacklist)
        setSupportActionBar(toolbar)

        ignoreHiddenSwitch.setOnCheckedChangeListener(ignoreHiddenChangeListener)
        speedDialView.addAllActionItems(listOf(
                SpeedDialActionItem.Builder(R.id.blacklist_add_phonenumber_fab_id, R.drawable.ic_add_phone_number_48dp)
                        .setLabel(getString(R.string.blacklist_add_num_action))
                        .setLabelColor(ContextCompat.getColor(this, R.color.primary_dark_text))
                        .setLabelBackgroundColor(ContextCompat.getColor(this, R.color.action_item_color))
                        .setFabBackgroundColor(ContextCompat.getColor(this, R.color.primary_light))
                        .setLabelClickable(true)
                        .create(),
                SpeedDialActionItem.Builder(R.id.blacklist_add_contact_fab_id, R.drawable.ic_add_contact_phone_48dp)
                        .setLabel(getString(R.string.blacklist_add_contact_action))
                        .setLabelColor(ContextCompat.getColor(this, R.color.primary_dark_text))
                        .setLabelBackgroundColor(ContextCompat.getColor(this, R.color.action_item_color))
                        .setFabBackgroundColor(ContextCompat.getColor(this, R.color.primary_light))
                        .setLabelClickable(true)
                        .create()))
        speedDialView.setOnActionSelectedListener(speedDialActionSelectedListener)

        val layoutManager = LinearLayoutManager(this)
        val dividerDecoration = DividerItemDecoration(this, layoutManager.orientation)
        @Suppress("DEPRECATION")
        dividerDecoration.setDrawable(resources.getDrawable(R.drawable.divider_blacklist))
        list.layoutManager = layoutManager
        list.addItemDecoration(dividerDecoration)
        list.adapter = blacklistAdapter

        if (savedInstanceState == null) viewModel.onInitGetList()

        viewModel.viewStateData.observe(this, Observer{
            when(it){
                is BlacklistViewState.ListViewState -> showListWithIgnoreHidden(it)
                is BlacklistViewState.LoadingViewState -> setViewState(ViewState.LOADING)
            }
        })
        viewModel.navigateSingleData.observe(this, Observer {
            when(it) {
                is BlacklistNavRoute.PhonenumberCallRoute -> navigateToDialingAppPhonenumberViewOrNotFound(it)
                is BlacklistNavRoute.PhonenumberSmsRoute -> navigateToSmsAppPhonenumberOrNotFound(it)
                is BlacklistNavRoute.ContactOpenInContactsAppRoute -> navigateToContactsAppWithContactViewOrNotFound(it)
                is BlacklistNavRoute.SelectContactRoute -> navigateToSelectContact()
                is BlacklistNavRoute.BlacklistContactOptionsRoute -> navigateToContactOptions()
                is BlacklistNavRoute.BlacklistPhonenumberOptionsRoute -> navigateToPhoneOptions()
            }
        })
        viewModel.warningMessageData.observe(this, Observer { showWarningDialog(it!!) })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            viewModel.onAdditionOrCreationCancelled()
            return
        }
        if (requestCode == AndroidComponentKeys.REQUEST_CODE_SELECT_CONTACT_VIEW) {
            navigateToContactOptions()
        }
    }

    override fun setViewState(state: ViewState){
        when (state){
            ViewState.DATA -> {
                loadingView.visibility = View.GONE
                dataView.visibility = View.VISIBLE
                ignoreHiddenSwitch.visibility = View.VISIBLE
                speedDialView.show()
            }
            ViewState.LOADING -> {
                speedDialView.hide()
                ignoreHiddenSwitch.visibility = View.GONE
                dataView.visibility = View.GONE
                loadingView.visibility = View.VISIBLE
            }
        }
    }

    private fun showListWithIgnoreHidden(listViewState: BlacklistViewState.ListViewState) {
        val resultList: GetBlacklistResult.ListWithIgnoreResult = listViewState.blackListWithIgnoreHidden
        setViewState(ViewState.DATA)
        ignoreHiddenSwitch.setCheckedWithoutInvokeListener(resultList.ignoreHidden,
                ignoreHiddenChangeListener)
        blacklistAdapter.addAll(resultList)
    }

    private fun showWarningDialog(warning: GetBlacklistResult.SystemVerWarning){
        AlertDialog.Builder(this)
                .setMessage(warningTypeToResId(warning.warningType))
                .setNegativeButton(android.R.string.ok, null)
                .create()
                .show()
    }

    private fun navigateToPhoneOptions() =
            navigator.toBlacklistPhoneOptionsWithResult(this,
                    AndroidComponentKeys.REQUEST_CODE_PHONENUMBER_OPTIONS_VIEW)

    private fun navigateToContactOptions() =
            navigator.toBlacklistContactOptionsWithResult(this,
                    AndroidComponentKeys.REQUEST_CODE_BLACKLIST_CONTACT_OPTIONS_VIEW)

    private fun navigateToSelectContact() =
            navigator.toSelectContactWithResult(this,
                    AndroidComponentKeys.REQUEST_CODE_SELECT_CONTACT_VIEW)

    private fun navigateToDialingAppPhonenumberViewOrNotFound(contactOpenInContactsAppRoute: BlacklistNavRoute.PhonenumberCallRoute){
        val srcNumber = contactOpenInContactsAppRoute.phoneNumber
        try {
            navigator.toDialingWithNumber(this, Uri.encode(srcNumber))
        }
        catch (e: ActivityNotFoundException){
            Toast.makeText(this, R.string.msg_calls_app_not_found_title, Toast.LENGTH_LONG)
                    .show()
        }
    }

    private fun navigateToSmsAppPhonenumberOrNotFound(contactOpenInContactsAppRoute: BlacklistNavRoute.PhonenumberSmsRoute){
        try {
            navigator.toCreateSmsWithNumber(this, contactOpenInContactsAppRoute.phoneNumber)
        }
        catch (e: ThirdPartyAppForNavigationNotFoundException){
            Toast.makeText(this, R.string.msg_sms_app_not_found_title, Toast.LENGTH_LONG)
                    .show()
        }
    }

    private fun navigateToContactsAppWithContactViewOrNotFound(contactOpenInContactsAppRoute: BlacklistNavRoute.ContactOpenInContactsAppRoute){
        try {
            navigator.toEditContact(this,
                    contactOpenInContactsAppRoute.contactId,
                    contactOpenInContactsAppRoute.contactKey)
        }
        catch (e: ThirdPartyAppForNavigationNotFoundException){
            Toast.makeText(this, R.string.msg_contact_app_not_found_title, Toast.LENGTH_LONG)
                    .show()
        }
    }

    private fun warningTypeToResId(warningType: SystemVerWarningType): Int = when(warningType){
        SystemVerWarningType.MAY_UPDATE_TO_INCOMPATIBLE_VER -> R.string.blacklist_warning_update_to_incompatible_android_version
        SystemVerWarningType.INCOMPATIBLE_VER -> R.string.blacklist_warning_update_to_incompatible_android_version
    }

    val contactClickListener = object : BlacklistContactItemWithIgnoredInfoHolder.ClickListener{

        override fun onChangeClick(position: Int) = viewModel.onInitContactItemChange(position)

        override fun onDeleteClick(position: Int) = viewModel.onInitContactItemDelete(position)

        override fun onOpenInContactsClick(position: Int) = viewModel.onInitContactOpenInContactsApp(position)
    }

    val phoneNumberClickListener = object : BlacklistPhoneNumberItemHolder.ClickListener{

        override fun onDeleteClick(position: Int) = viewModel.onInitPhoneNumberItemDelete(position)

        override fun onChangeClick(position: Int) = viewModel.onInitPhoneNumberItemChange(position)

        override fun onCallClick(position: Int) = viewModel.onInitPhoneNumberItemCall(position)

        override fun onSMSClick(position: Int) = viewModel.onInitPhoneNumberItemSms(position)
    }

    private val speedDialActionSelectedListener = SpeedDialView.OnActionSelectedListener {
        when(it.id) {
            R.id.blacklist_add_phonenumber_fab_id -> viewModel.onInitCreateItem()
            R.id.blacklist_add_contact_fab_id -> viewModel.onInitAddContactItem()
        }
        return@OnActionSelectedListener false
    }

    private val ignoreHiddenChangeListener = CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
        if(buttonView.id == ignoreHiddenSwitch.id){
            viewModel.onIgnoreHiddenStateChanged(isChecked)
        }
    }

}
