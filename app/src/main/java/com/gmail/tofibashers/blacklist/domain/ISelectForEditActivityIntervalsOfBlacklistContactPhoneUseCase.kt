package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase selects one list of [ActivityInterval] from multiple intervals lists in model for edition. If multiple intervals not selected,
 * and [InteractionMode.CREATE], then creates multiple intervals with default settings.
 * Created by TofiBashers on 19.04.2018.
 */
interface ISelectForEditActivityIntervalsOfBlacklistContactPhoneUseCase {

   /**
    * @param intervalPosition position in selected list of [ActivityInterval]
    * @param totalIntervalsCount total count of created default intervals if their not selected
    * @return [Completable] that completes after success operation.
    * Result [Completable] subscribes and executes in [Schedulers.IO], provides result to Android UI-thread
    */
   fun build(intervalPosition: Int, totalIntervalsCount: Int) : Completable
}