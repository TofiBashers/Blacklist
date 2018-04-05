package com.gmail.tofibashers.blacklist.di.app

import com.gmail.tofibashers.blacklist.data.datasource.IMemoryDatasource
import com.gmail.tofibashers.blacklist.data.datasource.MemoryDatasource
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


/**
 * Created by TofiBashers on 28.01.2018.
 */

@Module
abstract class MemoryStorageModule {

    @Binds
    @Singleton
    abstract fun provideIMemoryDatasource(memoryDatasource: MemoryDatasource) : IMemoryDatasource
}