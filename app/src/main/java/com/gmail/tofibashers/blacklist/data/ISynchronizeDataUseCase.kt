package com.gmail.tofibashers.blacklist.data

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * This case synchronized database and contacts data.
 * Created by TofiBashers on 01.05.2018.
 */
interface ISynchronizeDataUseCase {

    /**
     * @return [Completable] that never completes.
     * Result [Completable] executes in [Schedulers.IO], provides result to Android UI-thread
     */
    fun buildSyncOnSubscribeAndAfterAllChanges(): Completable
}