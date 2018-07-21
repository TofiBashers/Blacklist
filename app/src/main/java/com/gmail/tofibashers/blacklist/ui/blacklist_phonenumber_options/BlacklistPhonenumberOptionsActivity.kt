package com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options

import android.app.Activity
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.constraint.Group
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.gmail.tofibashers.blacklist.R

import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import com.gmail.tofibashers.blacklist.ui.common.BaseStateableViewActivity
import com.gmail.tofibashers.blacklist.ui.common.SavingResult
import com.gmail.tofibashers.blacklist.utils.setCheckedWithoutInvokeListener

import kotterknife.bindView
import javax.inject.Inject

class BlacklistPhonenumberOptionsActivity : BaseStateableViewActivity<Group, Group>(),
        View.OnClickListener{

    override val loadingView: Group by bindView(R.id.group_progress)
    override val dataView: Group by bindView(R.id.group_options_with_controls)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val ignoreCallsCheckBox: CheckBox by bindView(R.id.checkbox_ignore_calls)
    private val ignoreSMSCheckBox: CheckBox by bindView(R.id.checkbox_ignore_sms)
    private val okButton: Button by bindView(R.id.button_save)
    private val cancelButton: Button by bindView(R.id.button_cancel)
    private val changeScheduleButton: Button by bindView(R.id.button_change_schedule)
    private val numberEditText: TextInputEditText by bindView(R.id.edit_number)

    @Inject
    lateinit var viewModel: BlacklistPhonenumberOptionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate")
        setContentView(R.layout.activity_blacklist_phonenumber_options)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        okButton.setOnClickListener(this)
        cancelButton.setOnClickListener(this)
        changeScheduleButton.setOnClickListener(this)
        numberEditText.addTextChangedListener(phoneNumberEditWatcher)
        ignoreCallsCheckBox.setOnCheckedChangeListener(checkboxesListener)
        ignoreSMSCheckBox.setOnCheckedChangeListener(checkboxesListener)

        if(savedInstanceState == null) viewModel.onInitGetItem()

        viewModel.viewStateData.observe(this, Observer {
            when (it){
                is BlacklistPhonenumberOptionsViewState.DataViewState -> showDataStateWithParamsState(it)
                is BlacklistPhonenumberOptionsViewState.LoadingViewState -> showDefaultLoading()
            }
        })
        viewModel.navigateSingleData.observe(this, Observer {
            when (it){
                is BlacklistPhonenumberOptionsNavData.ListRoute ->
                    navigator.toParentWithResult(this, when(it.savingResult){
                        SavingResult.SAVED -> Activity.RESULT_OK
                        SavingResult.CANCELED -> Activity.RESULT_CANCELED
                    })
                is BlacklistPhonenumberOptionsNavData.ActivityIntervalDetailsRoute ->
                    navigator.toTimeSettings(this)
                is BlacklistPhonenumberOptionsNavData.SavedNumberAlreadyExistsRoute ->
                    navigator.toSavedNumberAlreadyExists(this)
            }
        })
    }

    override fun onClick(V: View) {
        when (V.id) {
            R.id.button_save -> viewModel.onInitSave()
            R.id.button_cancel -> viewModel.onInitCancel()
            R.id.button_change_schedule -> viewModel.onInitChangeSchedule()
        }
    }

    override fun onBackPressed() {
        viewModel.onInitCancel()
    }

    override fun onSupportNavigateUp() : Boolean{
        onBackPressed()
        return true
    }

    private fun showDataStateWithParamsState(dataViewState: BlacklistPhonenumberOptionsViewState.DataViewState){
        setViewState(ViewState.DATA)
        val element: BlacklistPhoneNumberItem = dataViewState.state.phoneNumberItem
        numberEditText.removeTextChangedListener(phoneNumberEditWatcher)
        numberEditText.setTextKeepState(element.number)
        numberEditText.addTextChangedListener(phoneNumberEditWatcher)
        ignoreCallsCheckBox.setCheckedWithoutInvokeListener(element.isCallsBlocked, checkboxesListener)
        ignoreSMSCheckBox.setCheckedWithoutInvokeListener(element.isSmsBlocked, checkboxesListener)
        setTitle(if (dataViewState.state.mode == InteractionMode.CREATE) R.string.blacklist_phonenumber_options_toolbar_create_title
                else R.string.blacklist_phonenumber_options_toolbar_edit_title)
        okButton.isEnabled = dataViewState.state.isValidToSave
    }

    private val phoneNumberEditWatcher = object : TextWatcher{

        override fun afterTextChanged(s: Editable) {
            viewModel.onNumberChanged(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    }

    private val checkboxesListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        when(buttonView.id) {
            ignoreCallsCheckBox.id -> viewModel.onSetIsCallsBlocked(isChecked)
            ignoreSMSCheckBox.id -> viewModel.onSetIsSmsBlocked(isChecked)
        }
    }

    companion object {

        private val LOG_TAG = BlacklistPhonenumberOptionsActivity::class.simpleName
    }

}
