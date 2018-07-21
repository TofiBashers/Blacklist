package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.InteractionMode
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase selects one list of [ActivityInterval] from multiple phones lists in model for edition.
 * Created by TofiBashers on 19.04.2018.
 */
interface ISelectForEditActivityIntervalsOfBlacklistContactPhoneUseCase {

   /**
    * @param phonePosition position in selected list of [ActivityInterval]
    * @return [Completable] that completes after success operation.
    * Result [Completable] subscribes and executes in [Schedulers.IO], provides result to Android UI-thread
    */
   fun build(phonePosition: Int) : Completable
}