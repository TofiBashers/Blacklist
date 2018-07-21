package com.gmail.tofibashers.blacklist.data.repo

import com.gmail.tofibashers.blacklist.entity.BlacklistContactItem
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneWithActivityIntervals
import io.reactivex.Single


/**
 * Created by TofiBashers on 09.05.2018.
 */
interface IBlacklistContactPhoneWithActivityIntervalsRepository : IBaseListSelectionOperationRepository<BlacklistContactPhoneWithActivityIntervals>{

    /**
     * @return list of [BlacklistContactPhoneWithActivityIntervals], associated with [item], without any sorting.
     * Result [Single] doesn't modify any schedulers.
     */
    fun getAllAssociatedWithBlacklistContact(item: BlacklistContactItem) : Single<List<BlacklistContactPhoneWithActivityIntervals>>
}