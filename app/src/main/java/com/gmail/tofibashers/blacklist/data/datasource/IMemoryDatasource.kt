package com.gmail.tofibashers.blacklist.data.datasource

import com.gmail.tofibashers.blacklist.data.memory.*
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
    fun removeSelectedBlacklistContactPhonesWithActivityIntervals(): Completable
    fun getSelectedBlacklistContactPhonesWithActivityIntervals(): Maybe<List<MemoryBlacklistContactPhoneWithActivityIntervals>>
    fun putSelectedBlacklistContactPhonesWithActivityIntervals(phonesWithIntervals: List<MemoryBlacklistContactPhoneWithActivityIntervals>): Completable
    fun removeSelectedBlackListItem(): Completable
    fun getSelectedBlackListItem(): Maybe<MemoryBlacklistItem>
    fun putSelectedBlackListItem(blacklistItem: MemoryBlacklistItem): Completable
    fun removeSelectedContactItem(): Completable
    fun getSelectedContactItem(): Maybe<MemoryWhitelistContactItem>
    fun putSelectedContactItem(whitelistContactItem: MemoryWhitelistContactItem): Completable
    fun removeSelectedWhitelistContactPhones(): Completable
    fun getSelectedWhitelistContactPhones(): Maybe<List<MemoryWhitelistContactPhone>>
    fun putSelectedWhitelistContactPhones(whitelistContactPhones: List<MemoryWhitelistContactPhone>): Completable
    fun removeSelectedBlacklistContactItem(): Completable
    fun getSelectedBlacklistContactItem(): Maybe<MemoryBlacklistContactItem>
    fun putSelectedBlacklistContactItem(contactItem: MemoryBlacklistContactItem): Completable
    fun getSelectedMode(): Maybe<InteractionMode>
    fun getAndRemoveSelectedMode(): Maybe<InteractionMode>
    fun removeSelectedMode() : Completable
    fun putSelectedMode(interactionMode: InteractionMode): Completable
}