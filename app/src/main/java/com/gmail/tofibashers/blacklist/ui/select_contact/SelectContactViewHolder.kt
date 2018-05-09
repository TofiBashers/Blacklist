package com.gmail.tofibashers.blacklist.ui.select_contact

import android.support.constraint.Group
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItemWithHasPhones
import com.gmail.tofibashers.blacklist.ui.common.BaseViewHolder
import com.gmail.tofibashers.blacklist.ui.common.CircleImageTranformation
import com.gmail.tofibashers.blacklist.utils.loadContactImageByUrlOrDefault
import com.squareup.picasso.Picasso
import kotterknife.bindView


/**
 * Created by TofiBashers on 10.04.2018.
 */
class SelectContactViewHolder(
        private val clickListener: ClickListener,
        parent: ViewGroup
) : BaseViewHolder<WhitelistContactItemWithHasPhones>(R.layout.item_select_contact, parent), View.OnClickListener {

    private val contactImageView: ImageView by bindView(R.id.image_contact)
    private val contactNameView: TextView by bindView(R.id.text_contact_name)
    private val contactOptionsButton: ImageButton by bindView(R.id.imagebutton_change)
    private val contactInfoGroup: Group by bindView(R.id.group_contact_info)
    private val disabledToSelectInfoGroup: Group by bindView(R.id.group_disabled_to_select_info)

    init {
        contactOptionsButton.setOnClickListener(this)
        itemView.setOnClickListener(this)
    }

    override fun bind(item: WhitelistContactItemWithHasPhones){
        contactInfoGroup.isEnabled = item.hasPhones
        disabledToSelectInfoGroup.visibility = if(item.hasPhones) View.INVISIBLE else View.VISIBLE
        contactNameView.text = item.name
        loadContactImageByUrlOrDefault(contactImageView, item.photoUrl)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imagebutton_change -> clickListener.onChangeClick(adapterPosition)
            R.id.constraint_root_item_contact -> clickListener.onSelectClick(adapterPosition)
        }
    }

    interface ClickListener {
        fun onSelectClick(position: Int)
        fun onChangeClick(position: Int)
    }
}