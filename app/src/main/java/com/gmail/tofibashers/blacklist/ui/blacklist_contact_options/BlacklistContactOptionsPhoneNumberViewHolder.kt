package com.gmail.tofibashers.blacklist.ui.blacklist_contact_options

import android.support.design.widget.FloatingActionButton
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import com.gmail.tofibashers.blacklist.ui.common.BaseViewHolder
import kotterknife.bindView


/**
 * Created by TofiBashers on 15.04.2018.
 */
class BlacklistContactOptionsPhoneNumberViewHolder(
        private val stateChangeListener: StateChangeListener,
        parent: ViewGroup
) : BaseViewHolder<BlacklistContactPhoneNumberItem>(R.layout.item_blacklist_contact_options_phone, parent),
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private val phoneNumberView: TextView by bindView(R.id.text_phone_number)
    private val ignoreCallsCheckBox: CheckBox by bindView(R.id.checkbox_ignore_calls)
    private val ignoreSMSCheckBox: CheckBox by bindView(R.id.checkbox_ignore_sms)
    private val changeScheduleButton: FloatingActionButton by bindView(R.id.floatingactionbutton_change_schedule)

    init {
        changeScheduleButton.setOnClickListener(this)
        ignoreCallsCheckBox.setOnCheckedChangeListener(this)
        ignoreSMSCheckBox.setOnCheckedChangeListener(this)
    }

    override fun bind(item: BlacklistContactPhoneNumberItem){
        phoneNumberView.text = item.number
        ignoreCallsCheckBox.setOnCheckedChangeListener(null)
        ignoreSMSCheckBox.setOnCheckedChangeListener(null)
        ignoreCallsCheckBox.isChecked = item.isCallsBlocked
        ignoreSMSCheckBox.isChecked = item.isSmsBlocked
        ignoreCallsCheckBox.setOnCheckedChangeListener(this)
        ignoreSMSCheckBox.setOnCheckedChangeListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            changeScheduleButton.id -> {
                stateChangeListener.onChangeScheduleClick(adapterPosition)
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            ignoreCallsCheckBox.id -> stateChangeListener.onIgnoreCallsCheckedChanged(adapterPosition, isChecked)
            ignoreSMSCheckBox.id -> stateChangeListener.onIgnoreSmsCheckedChanged(adapterPosition, isChecked)
        }
    }

    interface StateChangeListener {
        fun onIgnoreSmsCheckedChanged(position: Int, isChecked: Boolean)
        fun onIgnoreCallsCheckedChanged(position: Int, isChecked: Boolean)
        fun onChangeScheduleClick(position: Int)
    }
}