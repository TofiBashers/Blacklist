package com.gmail.tofibashers.blacklist.ui.blacklist

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import com.gmail.tofibashers.blacklist.utils.OnClickListItemWithPositionListener
import kotterknife.bindView

/**
 * Created by TofixXx on 05.08.2015.
 */
class BlacklistAdapter(private val clickListener: OnClickListItemWithPositionListener) :
        RecyclerView.Adapter<BlacklistAdapter.BlackListItemHolder>() {

    private var list: MutableList<BlacklistItem> = mutableListOf()

    fun addAll(list: List<BlacklistItem>) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    list[newItemPosition].dbId == this@BlacklistAdapter.list[oldItemPosition].dbId

            override fun getOldListSize(): Int = this@BlacklistAdapter.list.size

            override fun getNewListSize(): Int  = list.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    this@BlacklistAdapter.list[oldItemPosition] == list[newItemPosition]
        }, false)
                .dispatchUpdatesTo(this)
        this.list = list.toMutableList()
    }

    fun getItem(position: Int): BlacklistItem = list[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlackListItemHolder
            = BlackListItemHolder(clickListener, parent)

    override fun onBindViewHolder(holder: BlackListItemHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int = list.count()

    class BlackListItemHolder(
            private val clickListener: OnClickListItemWithPositionListener,
            parent: ViewGroup
    ) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_numberlist, parent, false)
    ), View.OnClickListener{

        private val callsBlockView: ImageView by bindView(R.id.image_is_calls_block)
        private val smsBlockView: ImageView by bindView(R.id.image_is_sms_block)
        private val phoneNumberView: TextView by bindView(R.id.text_phone_number)
        private val optionsButton: ImageButton by bindView(R.id.imagebutton_options)

        init {
            optionsButton.setOnClickListener(this)
        }

        fun bind(blacklistItem: BlacklistItem){
            smsBlockView.setImageResource(if (blacklistItem.isSmsBlocked) R.drawable.ic_sms_disabled_48dp
            else R.drawable.ic_sms_enabled_48dp_exp)
            callsBlockView.setImageResource(if (blacklistItem.isCallsBlocked) R.drawable.ic_call_disabled_48dp
            else R.drawable.ic_call_enabled_48dp_exp)
            phoneNumberView.text = blacklistItem.number
        }

        override fun onClick(v: View) = clickListener.onClickListItem(v, adapterPosition)
    }
}
