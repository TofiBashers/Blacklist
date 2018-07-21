package com.gmail.tofibashers.blacklist.di.app

import android.content.ContentResolver
import android.content.Context
import com.gmail.tofibashers.blacklist.data.datasource.DeviceDatasource
import com.gmail.tofibashers.blacklist.data.datasource.IDeviceDatasource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by TofiBashers on 04.05.2018.
 */
@Module
class DeviceDataModule {

    @Singleton
    @Provides
    fun provideContentResolver(context: Context): ContentResolver = context.contentResolver

    @Singleton
    @Provides
    fun provideIDeviceDataSource(deviceDatasource: DeviceDatasource) : IDeviceDatasource = deviceDatasource
}