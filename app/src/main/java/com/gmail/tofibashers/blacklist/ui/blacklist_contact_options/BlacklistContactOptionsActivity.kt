package com.gmail.tofibashers.blacklist.ui.blacklist_contact_options

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import com.gmail.tofibashers.blacklist.ui.common.BaseStateableViewActivity
import com.gmail.tofibashers.blacklist.ui.common.SavingResult
import com.gmail.tofibashers.blacklist.utils.AndroidComponentKeys
import kotterknife.bindView
import javax.inject.Inject

class BlacklistContactOptionsActivity : BaseStateableViewActivity<Group, Group>(), BlacklistContactOptionsAdapter.IEventsListener, View.OnClickListener {

    override val loadingView: Group by bindView(R.id.group_progress)
    override val dataView: Group by bindView(R.id.group_contact_with_options_with_controls)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val okButton: Button by bindView(R.id.button_save)
    private val cancelButton: Button by bindView(R.id.button_cancel)
    private val list: RecyclerView by bindView(R.id.contact_with_options_list)

    @Inject
    lateinit var blacklistContactOptionsAdapter: BlacklistContactOptionsAdapter

    @Inject
    lateinit var viewModel: BlacklistContactOptionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blacklist_contact_options)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        okButton.setOnClickListener(this)
        cancelButton.setOnClickListener(this)

        val layoutManager = LinearLayoutManager(this)
        list.layoutManager = layoutManager
        list.addItemDecoration(BlacklistContactOptionsVerticalDividersByTagDecoration(this))
        list.adapter = blacklistContactOptionsAdapter

        if (savedInstanceState == null) viewModel.onInitGetContactAndPhones()

        viewModel.viewStateData.observe(this, Observer {
            when (it){
                is BlacklistContactOptionsViewState.DataViewState -> showDataStateWithParamsState(it)
                is BlacklistContactOptionsViewState.LoadingViewState -> showDefaultLoading()
            }
        })
        viewModel.navigateSingleData.observe(this, Observer {
            when (it){
                is BlacklistContactOptionsNavData.ListRoute -> {
                    navigator.toParentWithResult(this, when(it.result){
                        SavingResult.SAVED -> Activity.RESULT_OK
                        SavingResult.CANCELED -> Activity.RESULT_CANCELED
                    })
                }
                is BlacklistContactOptionsNavData.ListRouteWithCancelAndChangedOrDeletedError -> {
                    Toast.makeText(applicationContext,
                            R.string.blacklist_contact_options_changed_or_deleted_error_title,
                            Toast.LENGTH_LONG)
                            .show()
                    navigator.toParentWithResult(this, Activity.RESULT_CANCELED)
                }
                is BlacklistContactOptionsNavData.ActivityIntervalDetailsRoute ->
                    navigator.toTimeSettingsWithResult(this, AndroidComponentKeys.REQUEST_CODE_TIME_SETTINGS_VIEW)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == AndroidComponentKeys.REQUEST_CODE_TIME_SETTINGS_VIEW && resultCode == Activity.RESULT_OK){
            viewModel.onScheduleChanged()
        }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.button_save -> viewModel.onInitSave()
            R.id.button_cancel -> viewModel.onInitCancel()
        }
    }

    override fun onBackPressed() {
        viewModel.onInitCancel()
    }

    override fun onSupportNavigateUp() : Boolean{
        onBackPressed()
        return true
    }

    override fun onPhoneIgnoreSmsCheckedChanged(modelPosition: Int, isChecked: Boolean) =
            viewModel.onSetIsSmsBlocked(modelPosition, isChecked)

    override fun onPhoneIgnoreCallsCheckedChanged(modelPosition: Int, isChecked: Boolean) =
        viewModel.onSetIsCallsBlocked(modelPosition, isChecked)

    override fun onPhoneChangeScheduleClick(modelPosition: Int) =
            viewModel.onInitChangeSchedule(modelPosition)

    private fun showDataStateWithParamsState(dataViewState: BlacklistContactOptionsViewState.DataViewState) {
        setViewState(ViewState.DATA)
        val modeWithState = dataViewState.modeWithState
        setTitle(
                if (modeWithState.mode == InteractionMode.CREATE) R.string.blacklist_contact_options_toolbar_create_title
                else R.string.blacklist_contact_options_toolbar_edit_title)
        okButton.isEnabled = modeWithState.isValidToSave
        blacklistContactOptionsAdapter.set(modeWithState.contactItem, modeWithState.phoneNumbers)
    }
}
