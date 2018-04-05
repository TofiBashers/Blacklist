package com.gmail.tofibashers.blacklist.data.repo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by TofiBashers on 29.01.2018.
 */
@Singleton
class PreferencesData
@Inject
constructor(private val appContext: Context) : IPreferencesData {

    private val ignoreHiddenNumbersProcessor = BehaviorRelay.create<Boolean>()
    private val ignoreHiddeNumbersChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if(key == PREF_KEY_IGNORE_HIDDEN_NUMBERS){
            Log.i(LOG_TAG, "Shared preference changed")
            ignoreHiddenNumbersProcessor.accept(sharedPreferences.getBoolean(
                    PREF_KEY_IGNORE_HIDDEN_NUMBERS, PREF_DEFAULT_IGNORE_HIDDEN_NUMBERS))
        }
    }

    init {
        val preferences: SharedPreferences = appContext.getSharedPreferences(
                PREF_NAME_PREFERENCES, Context.MODE_PRIVATE)
        preferences.registerOnSharedPreferenceChangeListener( ignoreHiddeNumbersChangeListener )
    }

    override fun getIgnoreHiddenNumbersWithChanges(): Flowable<Boolean> {
        return Flowable.defer( {
            val currentVal = appContext.getSharedPreferences(PREF_NAME_PREFERENCES, Context.MODE_PRIVATE)
                    .getBoolean(PREF_KEY_IGNORE_HIDDEN_NUMBERS, PREF_DEFAULT_IGNORE_HIDDEN_NUMBERS)
            ignoreHiddenNumbersProcessor.accept(currentVal)
            return@defer ignoreHiddenNumbersProcessor.toFlowable(BackpressureStrategy.LATEST)
        })
    }

    override fun getIgnoreHiddenNumbers(): Single<Boolean> {
        return Single.fromCallable {
            appContext.getSharedPreferences(PREF_NAME_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(PREF_KEY_IGNORE_HIDDEN_NUMBERS, PREF_DEFAULT_IGNORE_HIDDEN_NUMBERS)
        }
    }

    override fun setIgnoreHiddenNumbers(ignoreHiddenNumber: Boolean): Completable {
        return Completable.fromCallable( {
            appContext.getSharedPreferences(
                    PREF_NAME_PREFERENCES, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(PREF_KEY_IGNORE_HIDDEN_NUMBERS, ignoreHiddenNumber)
                    .apply()
        })
    }


    override fun getIsFirstTimeLaunchBeforeKitkat(): Single<Boolean> {
        return Single.fromCallable {
            appContext.getSharedPreferences(PREF_NAME_PREFERENCES, Context.MODE_PRIVATE)
                    .getBoolean(PREF_IS_FIRST_TIME_LAUNCH_BEFORE_KITKAT, PREF_DEFAULT_IS_FIRST_TIME_LAUNCH_BEFORE_KITKAT)
        }
    }

    override fun setIsFirstTimeLaunchBeforeKitkatFalse(): Completable {
        return Completable.fromCallable( {
            appContext.getSharedPreferences(
                    PREF_NAME_PREFERENCES, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(PREF_IS_FIRST_TIME_LAUNCH_BEFORE_KITKAT, false)
                    .apply()
        })
    }

    override fun getIsFirstTimeLaunchOnKitkatOrHigher(): Single<Boolean> {
        return Single.fromCallable {
            appContext.getSharedPreferences(PREF_NAME_PREFERENCES, Context.MODE_PRIVATE)
                    .getBoolean(PREF_IS_FIRST_TIME_LAUNCH_ON_KITKAT_OR_HIGHER, PREF_DEFAULT_IS_FIRST_TIME_LAUNCH_ON_KITKAT_OR_HIGHER)
        }
    }

    override fun setIsFirstTimeLaunchOnKitkatOrHigherFalse(): Completable {
        return Completable.fromCallable( {
            appContext.getSharedPreferences(
                    PREF_NAME_PREFERENCES, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(PREF_IS_FIRST_TIME_LAUNCH_ON_KITKAT_OR_HIGHER, false)
                    .apply()
        })
    }

    companion object {
        private val LOG_TAG = PreferencesData::class.simpleName

        private const val PREF_NAME_PREFERENCES = "privatePreferences"
        private const val PREF_KEY_IGNORE_HIDDEN_NUMBERS = "ignoreHiddenNumbers"
        private const val PREF_IS_FIRST_TIME_LAUNCH_BEFORE_KITKAT = "isFirstTimeLaunchBeforeKitkat"
        private const val PREF_IS_FIRST_TIME_LAUNCH_ON_KITKAT_OR_HIGHER = "isFirstTimeOnKitkatOrHigher"

        private const val PREF_DEFAULT_IGNORE_HIDDEN_NUMBERS = false
        private const val PREF_DEFAULT_IS_FIRST_TIME_LAUNCH_BEFORE_KITKAT = true
        private const val PREF_DEFAULT_IS_FIRST_TIME_LAUNCH_ON_KITKAT_OR_HIGHER = true
    }

}