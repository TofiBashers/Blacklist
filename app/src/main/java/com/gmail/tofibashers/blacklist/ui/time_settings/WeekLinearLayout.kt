package com.gmail.tofibashers.blacklist.ui.time_settings

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.utils.setCheckedWithoutInvokeListener
import java.util.*


/**
 * Created by TofiBashers on 25.01.2018.
 */
class WeekLinearLayout : LinearLayoutCompat {

    private val viewHoldersList = ArrayList<WeekdayViewsHolder>(7)

    var onTimeSetClickListener: OnTimeSetClickListener? = null
        set(value) {
            for(viewHolder in viewHoldersList){
                viewHolder.onTimeSetClickListener = value
            }
        }
    var onWeekdayCheckedChangeListener: OnWeekdayCheckedChangeListener? = null
        set(value) {
            for(viewHolder in viewHoldersList){
                viewHolder.onWeekdayCheckedChangeListener = value
            }
        }

    constructor (context: Context) : super(context) {
        initDaysViews()
    }

    constructor (context: Context, attrs: AttributeSet) : super(context, attrs) {
        initDaysViews()
    }

    constructor (context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initDaysViews()
    }

    fun showWeekdayInfo(position: Int,
                        weekdayName: String,
                        isChecked: Boolean,
                        beginTime: String,
                        endTime: String) {
        val viewHolder = viewHoldersList[position]
        viewHolder.showInfo(weekdayName, isChecked, beginTime, endTime)
    }

    /**
     * Simple inflation of 7 same view fields with setting position tags to childs
     */
    private fun initDaysViews() {
        for (i in 0..6) {
            val rootView: View = LayoutInflater.from(context)
                    .inflate(R.layout.item_time_and_weekday_settings, this, false)
            viewHoldersList.add(WeekdayViewsHolder(rootView, i))
            addView(rootView, i)
        }
    }

    interface OnWeekdayCheckedChangeListener {
        fun onDayCheckedChanged(isChecked: Boolean, position: Int)
    }

    interface OnTimeSetClickListener {
        fun onTimeSetClick(isBeginTime: Boolean, position: Int)
    }

    class WeekdayViewsHolder(private val rootView: View,
                             private val position: Int) {

        val weekdayCheckbox = rootView.findViewById<CheckBox>(R.id.checkbox_weekday_state)
        val setBeginTimeButton = rootView
                .findViewById<View>(R.id.include_begin_time)
                .findViewById<ImageButton>(R.id.imagebutton_set_time)
        val setEndTimeButton = rootView
                .findViewById<View>(R.id.include_end_time)
                .findViewById<ImageButton>(R.id.imagebutton_set_time)
        val setBeginTimeButtonCard = rootView
                .findViewById<View>(R.id.include_begin_time)
                .findViewById<CardView>(R.id.cardview_wrap_imagebutton_set_time)
        val setEndTimeButtonCard = rootView
                .findViewById<View>(R.id.include_end_time)
                .findViewById<CardView>(R.id.cardview_wrap_imagebutton_set_time)
        val beginTimeTextView = rootView
                .findViewById<View>(R.id.include_begin_time)
                .findViewById<TextView>(R.id.text_time)
        val endTimeTextView = rootView
                .findViewById<View>(R.id.include_end_time)
                .findViewById<TextView>(R.id.text_time)
        val hyphenTextView = rootView
                .findViewById<TextView>(R.id.text_hyphen)

        var onTimeSetClickListener: OnTimeSetClickListener? = null
        var onWeekdayCheckedChangeListener: OnWeekdayCheckedChangeListener? = null

        private val beginTimeClickListener = OnClickListener {
            onTimeSetClickListener?.onTimeSetClick(true, position)
        }

        private val endTimeClickListener = OnClickListener {
            onTimeSetClickListener?.onTimeSetClick(false, position)
        }

        private val checkedChangeListener = CompoundButton.OnCheckedChangeListener {
            _, isChecked: Boolean ->
            onWeekdayCheckedChangeListener?.onDayCheckedChanged(isChecked, position)
        }

        init {
            weekdayCheckbox.setOnCheckedChangeListener(checkedChangeListener)
            setBeginTimeButton.setOnClickListener(beginTimeClickListener)
            setEndTimeButton.setOnClickListener(endTimeClickListener)
        }

        fun showInfo(weekdayName: String,
                     isChecked: Boolean,
                     beginTime: String,
                     endTime: String) {
            weekdayCheckbox.text = weekdayName
            weekdayCheckbox.setCheckedWithoutInvokeListener(isChecked, checkedChangeListener)
            beginTimeTextView.text = beginTime
            endTimeTextView.text = endTime
            val settingsImageId =
                    if(isChecked) R.drawable.ic_settings_48dp
                    else R.drawable.ic_settings_disabled_48dp
            val settingsElevationId =
                    if(isChecked) R.dimen.partial_time_set_time_button_enabled_elevation
                    else R.dimen.partial_time_set_time_button_disabled_elevation
            setBeginTimeButton.isEnabled = isChecked
            setEndTimeButton.isEnabled = isChecked
            setBeginTimeButton.setImageResource(settingsImageId)
            setEndTimeButton.setImageResource(settingsImageId)
            setBeginTimeButtonCard.cardElevation = rootView.resources.getDimension(settingsElevationId)
            setEndTimeButtonCard.cardElevation = rootView.resources.getDimension(settingsElevationId)
            beginTimeTextView.isEnabled = isChecked
            endTimeTextView.isEnabled = isChecked
            hyphenTextView.isEnabled = isChecked
        }
    }
}