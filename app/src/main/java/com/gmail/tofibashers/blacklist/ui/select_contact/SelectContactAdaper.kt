package com.gmail.tofibashers.blacklist.ui.select_contact

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.gmail.tofibashers.blacklist.entity.WhitelistContactItemWithHasPhones


/**
 * Created by TofiBashers on 10.04.2018.
 */
class SelectContactAdaper(private val contactClickListener: SelectContactViewHolder.ClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list: MutableList<WhitelistContactItemWithHasPhones> = mutableListOf()

    override fun getItemCount(): Int = list.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ITEM_CONTACT_TYPE -> SelectContactViewHolder(contactClickListener, parent)
            else -> throw RuntimeException("Invalid ViewType: " + viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sectionHolder = holder as SelectContactViewHolder
        sectionHolder.bind(list[position])
    }

    fun set(list: List<WhitelistContactItemWithHasPhones>){
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun getOldListSize(): Int = this@SelectContactAdaper.list.size

            override fun getNewListSize(): Int = list.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = this@SelectContactAdaper.list[oldItemPosition]
                val newItem = list[newItemPosition]
                return oldItem.deviceDbId == newItem.deviceDbId && oldItem.deviceKey == newItem.deviceKey
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = this@SelectContactAdaper.list[oldItemPosition]
                val newItem = list[newItemPosition]
                return oldItem.name == newItem.name &&
                        oldItem.hasPhones == newItem.hasPhones &&
                        oldItem.photoUrl == newItem.photoUrl
            }

        }, false)
                .dispatchUpdatesTo(this)
        this.list = list.toMutableList()
    }

    companion object {
        const val ITEM_CONTACT_TYPE = 1
    }
}