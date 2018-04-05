package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.GetBlacklistResult
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


/**
 * This UseCase provides all elements of blacklist firstly, and after any changes.
 * Created by TofiBashers on 16.01.2018.
 */
interface IGetBlacklistItemsSortByNumberWithIgnoreHiddenUseCase {

    /**
     * Result [Observable] executes at [Schedulers.IO], provides result to android UI thread,
     * never calls onComplete().
     */
    fun build() : Observable<GetBlacklistResult>
}