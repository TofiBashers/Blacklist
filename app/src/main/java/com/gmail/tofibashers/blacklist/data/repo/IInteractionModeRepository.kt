package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.Maybe


/**
 * Created by TofiBashers on 16.01.2018.
 */
interface IInteractionModeRepository {
    fun getSelectedMode(): Maybe<InteractionMode>
    fun removeSelectedMode() : Completable
    fun putSelectedMode(interactionMode: InteractionMode): Completable
}