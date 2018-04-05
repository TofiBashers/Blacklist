package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.data.datasource.IMemoryDatasource
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 21.01.2018.
 */
@Singleton
class InteractionModeRepository
@Inject
constructor(private val memoryDatasource: IMemoryDatasource) : IInteractionModeRepository {

    override fun getSelectedMode(): Maybe<InteractionMode> = memoryDatasource.getSelectedMode()

    override fun removeSelectedMode(): Completable = memoryDatasource.removeSelectedMode()

    override fun putSelectedMode(interactionMode: InteractionMode): Completable = memoryDatasource.putSelectedMode(interactionMode)
}