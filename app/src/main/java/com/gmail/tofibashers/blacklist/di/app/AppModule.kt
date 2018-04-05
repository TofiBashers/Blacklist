package com.gmail.tofibashers.blacklist.di.app

import android.content.Context
import com.gmail.tofibashers.blacklist.utils.ISystemDateTimeProvider
import com.gmail.tofibashers.blacklist.utils.SystemDateTimeProvider
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerApplication
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.01.2018.
 */
@Module
class AppModule {

    @Singleton
    @Provides
    fun provideAppContext(daggerApplication: DaggerApplication): Context = daggerApplication.applicationContext

    @Singleton
    @Provides
    fun provideISystemTimeProvider() : ISystemDateTimeProvider = SystemDateTimeProvider()
}