package com.gmail.tofibashers.blacklist.ui.common

import android.view.View


/**
 * Created by TofiBashers on 17.01.2018.
 */
abstract class BaseStateableViewActivity<out L : View, out D : View>: BaseAppCompatActivity() {

    protected enum class ViewState{
        DATA,
        LOADING
    }

    abstract val loadingView: L
    abstract val dataView: D

    protected fun showDefaultLoading() = setViewState(ViewState.LOADING)

    protected open fun setViewState(state: ViewState){
        when (state){
            ViewState.DATA -> {
                loadingView.visibility = View.GONE
                dataView.visibility = View.VISIBLE
            }
            ViewState.LOADING -> {
                dataView.visibility = View.GONE
                loadingView.visibility = View.VISIBLE
            }
        }
    }
}