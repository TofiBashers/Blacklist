package com.gmail.tofibashers.blacklist

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement


/**
 * Created by TofiBashers on 12.02.2018.
 */
class RxSchedulersOverrideRule : TestRule {


    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement(){
            override fun evaluate() {
                RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
                RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
                RxJavaPlugins.setSingleSchedulerHandler { Schedulers.trampoline() }
                RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
                RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
                try{
                    base.evaluate()
                }
                finally{
                    RxAndroidPlugins.reset()
                    RxJavaPlugins.reset()
                }
            }
        }
    }
}