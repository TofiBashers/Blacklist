package com.gmail.tofibashers.blacklist.domain

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 22.01.2018.
 */
@Singleton
class DeleteAllSelectionsUseCase
@Inject
constructor(
        private val deleteAllSelectionsSyncUseCase: IDeleteAllSelectionsSyncUseCase
) : IDeleteAllSelectionsUseCase {

    override fun build(): Completable {
        return deleteAllSelectionsSyncUseCase.build()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}