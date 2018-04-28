package com.gmail.tofibashers.blacklist.ui.time_settings

import android.app.Activity
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Button
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.entity.MutableActivityIntervalsWithEnableAndValidState
import com.gmail.tofibashers.blacklist.entity.TimeChangeInitData
import com.gmail.tofibashers.blacklist.ui.common.BaseStateableViewActivity
import com.gmail.tofibashers.blacklist.ui.common.SavingResult
import com.gmail.tofibashers.blacklist.utils.AndroidComponentKeys
import com.gmail.tofibashers.blacklist.utils.TimeFormatUtils
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotterknife.bindView
import javax.inject.Inject


class TimeSettingsActivity : BaseStateableViewActivity<Group, Group>(),
        View.OnClickListener,
        WeekLinearLayout.OnTimeSetClickListener,
        WeekLinearLayout.OnWeekdayCheckedChangeListener,
        TimePickerDialog.OnTimeSetListener{

    override val loadingView: Group by bindView(R.id.group_progress)
    override val dataView: Group by bindView(R.id.group_scrollview_with_save)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val weekLayout: WeekLinearLayout by bindView(R.id.weekview)
    private val saveButton: Button by bindView(R.id.button_save)

    @Inject
    lateinit var timeFormatUtils: TimeFormatUtils

    @Inject
    lateinit var viewModel: TimeSettingsViewModel

    private var onTimeChangeViewPosition: Int? = null
    private var onTimeChangeIsBegin: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate")
        setContentView(R.layout.activity_time_settings)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        prepareViewStateAndViewModel(savedInstanceState)
        viewModel.viewStateData.observe(this, Observer {
            when(it){
                is TimeSettingsViewState.DataViewState -> {
                    setViewState(ViewState.DATA)
                    showData(it)
                }
                is TimeSettingsViewState.LoadingViewState -> setViewState(ViewState.LOADING)
            }
        })
        viewModel.navigationData.observe(this, Observer{
            when(it){
                is TimeSettingsNavData.TimeChangeRoute -> prepareAndShowTimePicker(it.timeChangeInitData)
                is TimeSettingsNavData.ItemDetailsRoute -> finishWithResult(it.result)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val dialog = fragmentManager.findFragmentByTag(AndroidComponentKeys.TAG_TIMEPICKER_DIALOG) as TimePickerDialog?
        dialog?.onTimeSetListener = this
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        onTimeChangeIsBegin?.let {
            outState.putBoolean(AndroidComponentKeys.BUNDLE_TIME_CHANGE_IS_BEGIN, it)
        }
        onTimeChangeViewPosition?.let {
            outState.putInt(AndroidComponentKeys.BUNDLE_ON_TIME_CHANGE_VIEW_POSITION, it)
        }
    }

    override fun onDayCheckedChanged(isChecked: Boolean, position: Int) =
            viewModel.onInitChangeEnableState(position, isChecked)

    override fun onTimeSetClick(isBeginTime: Boolean, position: Int) {
        viewModel.onInitChangeTime(isBeginTime,
                position)
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        viewModel.onTimeChanged(
                timeFormatUtils.intValuesViewToLocalTime(hourOfDay, minute),
                onTimeChangeIsBegin!!,
                onTimeChangeViewPosition!!)
    }

    override fun onClick(v: View) {
        when (v.id) {
            saveButton.id -> viewModel.onInitSave()
        }
    }

    override fun onBackPressed() {
        viewModel.onInitCancel()
        super.onBackPressed()
    }

    override fun onSupportNavigateUp() : Boolean{
        onBackPressed()
        return true
    }

    private fun prepareViewStateAndViewModel(savedInstanceState: Bundle?) {
        weekLayout.onTimeSetClickListener = this
        weekLayout.onWeekdayCheckedChangeListener = this
        saveButton.setOnClickListener(this)
        if (savedInstanceState == null) viewModel.onInitGetItem()
        else{
            onTimeChangeIsBegin = savedInstanceState.getBoolean(AndroidComponentKeys.BUNDLE_TIME_CHANGE_IS_BEGIN)
            onTimeChangeViewPosition = savedInstanceState.getInt(AndroidComponentKeys.BUNDLE_ON_TIME_CHANGE_VIEW_POSITION)
        }
    }

    private fun prepareAndShowTimePicker(timeChangeInitData: TimeChangeInitData){
        onTimeChangeIsBegin = timeChangeInitData.isBeginTimeToChange
        onTimeChangeViewPosition = timeChangeInitData.position
        val timeInPickerFormat = timeFormatUtils.localTimeToViewIntArray(timeChangeInitData.initTime)
        val dialog = TimePickerDialog.newInstance(
                this@TimeSettingsActivity,
                timeInPickerFormat[0],
                timeInPickerFormat[1],
                timeFormatUtils.is24hourFormatForView())
        dialog.accentColor = resources.getColor(R.color.time_picker_color)
        dialog.setSelectableTimes(timeFormatUtils.localTimesToViewTimepoints(*timeChangeInitData.selectableTimes.toTypedArray()))
        dialog.vibrate(false)
        dialog.show(fragmentManager, AndroidComponentKeys.TAG_TIMEPICKER_DIALOG)
    }

    private fun showData(viewState: TimeSettingsViewState.DataViewState) =
            showDaysFields(viewState.activtiyIntervals)

    private fun showDaysFields(activityTimesWithEnableAndValid: MutableActivityIntervalsWithEnableAndValidState) {
        for((i, weekdayActiveTime) in activityTimesWithEnableAndValid.withIndex()) {
            val activityInterval = weekdayActiveTime.second
            weekLayout.showWeekdayInfo(i,
                    timeFormatUtils.weekdayIdToLocalizedWeekdayName(activityInterval.weekDayId),
                    weekdayActiveTime.first,
                    timeFormatUtils.localTimeToLocalizedViewTime(activityInterval.beginTime, false),
                    timeFormatUtils.localTimeToLocalizedViewTime(activityInterval.endTime, true))
        }
        saveButton.isEnabled = activityTimesWithEnableAndValid.isValidToSave
    }

    private fun finishWithResult(savingResult: SavingResult) {
        setResult(when(savingResult){
            SavingResult.SAVED -> Activity.RESULT_OK
            SavingResult.CANCELED -> Activity.RESULT_CANCELED
        })
        finish()
    }

    companion object {

        private val LOG_TAG = TimeSettingsActivity::class.simpleName
    }
}
