package com.gmail.tofibashers.blacklist.domain

import com.gmail.tofibashers.blacklist.entity.BlacklistItem
import io.reactivex.Single


/**
 * This UseCase validate [BlacklistItem] for ability to save.
 * Created by TofiBashers on 05.02.2018.
 */
interface IValidateBlacklistItemForSaveSyncUseCase {

    /**
     * Result [Single] subscribes and provides result in Android UI-Thread
     */
    fun build(item: BlacklistItem) : Single<Boolean>
}