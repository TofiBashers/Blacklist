package com.gmail.tofibashers.blacklist.di.app

import android.arch.lifecycle.ViewModel

import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Created by TofiBashers on 20.01.2018.
 */
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
