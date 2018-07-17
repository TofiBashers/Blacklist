package com.gmail.tofibashers.blacklist.ui.blacklist

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.entity.SectionBlacklistItem
import com.gmail.tofibashers.blacklist.ui.common.BaseViewHolder
import kotterknife.bindView

class SectionHeaderHolder(
        parent: ViewGroup
) : BaseViewHolder<SectionBlacklistItem.Header>(R.layout.item_blacklist_header, parent){

    private val headerTextView: TextView by bindView(R.id.text_header)
    private val headerLeftImageView: ImageView by bindView(R.id.image_left_header)
    private val headerRightImageView: ImageView by bindView(R.id.image_right_header)

    override fun bind(item: SectionBlacklistItem.Header){
        headerTextView.setText(when(item.type){
            SectionBlacklistItem.Header.Type.CONTACTS -> R.string.blacklist_header_contacts_title
            SectionBlacklistItem.Header.Type.NUMBERS -> R.string.blacklist_header_phone_numbers_title
        })
        headerLeftImageView.setImageResource(when(item.type){
            SectionBlacklistItem.Header.Type.CONTACTS -> R.drawable.ic_header_contacts_48dp
            SectionBlacklistItem.Header.Type.NUMBERS -> R.drawable.ic_smartphone_with_4dp_padding
        })
        headerRightImageView.setImageResource(when(item.type){
            SectionBlacklistItem.Header.Type.CONTACTS -> R.drawable.ic_header_contacts_48dp
            SectionBlacklistItem.Header.Type.NUMBERS -> R.drawable.ic_smartphone_with_4dp_padding
        })
    }
}