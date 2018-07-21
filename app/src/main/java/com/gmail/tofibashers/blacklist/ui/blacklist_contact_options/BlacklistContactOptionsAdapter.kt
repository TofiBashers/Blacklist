package com.gmail.tofibashers.blacklist.ui.blacklist_contact_options

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem
import com.gmail.tofibashers.blacklist.entity.BlacklistPhoneNumberItem
import com.gmail.tofibashers.blacklist.ui.common.CircleImageTranformation


/**
 * Created by TofiBashers on 15.04.2018.
 */
class BlacklistContactOptionsAdapter(
        private val eventsListener: IEventsListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var blacklistContactItem: BlacklistContactItem? = null
    private var contactPhoneNumbers: List<BlacklistContactPhoneNumberItem> = listOf()

    fun set(blacklistContactItem: BlacklistContactItem,
            contactPhoneNumbers: List<BlacklistContactPhoneNumberItem>) { // with private setter because enabled only both simultaneously
        this.blacklistContactItem = blacklistContactItem
        this.contactPhoneNumbers = contactPhoneNumbers
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isContactInfoPosition(position) -> ITEM_CONTACT_INFO_TYPE
            else -> ITEM_PHONE_NUMBERS_MIDDLE_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ITEM_CONTACT_INFO_TYPE -> BlacklistContactOptionsContactInfoViewHolder(parent)
            ITEM_PHONE_NUMBERS_MIDDLE_TYPE ->
                BlacklistContactOptionsPhoneNumberViewHolder(stateChangeListener, parent)
            else -> throw RuntimeException("Unknown viewType: " + viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is BlacklistContactOptionsContactInfoViewHolder) {
            holder.bind(blacklistContactItem!!)
        }
        else{
            val numberHolder = holder as BlacklistContactOptionsPhoneNumberViewHolder
            numberHolder.bind(contactPhoneNumbers[phoneNumbersPositionFromViewPosition(position)])
        }
        holder.itemView.setTag(R.id.blacklist_contact_options_divider_key,
                if(position == 0 && contactPhoneNumbers.count() > 1) DividerType.SOLID_LINE
                else DividerType.GRADIENT)
    }

    override fun getItemCount(): Int {
        return if(blacklistContactItem == null) 0
        else contactPhoneNumbers.size + 1
    }

    private fun isContactInfoPosition(position: Int) = position == 0

    private fun phoneNumbersPositionFromViewPosition(viewPosition: Int) = viewPosition - 1

    private val stateChangeListener = object : BlacklistContactOptionsPhoneNumberViewHolder.StateChangeListener {
        override fun onIgnoreSmsCheckedChanged(position: Int, isChecked: Boolean) =
            eventsListener.onPhoneIgnoreSmsCheckedChanged(position-1, isChecked)

        override fun onIgnoreCallsCheckedChanged(position: Int, isChecked: Boolean) =
            eventsListener.onPhoneIgnoreCallsCheckedChanged(position-1, isChecked)

        override fun onChangeScheduleClick(position: Int) =
            eventsListener.onPhoneChangeScheduleClick(position-1)
    }

    interface IEventsListener {
        fun onPhoneIgnoreSmsCheckedChanged(modelPosition: Int, isChecked: Boolean)
        fun onPhoneIgnoreCallsCheckedChanged(modelPosition: Int, isChecked: Boolean)
        fun onPhoneChangeScheduleClick(modelPosition: Int)
    }

    companion object {
        const val ITEM_CONTACT_INFO_TYPE = 1
        const val ITEM_PHONE_NUMBERS_MIDDLE_TYPE = 2
    }
}