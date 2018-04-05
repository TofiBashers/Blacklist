package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.data.repo.IPreferencesData
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 08.02.2018.
 */
@Singleton
class SaveIgnoreHiddenNumbersSyncUseCase
@Inject
constructor(
        private val preferencesData: IPreferencesData
): ISaveIgnoreHiddenNumbersSyncUseCase {

    override fun build(ignoreHiddenNumbers: Boolean): Completable =
            preferencesData.setIgnoreHiddenNumbers(ignoreHiddenNumbers)
}