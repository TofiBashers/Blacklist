package com.gmail.tofibashers.blacklist.di.app

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import javax.inject.Singleton


/**
 * Created by TofiBashers on 20.01.2018.
 */

@Component(modules = arrayOf(
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        AndroidBindingsModule::class,
        ViewModelBindingsModule::class,
        LiveDataModule::class,
        UseCaseModule::class,
        RepositoryAndPreferenceDataModule::class,
        DatabaseModule::class,
        MemoryStorageModule::class,
        AppModule::class))
@Singleton
interface AppComponent : AndroidInjector<DaggerApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<DaggerApplication>()
}