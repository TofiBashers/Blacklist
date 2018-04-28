package com.gmail.tofibashers.blacklist.data.datasource

import com.gmail.tofibashers.blacklist.data.memory.MemoryActivityInterval
import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistContactItem
import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistItem
import com.gmail.tofibashers.blacklist.data.memory.MemoryWhitelistContactItem
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 27.01.2018.
 */

@Singleton
class MemoryDatasource
@Inject
constructor() : IMemoryDatasource {

    @Volatile
    private var selectedBlacklistItem: MemoryBlacklistItem? = null

    @Volatile
    private var selectedBlacklistContactItem: MemoryBlacklistContactItem? = null

    @Volatile
    private var selectedWhitelistContactItem: MemoryWhitelistContactItem? = null

    @Volatile
    private var selectedActivityIntervals: List<MemoryActivityInterval>? = null

    @Volatile
    private var selectedMultipleActivityIntervalsLists: List<List<MemoryActivityInterval>>? = null

    @Volatile
    private var selectedInteractionMode: InteractionMode? = null

    override fun removeSelectedActivityIntervals(): Completable =
            Completable.fromAction { synchronized(this, { selectedActivityIntervals = null }) }

    override fun getSelectedActivityIntervals(): Maybe<List<MemoryActivityInterval>> =
            Maybe.defer { synchronized(this, {
                return@synchronized if (selectedActivityIntervals == null) Maybe.empty<List<MemoryActivityInterval>>()
                else Maybe.just(selectedActivityIntervals) })
            }

    override fun putSelectedActivityIntervals(activityIntervals: List<MemoryActivityInterval>): Completable =
            Completable.fromAction { synchronized(this, {
                selectedActivityIntervals = activityIntervals
            }) }


    override fun removeSelectedMultipleActivityIntervalsLists(): Completable =
            Completable.fromAction { synchronized(this, { selectedMultipleActivityIntervalsLists = null }) }

    override fun getSelectedMultipleActivityIntervalsLists(): Maybe<List<List<MemoryActivityInterval>>> =
            Maybe.defer { synchronized(this, {
                return@synchronized if (selectedMultipleActivityIntervalsLists == null) Maybe.empty<List<List<MemoryActivityInterval>>>()
                else Maybe.just(selectedMultipleActivityIntervalsLists) })
            }

    override fun putSelectedMultipleActivityIntervalsLists(activityIntervalsLists: List<List<MemoryActivityInterval>>): Completable =
            Completable.fromAction { synchronized(this, {
                selectedMultipleActivityIntervalsLists = activityIntervalsLists
            }) }

    override fun removeSelectedBlackListItem(): Completable =
            Completable.fromAction {
                synchronized(this, {
                    selectedBlacklistItem = null
                })
            }

    override fun getSelectedBlackListItem(): Maybe<MemoryBlacklistItem> =
            Maybe.defer { synchronized(this, {
                if (selectedBlacklistItem == null) return@synchronized Maybe.empty<MemoryBlacklistItem>()
                else{ return@synchronized Maybe.just(selectedBlacklistItem)
                }
            }) }

    override fun putSelectedBlackListItem(blacklistItem: MemoryBlacklistItem): Completable =
            Completable.fromAction { synchronized(this, {
                selectedBlacklistItem = blacklistItem
            }) }

    override fun removeSelectedContactItem(): Completable =
            Completable.fromAction {
                synchronized(this, {
                    selectedWhitelistContactItem = null
                })
            }

    override fun getSelectedContactItem(): Maybe<MemoryWhitelistContactItem> =
            Maybe.defer { synchronized(this, {
                if (selectedWhitelistContactItem == null) return@synchronized Maybe.empty<MemoryWhitelistContactItem>()
                else{ return@synchronized Maybe.just(selectedWhitelistContactItem) }
            }) }

    override fun putSelectedContactItem(whitelistContactItem: MemoryWhitelistContactItem): Completable =
            Completable.fromAction { synchronized(this, {
                selectedWhitelistContactItem = whitelistContactItem
            }) }

    override fun removeSelectedBlacklistContactItem(): Completable =
            Completable.fromAction {
                synchronized(this, {
                    selectedBlacklistContactItem = null
                })
            }

    override fun getSelectedBlacklistContactItem(): Maybe<MemoryBlacklistContactItem> =
            Maybe.defer { synchronized(this, {
                if (selectedBlacklistContactItem == null) return@synchronized Maybe.empty<MemoryBlacklistContactItem>()
                else{ return@synchronized Maybe.just(selectedBlacklistContactItem) }
            }) }

    override fun putSelectedBlacklistContactItem(contactItem: MemoryBlacklistContactItem): Completable =
            Completable.fromAction { synchronized(this, {
                selectedBlacklistContactItem = contactItem
            }) }

    override fun getSelectedMode(): Maybe<InteractionMode> =
            Maybe.defer { synchronized(this, {
                return@synchronized if (selectedInteractionMode == null) Maybe.empty<InteractionMode>()
                else Maybe.just(selectedInteractionMode) })
            }

    override fun getAndRemoveSelectedMode(): Maybe<InteractionMode> =
            Maybe.defer { synchronized(this, {
                if (selectedInteractionMode == null)
                    return@synchronized Maybe.empty<InteractionMode>()
                else{
                    val retMode = selectedInteractionMode
                    selectedInteractionMode = null
                    return@synchronized Maybe.just(retMode)
                }
            }) }

    override fun removeSelectedMode(): Completable =
            Completable.fromAction { synchronized(this, {
                    selectedInteractionMode = null
            }) }

    override fun putSelectedMode(interactionMode: InteractionMode): Completable =
            Completable.fromAction { synchronized(this, {
                selectedInteractionMode = interactionMode
            }) }
}