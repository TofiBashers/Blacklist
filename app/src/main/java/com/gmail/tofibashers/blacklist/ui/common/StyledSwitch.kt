package com.gmail.tofibashers.blacklist.ui.common

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SwitchCompat
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import com.gmail.tofibashers.blacklist.R


/**
 * Created by TofiBashers on 29.06.2018.
 */
class StyledSwitch : SwitchCompat {

    constructor(context: Context) : super(ContextThemeWrapper(context, R.style.AppTheme_CompoundButton)) {
        setStyle()
    }

    constructor(context: Context, attrs: AttributeSet) : super(ContextThemeWrapper(context, R.style.AppTheme_CompoundButton), attrs) {
        setStyle()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ContextThemeWrapper(context, R.style.AppTheme_CompoundButton), attrs, defStyleAttr) {
        setStyle()
    }

    private fun setStyle() {
        isSaveEnabled = false
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
    }
}