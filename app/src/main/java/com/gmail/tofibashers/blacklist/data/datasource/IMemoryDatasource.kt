package com.gmail.tofibashers.blacklist.data.datasource

import com.gmail.tofibashers.blacklist.data.memory.MemoryActivityInterval
import com.gmail.tofibashers.blacklist.data.memory.MemoryBlacklistItem
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.Maybe


/**
 * This class provides a simple storage data in app's RAM
 * Created by TofiBashers on 27.01.2018.
 */
interface IMemoryDatasource {

    fun removeSelectedActivityIntervals(): Completable
    fun getSelectedActivityIntervals(): Maybe<List<MemoryActivityInterval>>
    fun putSelectedActivityIntervals(activityIntervals: List<MemoryActivityInterval>): Completable
    fun removeSelectedBlackListItem(): Completable
    fun getSelectedBlackListItem(): Maybe<MemoryBlacklistItem>
    fun putSelectedBlackListItem(blacklistItem: MemoryBlacklistItem): Completable
    fun getSelectedMode(): Maybe<InteractionMode>
    fun getAndRemoveSelectedMode(): Maybe<InteractionMode>
    fun removeSelectedMode() : Completable
    fun putSelectedMode(interactionMode: InteractionMode): Completable
}