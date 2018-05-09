package com.gmail.tofibashers.blacklist.data.datasource

import com.gmail.tofibashers.blacklist.data.device.DeviceContactItem
import com.gmail.tofibashers.blacklist.data.device.DeviceContactPhoneItem
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single


/**
 * Created by TofiBashers on 01.05.2018.
 */
interface IDeviceDatasource {

    /**
     * @return Result [Maybe] with [DeviceContactItem] if exists
     * Result [Maybe] doesn't specify any schedulers.
     */
    fun getDeviceContactByIdAndLookupKey(id: Long? = null, lookupkey: String? = null): Maybe<DeviceContactItem>

    /**
     * Get contacts, without all of [excludeForSelectionContacts],
     * sorted result by [DeviceContactItem.name] ascending.
     * @return [Flowable] that emits after subscribe, and after all changes
     * Result [Flowable] doesn't modify backpressure strategies and schedulers, never calls onComplete().
     */
    fun getAllContactsExcludeSortedByNameAscWithChanges(excludeForSelectionContacts: List<DeviceContactItem>): Flowable<List<DeviceContactItem>>

    /**
     * Get contactPhones by [deviceContactItem], without all of [excludeForSelectionContactPhones],
     * sorted result by [DeviceContactPhoneItem.number]
     * Result [Single] doesn't modify any schedulers.
     */
    fun getContactPhonesByDeviceContactExcludeSortedByNumberAsc(deviceContactItem: DeviceContactItem,
                                                                excludeForSelectionContactPhones: List<DeviceContactPhoneItem>): Single<List<DeviceContactPhoneItem>>

    /**
     * Get contactPhones by [deviceContactItem], without all of [excludeForSelectionContactPhones].
     * Result [Single] doesn't modify any schedulers.
     */
    fun getAllContactPhonesByDeviceContactExclude(deviceContactItem: DeviceContactItem,
                                                  excludeForSelectionContactPhones: List<DeviceContactPhoneItem>): Single<List<DeviceContactPhoneItem>>

    /**
     * Get count of contactPhones of [deviceContactItem], excluding count of [excludeForSelectionContactPhones].
     * @return [Single] with count
     * Result [Single] doesn't modify any schedulers.
     */
    fun getContactPhonesCountByDeviceContactExclude(deviceContactItem: DeviceContactItem,
                                                    excludeForSelectionContactPhones: List<DeviceContactPhoneItem>): Single<Int>

    /**
     * Get contactPhone, with [DeviceContactPhoneItem.id] and [DeviceContactPhoneItem.number].
     * @return [Single] with count
     * Result [Single] doesn't modify any schedulers.
     */
    fun getDeviceContactPhoneWithIdAndNumber(id: Long? = null, number: String): Maybe<DeviceContactPhoneItem>

    /**
     * @return [Flowable] that init value instantly after subscribe, and then after any change in datasource.
     * Result [Flowable] implements [BackpressureStrategy.DROP], doesn't specify any schedulers, never calls onComplete()
     */
    fun getInitialAndAnyDeviceDataChangesSignal() : Flowable<Unit>
}