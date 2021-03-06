package com.gmail.tofibashers.blacklist.data.datasource

import com.gmail.tofibashers.blacklist.data.memory.*
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
    private var selectedBlacklistPhoneNumberItem: MemoryBlacklistPhoneNumberItem? = null

    @Volatile
    private var selectedBlacklistContactItem: MemoryBlacklistContactItem? = null

    @Volatile
    private var selectedWhitelistContactItem: MemoryWhitelistContactItem? = null

    @Volatile
    private var selectedWhitelistContactPhones: List<MemoryWhitelistContactPhone>? = null

    @Volatile
    private var selectedActivityIntervals: List<MemoryActivityInterval>? = null

    @Volatile
    private var selectedBlacklistContactPhonesWithActivityIntervals: List<MemoryBlacklistContactPhoneWithActivityIntervals>? = null

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

    override fun removeSelectedBlacklistContactPhonesWithActivityIntervals(): Completable =
            Completable.fromAction { synchronized(this, { selectedBlacklistContactPhonesWithActivityIntervals = null }) }

    override fun getSelectedBlacklistContactPhonesWithActivityIntervals(): Maybe<List<MemoryBlacklistContactPhoneWithActivityIntervals>> =
            Maybe.defer { synchronized(this, {
                return@synchronized if (selectedBlacklistContactPhonesWithActivityIntervals == null) Maybe.empty<List<MemoryBlacklistContactPhoneWithActivityIntervals>>()
                else Maybe.just(selectedBlacklistContactPhonesWithActivityIntervals) })
            }

    override fun putSelectedBlacklistContactPhonesWithActivityIntervals(phonesWithIntervals: List<MemoryBlacklistContactPhoneWithActivityIntervals>): Completable =
            Completable.fromAction { synchronized(this, {
                selectedBlacklistContactPhonesWithActivityIntervals = phonesWithIntervals
            }) }

    override fun removeSelectedBlacklistPhoneNumberItem(): Completable =
            Completable.fromAction {
                synchronized(this, {
                    selectedBlacklistPhoneNumberItem = null
                })
            }

    override fun getSelectedBlacklistPhoneNumberItem(): Maybe<MemoryBlacklistPhoneNumberItem> =
            Maybe.defer { synchronized(this, {
                if (selectedBlacklistPhoneNumberItem == null) return@synchronized Maybe.empty<MemoryBlacklistPhoneNumberItem>()
                else{ return@synchronized Maybe.just(selectedBlacklistPhoneNumberItem)
                }
            }) }

    override fun putSelectedBlacklistPhoneNumberItem(blacklistPhoneNumberItem: MemoryBlacklistPhoneNumberItem): Completable =
            Completable.fromAction { synchronized(this, {
                selectedBlacklistPhoneNumberItem = blacklistPhoneNumberItem
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

    override fun removeSelectedWhitelistContactPhones(): Completable =
            Completable.fromAction {
                synchronized(this, {
                    selectedWhitelistContactPhones = null
                })
            }

    override fun getSelectedWhitelistContactPhones(): Maybe<List<MemoryWhitelistContactPhone>> =
            Maybe.defer { synchronized(this, {
                if (selectedWhitelistContactPhones == null) return@synchronized Maybe.empty<List<MemoryWhitelistContactPhone>>()
                else{ return@synchronized Maybe.just(selectedWhitelistContactPhones) }
            }) }

    override fun putSelectedWhitelistContactPhones(whitelistContactPhones: List<MemoryWhitelistContactPhone>): Completable =
            Completable.fromAction { synchronized(this, {
                selectedWhitelistContactPhones = whitelistContactPhones
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