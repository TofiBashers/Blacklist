package com.gmail.tofibashers.blacklist.ui.blacklist

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import com.gmail.tofibashers.blacklist.entity.*
import com.gmail.tofibashers.blacklist.ui.common.BaseViewHolder

/**
 * Created by TofixXx on 05.08.2015.
 */
class BlacklistAdapter(private val contactClickListener: BlacklistContactItemWithIgnoredInfoHolder.ClickListener,
                       private val blacklistPhoneNumberClickListener: BlacklistPhoneNumberItemHolder.ClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list: MutableList<SectionBlacklistItem> = mutableListOf()

    fun addAll(newList: List<SectionBlacklistItem>) {
        DiffUtil.calculateDiff(DiffUtilCallback(newList), false)
                .dispatchUpdatesTo(this)
        this.list = newList.toMutableList()
    }

    override fun getItemViewType(position: Int): Int {
        return when(list[position]){
            is SectionBlacklistItem.Header -> ITEM_SECTION_HEADER_TYPE
            is SectionBlacklistItem.PhoneNumber -> ITEM_PHONE_NUMBER_TYPE
            is SectionBlacklistItem.Contact -> ITEM_CONTACT_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_SECTION_HEADER_TYPE -> SectionHeaderHolder(parent)
            ITEM_PHONE_NUMBER_TYPE -> BlacklistPhoneNumberItemHolder(blacklistPhoneNumberClickListener, parent)
            ITEM_CONTACT_TYPE -> BlacklistContactItemWithIgnoredInfoHolder(contactClickListener, parent)
            else -> throw RuntimeException("Unknown viewType: " + viewType)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sectionHolder = holder as BaseViewHolder<SectionBlacklistItem>
        sectionHolder.bind(list[position])
    }

    override fun getItemCount(): Int = list.count()

    private inner class DiffUtilCallback(private val newList: List<SectionBlacklistItem>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = list.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val newListItem = newList[newItemPosition]
            val oldListItem = list[oldItemPosition]
            return when(oldListItem){
                is SectionBlacklistItem.PhoneNumber -> PhoneNumberDiffHelper.areTheSameItem(newListItem, oldListItem)
                is SectionBlacklistItem.Contact -> ContactDiffHelper.areTheSameItem(newListItem, oldListItem)
                is SectionBlacklistItem.Header -> HeaderDiffHelper.areTheSameItem(newListItem, oldListItem)
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val newListItem = newList[newItemPosition]
            val oldListItem = list[oldItemPosition]
            return when(oldListItem){
                is SectionBlacklistItem.PhoneNumber ->
                    PhoneNumberDiffHelper.areTheSameContents(newListItem as SectionBlacklistItem.PhoneNumber, oldListItem)
                is SectionBlacklistItem.Contact ->
                    ContactDiffHelper.areTheSameContents(newListItem as SectionBlacklistItem.Contact, oldListItem)
                is SectionBlacklistItem.Header -> true
            }
        }

    }

    private object PhoneNumberDiffHelper {

        fun areTheSameItem(item: SectionBlacklistItem, itemForCompare: SectionBlacklistItem.PhoneNumber) : Boolean{
            return item is SectionBlacklistItem.PhoneNumber &&
                    item.phoneNumberItem.dbId == itemForCompare.phoneNumberItem.dbId
        }

        fun areTheSameContents(item: SectionBlacklistItem.PhoneNumber,
                               itemForCompare: SectionBlacklistItem.PhoneNumber) : Boolean{
            val firstItem = item.phoneNumberItem
            val secondItem = itemForCompare.phoneNumberItem
            return firstItem.number == secondItem.number &&
                    firstItem.isCallsBlocked == secondItem.isCallsBlocked &&
                    firstItem.isSmsBlocked == secondItem.isSmsBlocked
        }
    }
    private object HeaderDiffHelper {
        fun areTheSameItem(item: SectionBlacklistItem,
                           itemForCompare: SectionBlacklistItem.Header) : Boolean{
            return item is SectionBlacklistItem.Header && item.type == itemForCompare.type
        }
    }

    private object ContactDiffHelper {

        fun areTheSameItem(item: SectionBlacklistItem,
                           itemForCompare: SectionBlacklistItem.Contact) : Boolean{
            return item is SectionBlacklistItem.Contact &&
                    item.contactItem.dbId == itemForCompare.contactItem.dbId &&
                    item.contactItem.deviceDbId == itemForCompare.contactItem.deviceDbId
        }

        fun areTheSameContents(item: SectionBlacklistItem,
                               itemForCompare: SectionBlacklistItem.Contact) : Boolean{
            val firstItem = (item as SectionBlacklistItem.Contact).contactItem
            val secondItem = itemForCompare.contactItem
            return firstItem.name == secondItem.name &&
                    firstItem.photoUrl == secondItem.photoUrl &&
                    firstItem.withNonIgnoredNumbers == secondItem.withNonIgnoredNumbers
        }
    }

    companion object {
        const val ITEM_SECTION_HEADER_TYPE = 1
        const val ITEM_CONTACT_TYPE = 2
        const val ITEM_PHONE_NUMBER_TYPE = 3
    }
}
