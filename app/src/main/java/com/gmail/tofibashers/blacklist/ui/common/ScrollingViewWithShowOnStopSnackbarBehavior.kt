package com.gmail.tofibashers.blacklist.ui.common

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import com.leinardi.android.speeddial.SpeedDialView


/**
 * Created by TofiBashers on 30.06.2018.
 */
class ScrollingViewWithShowOnStopSnackbarBehavior : SpeedDialView.ScrollingViewSnackbarBehavior {

    constructor(): super()

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        if(child.visibility != View.VISIBLE){
            show(child)
        }
    }
}