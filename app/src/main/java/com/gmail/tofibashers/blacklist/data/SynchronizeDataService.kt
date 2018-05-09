package com.gmail.tofibashers.blacklist.data

import android.content.Intent
import android.os.IBinder
import dagger.android.DaggerService
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject


/**
 * Created by TofiBashers on 05.05.2018.
 */
class SynchronizeDataService : DaggerService(){

    @Inject
    lateinit var synchronizeDataUseCase: ISynchronizeDataUseCase

    var synchronizeDataCaseDisposable: Disposable? = null

    override fun onCreate() {
        super.onCreate()
        synchronizeDataUseCase.buildSyncOnSubscribeAndAfterAllChanges()
                .subscribe(SynchronizeObserver())
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        synchronizeDataCaseDisposable?.dispose()
    }

    inner class SynchronizeObserver : CompletableObserver{

        override fun onSubscribe(d: Disposable) { synchronizeDataCaseDisposable = d }

        override fun onComplete() {}

        override fun onError(e: Throwable) {
            throw RuntimeException(e)
        }

    }
}