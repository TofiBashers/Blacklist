package com.gmail.tofibashers.blacklist.ui.common

import android.arch.lifecycle.ViewModel
import android.support.annotation.CallSuper
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * Created by TofiBashers on 14.01.2018.
 */
abstract class DisposableViewModel: ViewModel(){

    protected val requestsDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        requestsDisposable.clear()
    }

    fun disableAllRequests() = requestsDisposable.clear()

    abstract inner class DisposableSavingSingleObserver<T> : SingleObserver<T> {

        @CallSuper
        override fun onSubscribe(d: Disposable) {
            requestsDisposable.add(d)
        }
    }

    abstract inner class DisposableSavingCompletableObserver : CompletableObserver {

        @CallSuper
        override fun onSubscribe(d: Disposable) {
            requestsDisposable.add(d)
        }
    }

    abstract inner class DisposableSavingObserver<T> : Observer<T> {

        @CallSuper
        override fun onSubscribe(d: Disposable) {
            requestsDisposable.add(d)
        }
    }
}