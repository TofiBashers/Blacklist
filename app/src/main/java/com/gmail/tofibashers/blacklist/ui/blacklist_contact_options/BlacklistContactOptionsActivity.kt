package com.gmail.tofibashers.blacklist.ui.blacklist_contact_options

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.widget.DividerItemDecoration
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
import com.gmail.tofibashers.blacklist.ui.time_settings.TimeSettingsActivity
import com.gmail.tofibashers.blacklist.utils.AndroidComponentKeys
import kotterknife.bindView
import javax.inject.Inject

class BlacklistContactOptionsActivity : BaseStateableViewActivity<View, Group>(), BlacklistContactOptionsPhoneNumberViewHolder.StateChangeListener, View.OnClickListener {

    override val loadingView: View by bindView(R.id.progressbar_view)
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
        val dividerDecoration = DividerItemDecoration(this, layoutManager.orientation)
        @Suppress("DEPRECATION")
        dividerDecoration.setDrawable(resources.getDrawable(R.drawable.divider_blacklist_contact_options))
        list.layoutManager = layoutManager
        list.addItemDecoration(dividerDecoration)
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
                is BlacklistContactOptionsNavData.ListRoute -> showNavigationToList(it)
                is BlacklistContactOptionsNavData.ListRouteWithCancelAndChangedOrDeletedError -> showNavigationToListWithChangedOrDeletedError()
                is BlacklistContactOptionsNavData.ActivityIntervalDetailsRoute -> showActivityIntervalDetails()
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

    override fun onIgnoreSmsCheckedChanged(position: Int, isChecked: Boolean) =
            viewModel.onSetIsSmsBlocked(position, isChecked)

    override fun onIgnoreCallsCheckedChanged(position: Int, isChecked: Boolean) =
            viewModel.onSetIsCallsBlocked(position, isChecked)

    override fun onChangeScheduleClick(position: Int) = viewModel.onInitChangeSchedule(position)

    private fun showNavigationToListWithChangedOrDeletedError() {
        Toast.makeText(applicationContext,
                R.string.blacklist_contact_options_changed_or_deleted_error_title,
                Toast.LENGTH_LONG)
                .show()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun showNavigationToList(listRoute: BlacklistContactOptionsNavData.ListRoute) {
        setResult(when(listRoute.result){
            SavingResult.SAVED -> Activity.RESULT_OK
            SavingResult.CANCELED -> Activity.RESULT_CANCELED
        })
        finish()
    }

    private fun showActivityIntervalDetails() {
        val intent = Intent(this, TimeSettingsActivity::class.java)
        startActivityForResult(intent, AndroidComponentKeys.REQUEST_CODE_TIME_SETTINGS_VIEW)
    }

    private fun showDataStateWithParamsState(dataViewState: BlacklistContactOptionsViewState.DataViewState) {
        val modeWithState = dataViewState.modeWithState
        setTitle(
                if (modeWithState.mode == InteractionMode.CREATE) R.string.blacklist_contact_options_toolbar_create_title
                else R.string.blacklist_contact_options_toolbar_edit_title)
        okButton.isEnabled = modeWithState.isValidToSave
        blacklistContactOptionsAdapter.set(modeWithState.contactItem, modeWithState.phoneNumbers)
    }
}
