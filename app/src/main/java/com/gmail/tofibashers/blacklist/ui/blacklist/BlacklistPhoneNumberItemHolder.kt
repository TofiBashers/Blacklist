package com.gmail.tofibashers.blacklist.ui.blacklist

import android.support.v7.widget.PopupMenu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.entity.SectionBlacklistItem
import com.gmail.tofibashers.blacklist.ui.common.BaseViewHolder
import kotterknife.bindView

class BlacklistPhoneNumberItemHolder(
        private val clickListener: ClickListener,
        parent: ViewGroup
) : BaseViewHolder<SectionBlacklistItem.PhoneNumber>(R.layout.item_blacklist_phonenumber, parent),
        View.OnClickListener {

    private val callsBlockView: ImageView by bindView(R.id.image_is_calls_block)
    private val smsBlockView: ImageView by bindView(R.id.image_is_sms_block)
    private val phoneNumberView: TextView by bindView(R.id.text_phone_number)
    private val optionsButton: ImageButton by bindView(R.id.imagebutton_options)

    init {
        optionsButton.setOnClickListener(this)
    }

    override fun bind(item: SectionBlacklistItem.PhoneNumber){
        val phoneItem = item.phoneNumberItem
        smsBlockView.setImageResource(if (phoneItem.isSmsBlocked) R.drawable.ic_sms_disabled_48dp
        else R.drawable.ic_sms_enabled_48dp)
        callsBlockView.setImageResource(if (phoneItem.isCallsBlocked) R.drawable.ic_call_disabled_48dp
        else R.drawable.ic_call_enabled_48dp)
        phoneNumberView.text = phoneItem.number
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imagebutton_options -> {
                val popupMenu = PopupMenu(itemView.context, v)
                popupMenu.inflate(R.menu.blacklist_phonenumber_item_options)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.change_item -> clickListener.onChangeClick(adapterPosition)
                        R.id.delete_item -> clickListener.onDeleteClick(adapterPosition)
                        R.id.call_item -> clickListener.onCallClick(adapterPosition)
                        R.id.sms_item -> clickListener.onSMSClick(adapterPosition)
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }

    interface ClickListener {
        fun onDeleteClick(position: Int)
        fun onChangeClick(position: Int)
        fun onCallClick(position: Int)
        fun onSMSClick(position: Int)
    }
}