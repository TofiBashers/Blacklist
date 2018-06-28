package com.gmail.tofibashers.blacklist.di.app

import android.content.Context
import android.telephony.TelephonyManager
import com.gmail.tofibashers.blacklist.utils.ISystemDateTimeProvider
import com.gmail.tofibashers.blacklist.utils.SystemDateTimeProvider
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerApplication
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
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

    @Singleton
    @Provides
    fun provideTelephonyManager(appContext: Context) : TelephonyManager =
            appContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    @Singleton
    @Provides
    fun providePhoneNumberUtil(appContext: Context) : PhoneNumberUtil =
            PhoneNumberUtil.createInstance(appContext)
}