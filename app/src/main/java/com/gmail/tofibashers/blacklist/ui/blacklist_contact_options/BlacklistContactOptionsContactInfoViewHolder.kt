package com.gmail.tofibashers.blacklist.ui.blacklist_contact_options

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.ui.common.BaseViewHolder
import com.gmail.tofibashers.blacklist.ui.common.CircleImageTranformation
import com.gmail.tofibashers.blacklist.utils.loadContactImageByUrlOrDefault
import com.squareup.picasso.Picasso
import kotterknife.bindView


/**
 * Created by TofiBashers on 15.04.2018.
 */
class BlacklistContactOptionsContactInfoViewHolder(parent: ViewGroup) : BaseViewHolder<BlacklistContactItem>(R.layout.item_blacklist_contact_options_contact_info, parent) {
    private val contactImageView: ImageView by bindView(R.id.image_contact)
    private val contactNameView: TextView by bindView(R.id.text_contact_name)

    override fun bind(item: BlacklistContactItem){
        contactNameView.text = item.name
        loadContactImageByUrlOrDefault(contactImageView, item.photoUrl)
    }
}