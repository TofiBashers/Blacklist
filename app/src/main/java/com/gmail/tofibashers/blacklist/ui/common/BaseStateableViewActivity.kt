package com.gmail.tofibashers.blacklist.ui.common

import android.support.constraint.Group
import android.view.View


/**
 * Created by TofiBashers on 17.01.2018.
 */
abstract class BaseStateableViewActivity: BaseAppCompatActivity() {

    protected enum class ViewState{
        DATA,
        LOADING
    }

    abstract val loadingGroup: Group
    abstract val dataGroup: Group

    protected fun showDefaultLoading() = setViewState(ViewState.LOADING)

    protected fun setViewState(state: ViewState){
        when (state){
            ViewState.DATA -> {
                loadingGroup.visibility = View.GONE
                dataGroup.visibility = View.VISIBLE
            }
            ViewState.LOADING -> {
                dataGroup.visibility = View.GONE
                loadingGroup.visibility = View.VISIBLE
            }
        }
    }
}