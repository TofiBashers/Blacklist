package com.gmail.tofibashers.blacklist.data.datasource

import android.util.Log
import com.gmail.tofibashers.blacklist.data.memory.MemoryActivityInterval
import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistItem
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
    private var selectedActivityIntervals: List<MemoryActivityInterval>? = null

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

    override fun removeSelectedBlackListItem(): Completable =
            Completable.fromAction {
                Log.d("lol", "lol")
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