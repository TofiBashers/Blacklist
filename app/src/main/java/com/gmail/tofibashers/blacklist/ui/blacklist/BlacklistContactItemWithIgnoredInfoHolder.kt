package com.gmail.tofibashers.blacklist.ui.blacklist

import android.support.constraint.Group
import android.widget.PopupMenu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.entity.SectionBlacklistItem
import com.gmail.tofibashers.blacklist.ui.common.BaseViewHolder
import com.gmail.tofibashers.blacklist.utils.loadContactImageByUrlOrDefault
import kotterknife.bindView

class BlacklistContactItemWithIgnoredInfoHolder(
        private val clickListener: ClickListener,
        parent: ViewGroup
) : BaseViewHolder<SectionBlacklistItem.Contact>(R.layout.item_blacklist_contact, parent),
        View.OnClickListener {

    private val contactImageView: ImageView by bindView(R.id.image_contact)
    private val contactNameView: TextView by bindView(R.id.text_contact_name)
    private val contactOptionsButton: ImageButton by bindView(R.id.imagebutton_options)
    private val notAllIgnoredInfoImageView: ImageView by bindView(R.id.image_not_all_ignored_info)
    private val notAllIgnoredInfoTextView: TextView by bindView(R.id.text_not_all_ignored_info)

    init {
        contactOptionsButton.setOnClickListener(this)
        itemView.setOnClickListener(this)
    }

    override fun bind(item: SectionBlacklistItem.Contact){
        val contactItem = item.contactItem
        val withNonIgnoredNumbers = contactItem.withNonIgnoredNumbers
        notAllIgnoredInfoImageView.visibility = if(withNonIgnoredNumbers) View.VISIBLE else View.INVISIBLE
        notAllIgnoredInfoTextView.visibility = if(withNonIgnoredNumbers) View.VISIBLE else View.INVISIBLE
        contactNameView.text = contactItem.name
        loadContactImageByUrlOrDefault(contactImageView, contactItem.photoUrl)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imagebutton_options -> {
                val popupMenu = PopupMenu(itemView.context, v)
                popupMenu.inflate(R.menu.blacklist_contact_options)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.change_item -> clickListener.onChangeClick(adapterPosition)
                        R.id.delete_item -> clickListener.onDeleteClick(adapterPosition)
                        R.id.open_in_contacts_item -> clickListener.onOpenInContactsClick(adapterPosition)
                    }
                    true
                }
                popupMenu.show()
            }
            R.id.constraint_root_item_contact -> clickListener.onChangeClick(adapterPosition)
        }
    }

    interface ClickListener {
        fun onDeleteClick(position: Int)
        fun onChangeClick(position: Int)
        fun onOpenInContactsClick(position: Int)
    }
}