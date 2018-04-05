package com.gmail.tofibashers.blacklist.ui.time_settings

import android.arch.lifecycle.MutableLiveData
import com.gmail.tofibashers.blacklist.domain.*
import com.gmail.tofibashers.blacklist.entity.MutableActivityIntervalsWithEnableAndValidState
import com.gmail.tofibashers.blacklist.entity.TimeChangeInitData
import com.gmail.tofibashers.blacklist.ui.common.DisposableViewModel
import com.gmail.tofibashers.blacklist.ui.common.SingleLiveEvent
import org.joda.time.LocalTime
import javax.inject.Inject


/**
 * Created by TofiBashers on 23.01.2018.
 */
class TimeSettingsViewModel
@Inject
constructor(
        private val getSelectedActivityIntervalsUseCase: IGetSelectedOrNewActivityIntervalsUseCase,
        private val selectActivityIntervalsUseCase: ISelectOnlyEnabledActivityIntervalsUseCase,
        private val setDisableActivityIntervalUseCase: ISetDisableActivityIntervalAndValidateToSaveSyncUseCase,
        private val setEnableActivityIntervalUseCase: ISetEnableActivityIntervalAndValidateToSaveSyncUseCase,
        private val createTimeChangeInitDataUseCase: ICreateTimeChangeInitDataUseCase,
        private val loadingViewStateFactory: TimeSettingsViewState_LoadingViewStateFactory,
        private val dataViewStateFactory: TimeSettingsViewState_DataViewStateFactory,
        private val itemDetailsRouteFactory: TimeSettingsNavData_ItemDetailsRouteFactory,
        private val timeChangeRouteFactory: TimeSettingsNavData_TimeChangeRouteFactory,
        val viewStateData: MutableLiveData<TimeSettingsViewState>,
        val navigationData: SingleLiveEvent<TimeSettingsNavData>
): DisposableViewModel() {

    private var orderedIntervalsWithStates: MutableActivityIntervalsWithEnableAndValidState? = null

    fun onInitGetItem(){
        viewStateData.value = loadingViewStateFactory.create()
        getSelectedActivityIntervalsUseCase.build()
                .subscribe(GetSelectedIntervalsObserver())
    }

    fun onInitCancel(){
        requestsDisposable.clear()
        navigationData.value = itemDetailsRouteFactory.create()
    }

    fun onInitSave(){
        viewStateData.value = loadingViewStateFactory.create()
        selectActivityIntervalsUseCase.build(orderedIntervalsWithStates!!)
                .subscribe(SelectObserver())
    }

    fun onInitChangeEnableState(index: Int, isEnabled: Boolean){
        if(isEnabled){
            setEnableActivityIntervalUseCase.build(index, orderedIntervalsWithStates!!)
                    .subscribe(ChangeEnabledStateObserver())
        }
        else{
            setDisableActivityIntervalUseCase.build(index, orderedIntervalsWithStates!!)
                    .subscribe(ChangeEnabledStateObserver())
        }
    }

    fun onInitChangeTime(isBeginTime: Boolean, position: Int) {
        viewStateData.value = loadingViewStateFactory.create()
        createTimeChangeInitDataUseCase.build(isBeginTime, position, orderedIntervalsWithStates!!)
                .subscribe(CreateTimeChangeInitDataObserver())
    }

    fun onTimeChanged(time: LocalTime, isBeginTime: Boolean, index: Int){
        val activityInterval = orderedIntervalsWithStates!![index].second
        if(isBeginTime) activityInterval.beginTime = time
        else activityInterval.endTime = time
        viewStateData.value = dataViewStateFactory.create(orderedIntervalsWithStates)
    }

    private inner class GetSelectedIntervalsObserver : DisposableSavingSingleObserver<MutableActivityIntervalsWithEnableAndValidState>() {

        override fun onSuccess(intervals: MutableActivityIntervalsWithEnableAndValidState) {
            orderedIntervalsWithStates = intervals
            viewStateData.value = dataViewStateFactory.create(orderedIntervalsWithStates)
        }

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

    private inner class CreateTimeChangeInitDataObserver : DisposableSavingSingleObserver<TimeChangeInitData>() {

        override fun onSuccess(data: TimeChangeInitData) {
            viewStateData.value = dataViewStateFactory.create(orderedIntervalsWithStates)
            navigationData.value = timeChangeRouteFactory.create(data)
        }

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

    private inner class SelectObserver : DisposableSavingCompletableObserver() {

        override fun onComplete() {
            navigationData.value = itemDetailsRouteFactory.create()
        }

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

    private inner class ChangeEnabledStateObserver : DisposableSavingSingleObserver<MutableActivityIntervalsWithEnableAndValidState>() {

        override fun onSuccess(intervals: MutableActivityIntervalsWithEnableAndValidState) {
            orderedIntervalsWithStates = intervals
            viewStateData.value = dataViewStateFactory.create(orderedIntervalsWithStates)
        }

        override fun onError(error: Throwable) {
            throw RuntimeException(error)
        }
    }

}